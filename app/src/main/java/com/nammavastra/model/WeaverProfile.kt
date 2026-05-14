package com.nammavastra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeaverProfile(
    val id: String = "",
    val name: String = "",
    val village: String = "",
    @SerialName("image_url")
    val imageUrl: String = "",
    val subtitle: String = ""
)
