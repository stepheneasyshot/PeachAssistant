package com.stephen.copywritinggene.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephen.copywritinggene.network.NetworkRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class MainViewModel(private val networkRepository: NetworkRepository) : ViewModel() {

    fun chatWithAI(text:String) {
        viewModelScope.launch {
          val result =   networkRepository.chatWithAI(text)
            Napier.d("chatWithAI -> result = $result")
        }
    }
}