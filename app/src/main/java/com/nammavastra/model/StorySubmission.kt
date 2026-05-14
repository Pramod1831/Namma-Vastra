package com.nammavastra.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StorySubmission(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val section: String = "",
    @SerialName("weaver_name")
    val weaverName: String = "",
    val village: String = "",
    val subtitle: String = "",
    @SerialName("image_url")
    val imageUrl: String = "",
    @SerialName("submitted_by")
    val submittedBy: String = "",
    val status: String = "pending"
)
