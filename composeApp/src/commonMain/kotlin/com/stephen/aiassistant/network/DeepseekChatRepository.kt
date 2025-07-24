package com.stephen.aiassistant.network

import com.stephen.aiassistant.data.ChatRole
import com.stephen.aiassistant.data.bean.DeepSeekRequestBean
import com.stephen.aiassistant.data.bean.DeepSeekResult
import com.stephen.aiassistant.data.bean.RequestMessage
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DeepseekChatRepository(private val ktorClient: KtorClient) {

    companion object {
        const val BASE_URL =
            "https://api.deepseek.com"
        const val COMMON_SYSTEM_PROMT = "你是一个人工智能系统，可以根据用户的输入来返回生成式的回复"
        const val ENGLISH_SYSTEM_PROMT = "You are a English teacher, you can help me improve my English skills, please answer my questions in English."
        const val API_KEY = "xxxxxxxxxxxxxxxxx"
        const val MODEL_NAME = "deepseek-chat"
    }

    suspend fun generateCopyWritingByAI(text: String) = withContext(Dispatchers.IO) {
        ktorClient.client.post("${BASE_URL}/chat/completions") {
            // 配置请求头
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $API_KEY")
            }
            setBody(
                DeepSeekRequestBean(
                    model = MODEL_NAME,
                    max_tokens = 2048,
                    temperature = 0.3f,
                    stream = false,
                    messages = listOf(
                        RequestMessage(COMMON_SYSTEM_PROMT, ChatRole.SYSTEM.roleDescription),
                        RequestMessage(text, ChatRole.USER.roleDescription)
                    )
                )
            )
        }.body<DeepSeekResult>()
    }

    suspend fun englishExerciseChat(chat: String) = withContext(Dispatchers.IO) {
        ktorClient.client.post("${BASE_URL}/chat/completions") {
            // 配置请求头
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $API_KEY")
            }
            setBody(
                DeepSeekRequestBean(
                    model = MODEL_NAME,
                    max_tokens = 2048,
                    temperature = 0.3f,
                    stream = false,
                    messages = listOf(
                        RequestMessage(ENGLISH_SYSTEM_PROMT, ChatRole.SYSTEM.roleDescription),
                        RequestMessage(chat, ChatRole.USER.roleDescription)
                    )
                )
            )
        }.body<DeepSeekResult>()
    }

    suspend fun triggerFirstRequest() {
        ktorClient.client.post("https://www.baidu.com").body<String>()
    }
}