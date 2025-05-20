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
import com.stephen.aiassistant.helper.PermissionHelper
import com.stephen.aiassistant.helper.ThemeHelper
import com.stephen.aiassistant.network.DeepseekChatRepository
import com.stephen.aiassistant.network.DoubaoVisionRepository
import com.stephen.aiassistant.platform.copyTextToClipBoard
import com.stephen.aiassistant.platform.network.getPlatformConnectionManager
import com.stephen.permissions.PermissionsController
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
    private val permissionHelper: PermissionHelper,
    private val themeHelper: ThemeHelper
) : ViewModel() {

    private val networkStateManager = getPlatformConnectionManager()

    init {
        Napier.i("=====>MainViewModel init<=======")
        viewModelScope.launch {
            networkStateManager.start()
            runCatching {
                deepSeekChatRepository.triggerFirstRequest()
            }.onFailure { e ->
                Napier.e("network unreachable! message: ${e.message}")
            }
        }
    }

    val networkConnectivityState = networkStateManager.isNetworkConnected.asStateFlow()

    // 各权限状态
    val galleryPermissionState = permissionHelper.galleryPermissionState
    val microphonePermissionState = permissionHelper.microphonePermissionState
    val cameraPermissionState = permissionHelper.cameraPermissionState
    val storagePermissionState = permissionHelper.storagePermissionState
    val writeStoragePermissionState = permissionHelper.writeStoragePermissionState

    private val _copyWritingStateFlow = MutableStateFlow(ResultState())
    val copyWritingStateFlow = _copyWritingStateFlow.asStateFlow()

    private val _imageTempByteArray = MutableStateFlow<ByteArray?>(null)
    val imageTempByteArrayState = _imageTempByteArray.asStateFlow()

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

    /**
     * 拍照缓存ByteArray
     */
    fun saveTempPicture(imageByteArray: ByteArray) {
        Napier.i("updateTempPicture")
        _imageTempByteArray.update {
            imageByteArray
        }
    }

    /**
     * 清除相机拍摄临时图片
     */
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
     * 卡路里图片发送，兼容相册图片文件和临时ByteArray
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
     * 将这段文字复制到剪切板
     */
    fun copyText(text: String) {
        viewModelScope.launch {
            copyTextToClipBoard(text)
        }
    }

    /**
     * 更新设置页面的打开状态，维护防止重组后状态丢失
     */
    fun setSettingsViewOpenState(isOpen: Boolean) {
        settingsViewOpenState.update {
            isOpen
        }
    }

    /**
     * 下发主题存储值
     */
    fun setLocalThemeState(theme: Int) {
        themeHelper.setThemeState(theme)
    }

    /**
     * 更新主题状态
     */
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

    /**
     * 初始化权限管理器 PermissionsController
     */
    fun initPermissionController(controller: PermissionsController) {
        Napier.i("setPermissionController")
        permissionHelper.setPermissionController(controller)
    }

    /**
     * 打开系统权限设置页面
     */
    fun openPermissionSettings() {
        Napier.i("openPermissionSettings")
        permissionHelper.openPermissionSettings()
    }

    /**
     * 相册浏览权限
     */
    fun checkGalleryPermission() {
        permissionHelper.provideOrRequestGalleryPermission()
    }

    /**
     * 录音权限
     */
    fun checkMicrophonePermission() {
        permissionHelper.provideOrRequestMicrophonePermission()
    }

    /**
     * 相机权限
     */
    fun checkCameraPermission() {
        permissionHelper.provideOrRequestCameraPermission()
    }

    /**
     * 存储权限
     */
    fun checkStoragePermission() {
        permissionHelper.provideOrRequestStoragePermission()
    }

    /**
     * 写入存储权限
     */
    fun checkWriteStoragePermission() {
        permissionHelper.provideOrRequestWriteStoragePermission()
    }

    override fun onCleared() {
        super.onCleared()
        Napier.i("=====>MainViewModel onCleared<=======")
        networkStateManager.stop()
    }
}