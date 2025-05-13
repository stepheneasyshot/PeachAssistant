package com.stephen.aiassistant.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephen.aiassistant.copyTextToClipBoard
import com.stephen.aiassistant.data.uistate.ResultState
import com.stephen.aiassistant.network.NetworkRepository
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.gallery.GALLERY
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val networkRepository: NetworkRepository,
) : ViewModel() {

    private val _copyWritingStateFlow = MutableStateFlow(ResultState())
    val copyWritingStateFlow = _copyWritingStateFlow.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText = _streamingText.asStateFlow()

    var galleryPermissionState by mutableStateOf(PermissionState.NotDetermined)
        private set

    private lateinit var permissionsController: PermissionsController
    fun setPermissionController(controller: PermissionsController) {
        this.permissionsController = controller
        viewModelScope.launch {
            galleryPermissionState =
                permissionsController.getPermissionState(Permission.GALLERY)
        }
    }

    fun provideOrRequestGalleryPermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.GALLERY)
                galleryPermissionState = PermissionState.Granted
            } catch (e: DeniedAlwaysException) {
                galleryPermissionState = PermissionState.DeniedAlways
            } catch (e: DeniedException) {
                galleryPermissionState = PermissionState.Denied
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    fun generateCopyWriting(theme: String, style: String) {
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