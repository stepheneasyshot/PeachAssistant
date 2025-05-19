package com.stephen.aiassistant.vm

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephen.aiassistant.data.ChatRole
import com.stephen.aiassistant.data.ThemeState
import com.stephen.aiassistant.data.bean.FoodContent
import com.stephen.aiassistant.data.uistate.CalorieCalState
import com.stephen.aiassistant.data.uistate.ChatItem
import com.stephen.aiassistant.data.uistate.EnglishChatState
import com.stephen.aiassistant.data.uistate.ResultState
import com.stephen.aiassistant.helper.ThemeHelper
import com.stephen.aiassistant.network.DeepseekChatRepository
import com.stephen.aiassistant.network.DoubaoVisionRepository
import com.stephen.aiassistant.platform.copyTextToClipBoard
import com.stephen.aiassistant.platform.permissions.CAMERA
import com.stephen.aiassistant.platform.permissions.GALLERY
import com.stephen.aiassistant.platform.permissions.RECORD_AUDIO
import com.stephen.aiassistant.platform.permissions.STORAGE
import com.stephen.aiassistant.platform.permissions.WRITE_STORAGE
import com.stephen.permissions.DeniedAlwaysException
import com.stephen.permissions.DeniedException
import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionState
import com.stephen.permissions.PermissionsController
import com.stephen.permissions.RequestCanceledException
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainViewModel(
    private val deepSeekChatRepository: DeepseekChatRepository,
    private val douBaoVisionRepository: DoubaoVisionRepository,
    private val themeHelper: ThemeHelper
) : ViewModel() {

    init {
        Napier.i("=====>MainViewModel init<=======")
    }

    private val _copyWritingStateFlow = MutableStateFlow(ResultState())
    val copyWritingStateFlow = _copyWritingStateFlow.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText = _streamingText.asStateFlow()

    private val _galleryPermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val galleryPermissionState = _galleryPermissionStateFlow.asStateFlow()

    private val _microphonePermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val microphonePermissionState = _microphonePermissionStateFlow.asStateFlow()

    private val _cameraPermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val cameraPermissionState = _cameraPermissionStateFlow.asStateFlow()

    private val _storagePermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val storagePermissionState = _storagePermissionStateFlow.asStateFlow()
    private val _writeStoragePermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val writeStoragePermissionState = _writeStoragePermissionStateFlow.asStateFlow()

    // ai模型对话
    private val _aiModelChatListState = MutableStateFlow(EnglishChatState())
    val aiModelChatListStateFlow = _aiModelChatListState.asStateFlow()

    private val _foodListState = MutableStateFlow(CalorieCalState())
    val foodListStateFlow = _foodListState.asStateFlow()

    var settingsViewOpenState = MutableStateFlow(false)
        private set

    val themeState = themeHelper.themeStateStateFlow

    // 卡路里识别的网络请求任务，任务中断使用
    private var calorieUploadJob: Job? = null

    private lateinit var permissionsController: PermissionsController

    fun setPermissionController(controller: PermissionsController) {
        Napier.i("setPermissionController")
        this.permissionsController = controller
        viewModelScope.launch {
            _galleryPermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.GALLERY)
            }
            _microphonePermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.RECORD_AUDIO)
            }
            _cameraPermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.CAMERA)
            }
            _storagePermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.STORAGE)
            }
            _writeStoragePermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.WRITE_STORAGE)
            }
        }
    }

    fun openPermissionSettings() {
        permissionsController.openAppSettings()
    }

    /**
     * 相册浏览权限
     */
    fun provideOrRequestGalleryPermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.GALLERY)
                _galleryPermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _galleryPermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _galleryPermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 录音权限
     */
    fun provideOrRequestMicrophonePermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.RECORD_AUDIO)
                _microphonePermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _microphonePermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _microphonePermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 相机权限
     */
    fun provideOrRequestCameraPermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.CAMERA)
                _cameraPermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _cameraPermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _cameraPermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 存储权限
     */
    fun provideOrRequestStoragePermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.STORAGE)
                _storagePermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _storagePermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _storagePermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 写入存储权限
     */
    fun provideOrRequestWriteStoragePermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.WRITE_STORAGE)
                _writeStoragePermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _writeStoragePermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _writeStoragePermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    private val _imageTempByteArray = MutableStateFlow<ByteArray?>(null)
    val imageTempByteArrayState = _imageTempByteArray.asStateFlow()

    /**
     * 拍照缓存ByteArray
     */
    fun saveTempPicture(imageByteArray: ByteArray) {
        Napier.i("updateTempPicture")
        _imageTempByteArray.update {
            imageByteArray
        }
    }

    fun clearTempPicture() {
        Napier.i("clearTempPicture")
        _imageTempByteArray.update {
            null
        }
    }

    /**
     * 重选图片，取消上一个上传任务，立即重置加载状态
     */
    fun onReselectPicture() {
        calorieUploadJob?.cancel()
        _foodListState.update {
            it.copy(
                foodList = null,
                loading = false,
            )
        }
    }

    /**
     * 卡路里图片发送
     */
    fun calculateCalorieByAI(
        galleryImageFile: PlatformFile? = null,
        isUsingCameraTemp: Boolean? = null
    ) {
        Napier.i("calCalorieByAI -> galleryImageFile: ${galleryImageFile?.name}, isUsingCameraTempImage: $isUsingCameraTemp")
        _foodListState.update {
            it.copy(
                foodList = null,
                loading = true,
                errorMessage = ""
            )
        }
        _foodListState.value = _foodListState.value.toUiState()
        calorieUploadJob = viewModelScope.launch {
            runCatching {
                var imageFileType: String
                var imageFileBase64: String
                var calorieResult = ""
                galleryImageFile?.let {
                    imageFileType = galleryImageFile.name.substringAfterLast(".")
                    imageFileBase64 = galleryImageFile.readBytes().encodeBase64()
                    calorieResult = douBaoVisionRepository.calCalorieByAI(
                        imageFileType,
                        imageFileBase64
                    ).choices.first().message.content
                }
                isUsingCameraTemp?.let {
                    imageFileType = "jpg"
                    imageFileBase64 = imageTempByteArrayState.value!!.encodeBase64()
                    calorieResult = douBaoVisionRepository.calCalorieByAI(
                        imageFileType,
                        imageFileBase64
                    ).choices.first().message.content
                }
                val foodList = Json.decodeFromString<FoodContent>(calorieResult)
                Napier.i("calCalorieByAI -> result = $calorieResult")
                _foodListState.update {
                    it.copy(
                        foodList = foodList.foods,
                        loading = false,
                    )
                }
                _foodListState.value = _foodListState.value.toUiState()
            }.onFailure { e ->
                Napier.e(e.message.toString())
                _foodListState.update {
                    it.copy(
                        foodList = null,
                        loading = false,
                        errorMessage = e.message.toString()
                    )
                }
            }
        }
    }

    /**
     * 语音转文字
     */
    fun startToProcessSpeechToText() {
        Napier.i("startToProcessSpeechToText")
    }

    fun doEnglishTeacherTest() {
        _aiModelChatListState.update {
            it.copy(
                chatList = it.chatList + listOf(
                    ChatItem(
                        content = "Hi, I'm your personal English teacher. How can I help you today?",
                        role = ChatRole.ASSISTANT
                    )
                ),
                listSize = it.listSize + 1
            )
        }
    }

    /**
     * AI模型 英语口语对话
     */
    fun englishSpeakingExercise(text: String) {
        Napier.i("chatWithAI -> text = $text")
        // 用户输入的内容
        _aiModelChatListState.update {
            it.copy(
                chatList = it.chatList + listOf(ChatItem(content = text, role = ChatRole.USER)),
                listSize = it.listSize + 1
            )
        }
        _aiModelChatListState.value = _aiModelChatListState.value.toUiState()
        // 在线call，等待AI模型生成回复
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val aiResult = deepSeekChatRepository.englishExerciseChat(chat = text)
                    .choices.first().message.content
                Napier.i("englishSpeakingExercise -> aiResult = $aiResult")
                // 回复的内容
                _aiModelChatListState.update {
                    it.copy(
                        chatList = it.chatList + listOf(
                            ChatItem(
                                content = aiResult,
                                role = ChatRole.ASSISTANT
                            )
                        ),
                        listSize = it.listSize + 1
                    )
                }
                _aiModelChatListState.value = _aiModelChatListState.value.toUiState()
            }.onFailure { e ->
                Napier.e(e.message.toString())
                // 回复的内容
                _aiModelChatListState.update {
                    it.copy(
                        chatList = it.chatList + listOf(
                            ChatItem(
                                content = "Server error, please try again later.",
                                role = ChatRole.ASSISTANT
                            )
                        ),
                        listSize = it.listSize + 1
                    )
                }
                _aiModelChatListState.value = _aiModelChatListState.value.toUiState()
            }
        }
    }

    /**
     * 社交圈文案生成
     */
    fun generateCopyWriting(theme: String, style: String) {
        Napier.i("generateCopyWriting -> theme = $theme, style = $style")
        _copyWritingStateFlow.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            runCatching {
                val request =
                    "帮我生成一段社交软件使用的文案，主题是：$theme，风格是：$style，字数在100字左右"
                val result = deepSeekChatRepository.generateCopyWritingByAI(request)
                Napier.d("chatWithAI -> result = $result")
                _copyWritingStateFlow.update {
                    it.copy(isLoading = false, result = result.choices.first().message.content)
                }
            }.onFailure { e ->
                Napier.e("chatWithAI -> error = $e")
                _copyWritingStateFlow.update {
                    it.copy(isLoading = false, isError = true)
                }
            }
        }
    }

    /**
     * 社交圈文案生成 流式输出
     */
    @Suppress("unused")
    fun generateCopyWritingStreamVersion(theme: String, style: String) {
        Napier.i("generateCopyWritingStreamVersion -> theme = $theme, style = $style")
        _copyWritingStateFlow.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val request =
                "帮我生成一段社交软件使用的文案，主题是：$theme，风格是：$style，字数在100字左右"
            deepSeekChatRepository.generateCopyWritingByAIWithStream(request).collect { result ->
                _copyWritingStateFlow.update {
                    it.copy(isLoading = false)
                }
                _streamingText.update { it + result }
            }
        }
    }

    /**
     * 将这段文字复制到剪切板
     */
    fun copyText(text: String) {
        viewModelScope.launch {
            copyTextToClipBoard(text)
        }
    }

    /**
     * 测试接口
     */
    @Deprecated("测试接口")
    @Suppress("unused")
    fun testChat() {
        viewModelScope.launch {
            deepSeekChatRepository.testNetwork()
        }
    }

    fun triggerRandomNetworkRequest() {
        Napier.i("triggerRandomNetworkRequest")
        viewModelScope.launch {
            runCatching {
                deepSeekChatRepository.testNetwork()
            }.onFailure { e ->
                Napier.e(e.message.toString())
            }
        }
    }

    fun setSettingsViewOpenState(isOpen: Boolean) {
        settingsViewOpenState.update {
            isOpen
        }
    }

    fun setLocalThemeState(theme: Int) {
        themeHelper.setThemeState(theme)
    }

    fun updateLocalThemeState() {
        themeHelper.updateThemeState()
    }

    /**
     * 是否处于深色主题，明确设置为深色，或者系统自适应处于深色主题
     */
    @Composable
    fun isDarkTheme(): Boolean {
        return themeHelper.themeStateStateFlow.value == ThemeState.DARK || isSystemInDarkTheme()
    }
}