package com.stephen.aiassistant.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephen.aiassistant.data.ChatRole
import com.stephen.aiassistant.data.bean.Food
import com.stephen.aiassistant.data.bean.FoodContent
import com.stephen.aiassistant.data.uistate.EnglishChatState
import com.stephen.aiassistant.data.uistate.ChatItem
import com.stephen.aiassistant.data.uistate.ResultState
import com.stephen.aiassistant.network.DeepseekChatRepository
import com.stephen.aiassistant.network.DoubaoVisionRepository
import com.stephen.aiassistant.platform.copyTextToClipBoard
import com.stephen.aiassistant.platform.permissions.CAMERA
import com.stephen.aiassistant.platform.permissions.GALLERY
import com.stephen.aiassistant.platform.permissions.RECORD_AUDIO
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainViewModel(
    private val deepseekChatRepository: DeepseekChatRepository,
    private val doubaoVisionRepository: DoubaoVisionRepository,
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

    // ai模型对话
    private val _aiModelChatListState = MutableStateFlow(EnglishChatState())
    val aiModelChatListStateFlow = _aiModelChatListState.asStateFlow()

    private val _foodListState = MutableStateFlow(mutableListOf<Food>())
    val foodListStateFlow = _foodListState.asStateFlow()

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
                val result = "I'm fine. Thank you. And you?"
                Napier.i(result)
                // 回复的内容
                _aiModelChatListState.update {
                    it.copy(
                        chatList = it.chatList + listOf(
                            ChatItem(
                                content = result,
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
                                content = "通信过程中出现异常",
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
                val result = deepseekChatRepository.generateCopyWritingByAI(request)
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
            deepseekChatRepository.generateCopyWritingByAIWithStream(request).collect { result ->
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
            deepseekChatRepository.testChat()
        }
    }

    /**
     * 卡路里图片发送
     */
    fun calCalorieByAI(file: PlatformFile) {
        Napier.i("calCalorieByAI -> fileName: ${file.name}")
        _foodListState.update {
            it.clear()
            it
        }
        viewModelScope.launch {
            runCatching {
                val imagefileType = file.name.substringAfterLast(".")
                val imageFilebase64 = file.readBytes().encodeBase64()
                val result = doubaoVisionRepository.calCalorieByAI(
                    imagefileType,
                    imageFilebase64
                ).choices.first().message.content
                val foodList = Json.decodeFromString<FoodContent>(result)
                Napier.i("calCalorieByAI -> result = $result")
                _foodListState.value = foodList.foods.toMutableList()
            }
        }
    }
}