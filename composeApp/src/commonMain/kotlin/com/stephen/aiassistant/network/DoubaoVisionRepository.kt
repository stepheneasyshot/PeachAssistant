package com.stephen.aiassistant.network

import com.stephen.aiassistant.data.ChatRole
import com.stephen.aiassistant.data.bean.DoubaoRequestMessage
import com.stephen.aiassistant.data.bean.DoubaoVisionContent
import com.stephen.aiassistant.data.bean.DoubaoVisionRequest
import com.stephen.aiassistant.data.bean.DoubaoVisionResponse
import com.stephen.aiassistant.data.bean.ImageUrl
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DoubaoVisionRepository(private val ktorClient: KtorClient) {

    companion object {
        const val BASE_URL =
            "https://ark.cn-beijing.volces.com/api/v3"
        const val VISION_SYSTEM_PROMT =
            "下图是一张食物图片，请你计算每种食物的重量和卡路里，返回一个json，其中name为String，weight为Int，calorie为Int（单位千卡），json格式：\n" +
                    "{\n" +
                    "  \"foods\": [\n" +
                    "    {\n" +
                    "      \"name\": \"食物名称\",\n" +
                    "      \"weight\": \"食物重量\",\n" +
                    "      \"calorie\": \"食物卡路里\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}"
        const val API_KEY = "xxxxxxxxxxxx"
        const val MODEL_NAME = "doubao-1-5-vision-pro-32k-250115"
    }

    suspend fun calCalorieByAI(imageType: String, imageBase64:String) = withContext(Dispatchers.IO) {
        ktorClient.client.post("${BASE_URL}/chat/completions") {
            // 配置请求头
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $API_KEY")
            }
            setBody(
                DoubaoVisionRequest(
                    model = MODEL_NAME,
                    messages = listOf(
                        DoubaoRequestMessage(
                            role = ChatRole.SYSTEM.roleDescription,
                            content = listOf(
                                DoubaoVisionContent(
                                    type = "text",
                                    text = VISION_SYSTEM_PROMT,
                                ),
                                DoubaoVisionContent(
                                    type = "image_url",
                                    image_url = ImageUrl(
                                        url = "data:image/$imageType;base64,$imageBase64"
                                    ),
                                )
                            )
                        ),
                    )
                )
            )
        }.body<DoubaoVisionResponse>()
    }
}