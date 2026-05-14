package com.nammavastra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeaverStory(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    @SerialName("image_url")
    val imageUrl: String = "",
    val section: String = "",
    @SerialName("weaver_name")
    val weaverName: String = "",
    val village: String = ""
)
