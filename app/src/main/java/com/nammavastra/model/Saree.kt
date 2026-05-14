package com.nammavastra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Saree(
    val id: String = "",
    val name: String = "",
    @SerialName("weaver_name")
    val weaverName: String = "",
    @SerialName("fabric_type")
    val fabricType: String = "",
    @SerialName("price_range")
    val priceRange: String = "",
    val location: String = "",
    @SerialName("whatsapp_number")
    val whatsappNumber: String = "",
    @SerialName("image_url")
    val imageUrl: String = "",
    val description: String = ""
)
