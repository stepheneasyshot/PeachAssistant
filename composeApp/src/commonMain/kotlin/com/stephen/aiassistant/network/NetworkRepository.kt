package com.stephen.aiassistant.network

import com.stephen.aiassistant.data.ChatRole
import com.stephen.aiassistant.data.bean.DeepSeekRequestBean
import com.stephen.aiassistant.data.bean.DeepSeekResult
import com.stephen.aiassistant.data.bean.DeepSeekStreamResult
import com.stephen.aiassistant.data.bean.RequestMessage
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class NetworkRepository(private val ktorClient: KtorClient) {

    companion object {
        const val BASE_URL =
            "https://api.deepseek.com"
        const val COMMON_SYSTEM_PROMT = "你是一个人工智能系统，可以根据用户的输入来返回生成式的回复"
        const val API_KEY = "sk-5d1ebf9b757344e7939a2e4e5bdc0e21"
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

    fun generateCopyWritingByAIWithStream(text: String): Flow<String> =
        flow {
            val response: HttpResponse = ktorClient.client.post("${BASE_URL}/chat/completions") {
                // 配置请求头
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $API_KEY")
                }
                setBody(
                    DeepSeekRequestBean(
                        model = MODEL_NAME,
                        max_tokens = 2048,
                        temperature = 0.3f,
                        stream = true,
                        messages = listOf(
                            RequestMessage(
                                COMMON_SYSTEM_PROMT,
                                ChatRole.SYSTEM.roleDescription
                            ),
                            RequestMessage(text, ChatRole.USER.roleDescription)
                        )
                    )
                )
            }

            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line()
                if (line != null && line.startsWith("data: ")) {
                    val jsonData = line.removePrefix("data: ").trim()
                    if (jsonData == "[DONE]") {
                        break
                    }
                    val result = Json.decodeFromString<DeepSeekStreamResult>(jsonData)
                    emit(result.choices.first().delta.content)
                }
            }
        }.catch {
            Napier.e("generateCopyWritingByAIWithStream -> error = $it")
        }

    suspend fun testChat() {
        generateCopyWritingByAIWithStream("铁观音是红茶还是绿茶").collect { result ->
            // 处理流式结果
            println(result)
        }
    }
}