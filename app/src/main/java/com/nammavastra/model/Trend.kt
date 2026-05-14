package com.nammavastra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trend(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val colors: List<String> = emptyList(),
    @SerialName("image_url")
    val imageUrl: String = "",
    val month: String = ""
)
