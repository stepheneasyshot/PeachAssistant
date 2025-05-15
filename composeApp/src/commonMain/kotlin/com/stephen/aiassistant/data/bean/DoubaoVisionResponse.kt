package com.stephen.aiassistant.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class DoubaoVisionResponse(
    val choices: List<DoubaoVisionChoice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val service_tier: String,
    val usage: DoubaoVisionUsage
)

@Serializable
data class DoubaoVisionChoice(
    val finish_reason: String,
    val index: Int,
    val logprobs: String?,
    val message: DoubaoVisionMessage
)

@Serializable
data class DoubaoVisionUsage(
    val completion_tokens: Int,
    val completion_tokens_details: CompletionTokensDetails,
    val prompt_tokens: Int,
    val prompt_tokens_details: DoubaoVisionPromptTokensDetails,
    val total_tokens: Int
)

@Serializable
data class DoubaoVisionMessage(
    val content: String,
    val role: String
)

@Serializable
data class CompletionTokensDetails(
    val reasoning_tokens: Int
)

@Serializable
data class DoubaoVisionPromptTokensDetails(
    val cached_tokens: Int
)

@Serializable
data class FoodContent(
    val foods: List<Food>
)

@Serializable
data class Food(
    val name: String,
    val weight: Int,
    val calorie: Int
)
