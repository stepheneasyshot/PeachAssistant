package com.stephen.aiassistant.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class DeepSeekStreamResult(
    val choices: List<StreamChoice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String? = "",
    val system_fingerprint: String,
    val usage: StreamUsage? = null
)

@Serializable
data class StreamChoice(
    val delta: Message,
    val finish_reason: String? = "",
    val index: Int,
    val logprobs: String?
)

@Serializable
data class StreamUsage(
    val completion_tokens: Int,
    val prompt_cache_hit_tokens: Int,
    val prompt_cache_miss_tokens: Int,
    val prompt_tokens: Int,
    val prompt_tokens_details: StreamPromptTokensDetails,
    val total_tokens: Int
)

@Serializable
data class StreamPromptTokensDetails(
    val cached_tokens: Int
)