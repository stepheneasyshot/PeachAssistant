package com.stephen.copywritinggene.network

import com.stephen.copywritinggene.data.bean.DeepSeekRequestBean
import com.stephen.copywritinggene.data.bean.DeepSeekResult
import com.stephen.copywritinggene.data.bean.RequestMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class NetworkRepository(private val httpClient: HttpClient) {

    companion object {
        const val BASE_URL =
            "https://api.deepseek.com"
        const val COMMON_SYSTEM_PROMT = "你是一个人工智能系统，可以根据用户的输入来返回生成式的回复"
        const val API_KEY = "sk-5d1ebf9b757344e7939a2e4e5bdc0e21"
        const val MODEL_NAME = "deepseek-chat"

        enum class ChatRole(val roleDescription: String) {
            SYSTEM("system"),
            USER("user"),
        }
    }

    suspend fun generateCopyWritingByAI(text: String) = withContext(Dispatchers.IO) {
        httpClient.post("${BASE_URL}/chat/completions") {
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
}