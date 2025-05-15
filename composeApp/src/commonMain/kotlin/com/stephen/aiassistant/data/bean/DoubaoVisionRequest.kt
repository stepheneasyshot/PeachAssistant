package com.stephen.aiassistant.data.bean

import kotlinx.serialization.Serializable

@Serializable
data class DoubaoVisionRequest(
    val model: String,
    val messages: List<DoubaoRequestMessage>
)

@Serializable
data class DoubaoRequestMessage(
    val role: String,
    val content: List<DoubaoVisionContent>,
)

@Serializable
data class DoubaoVisionContent(
    val type: String,
    val image_url: ImageUrl? = null,
    val text: String? = null
)

@Serializable
data class ImageUrl(
    val url: String
)