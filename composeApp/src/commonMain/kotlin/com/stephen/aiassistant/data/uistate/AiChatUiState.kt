package com.stephen.aiassistant.data.uistate

import com.stephen.aiassistant.data.ChatRole


data class AiChatUiState(
    val chatList: List<ChatItem> = listOf(),
    val listSize: Int = chatList.size
) {
    fun toUiState() = AiChatUiState(chatList = chatList, listSize = listSize)
}

data class ChatItem(
    val content: String,
    val role: ChatRole,
)