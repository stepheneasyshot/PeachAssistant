package com.stephen.aiassistant.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class DeepSeekRequestBean(
    val frequency_penalty: Int? = null,
    val logprobs: Boolean? = null,
    val max_tokens: Int? = null,
    val messages: List<RequestMessage>,
    val model: String,
    val presence_penalty: Int? = null,
    val response_format: ResponseFormat? = null,
    val stop: String? = null,
    val stream: Boolean? = false,
    val stream_options: String? =   null,
    val temperature: Float? = 0.3f,
    val tool_choice: String? = null,
    val tools: String? = null,
    val top_logprobs: String? = null,
    val top_p: Int? = null,
)

@Serializable
data class RequestMessage(
    val content: String,
    val role: String
)

@Serializable
data class ResponseFormat(
    val type: String
)