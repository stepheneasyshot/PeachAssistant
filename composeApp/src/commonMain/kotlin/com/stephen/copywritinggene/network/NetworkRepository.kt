package com.stephen.copywritinggene.network

import com.stephen.copywritinggene.data.bean.AiRequestData
import com.stephen.copywritinggene.data.bean.QwenResult
import com.stephen.copywritinggene.data.bean.RequestMessage
import com.stephen.copywritinggene.data.bean.Role
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
            "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
        const val COMMON_SYSTEM_PROMT = "你是一个人工智能系统，可以根据用户的输入来返回生成式的回复"
        const val API_KEY = "sk-053e7743ae6a416790f90d833a019d63"
        const val MODEL_NAME = "DeepSeek-V3"
    }

    suspend fun chatWithAI(text: String) = withContext(Dispatchers.IO) {
        httpClient.post(BASE_URL) {
            // 配置请求头
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $API_KEY")
            }
            setBody(
                AiRequestData(
                    model = MODEL_NAME,
                    max_tokens = 2048,
                    temperature = 0.3,
                    stream = false,
                    messages = listOf(
                        RequestMessage(COMMON_SYSTEM_PROMT, Role.SYSTEM.roleDescription),
                        RequestMessage(text, Role.USER.roleDescription)
                    )
                )
            )
        }.body<QwenResult>()
    }
}