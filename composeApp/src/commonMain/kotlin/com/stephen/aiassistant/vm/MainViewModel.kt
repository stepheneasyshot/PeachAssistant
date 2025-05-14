package com.stephen.aiassistant.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephen.aiassistant.data.uistate.ResultState
import com.stephen.aiassistant.network.NetworkRepository
import com.stephen.aiassistant.platform.copyTextToClipBoard
import com.stephen.aiassistant.platform.permissions.GALLERY
import com.stephen.aiassistant.platform.permissions.RECORD_AUDIO
import com.stephen.permissions.DeniedAlwaysException
import com.stephen.permissions.DeniedException
import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionState
import com.stephen.permissions.PermissionsController
import com.stephen.permissions.RequestCanceledException
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val networkRepository: NetworkRepository,
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

    fun openPermissionSettings(){
        permissionsController.openAppSettings()
    }

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
     * 语音转文字
     */
    fun startToProcessSpeechToText(){
        Napier.i("startToProcessSpeechToText")
    }

    fun generateCopyWriting(theme: String, style: String) {
        Napier.i("generateCopyWriting -> theme = $theme, style = $style")
        _copyWritingStateFlow.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            runCatching {
                val request =
                    "帮我生成一段社交软件使用的文案，主题是：$theme，风格是：$style，字数在100字左右"
                val result = networkRepository.generateCopyWritingByAI(request)
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

    fun generateCopyWritingStreamVersion(theme: String, style: String) {
        Napier.i("generateCopyWritingStreamVersion -> theme = $theme, style = $style")
        _copyWritingStateFlow.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val request =
                "帮我生成一段社交软件使用的文案，主题是：$theme，风格是：$style，字数在100字左右"
            networkRepository.generateCopyWritingByAIWithStream(request).collect { result ->
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
    fun testChat() {
        viewModelScope.launch {
            networkRepository.testChat()
        }
    }
}