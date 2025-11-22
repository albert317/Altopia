package org.terratec.altopia.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    val id: Long,
    @SerialName("created_at")
    val createdAt: String,
    val videoLink: String,
    val isFinished: Boolean? = null
)
