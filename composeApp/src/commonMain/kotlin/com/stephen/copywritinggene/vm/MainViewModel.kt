package com.stephen.copywritinggene.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephen.copywritinggene.copyTextToClipBoard
import com.stephen.copywritinggene.data.uistate.ResultState
import com.stephen.copywritinggene.network.NetworkRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val networkRepository: NetworkRepository) : ViewModel() {

    private val _copyWritingStateFlow = MutableStateFlow(ResultState())
    val copyWritingStateFlow = _copyWritingStateFlow.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText = _streamingText.asStateFlow()

    fun generateCopyWriting(theme: String, style: String) {
        _copyWritingStateFlow.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val request =
                "帮我生成一段社交软件使用的文案，主题是：$theme，风格是：$style，字数在100字左右"
            val result = networkRepository.generateCopyWritingByAI(request)
            Napier.d("chatWithAI -> result = $result")
            _copyWritingStateFlow.update {
                it.copy(isLoading = false, result = result.choices.first().message.content)
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