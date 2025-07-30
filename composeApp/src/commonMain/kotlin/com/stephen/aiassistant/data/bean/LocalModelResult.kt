package com.stephen.aiassistant.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class LocalModelResult(
    val choices: List<LocalModelChoice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val system_fingerprint: String,
    val timings: Timings,
    val usage: LocalModelUsage
)

@Serializable
data class LocalModelChoice(
    val finish_reason: String,
    val index: Int,
    val message: Message
)

@Serializable
data class Timings(
    val predicted_ms: Double,
    val predicted_n: Int,
    val predicted_per_second: Double,
    val predicted_per_token_ms: Double,
    val prompt_ms: Double,
    val prompt_n: Int,
    val prompt_per_second: Double,
    val prompt_per_token_ms: Double
)

@Serializable
data class LocalModelUsage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)