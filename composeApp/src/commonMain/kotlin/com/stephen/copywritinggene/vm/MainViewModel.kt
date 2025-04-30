package com.stephen.copywritinggene.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}