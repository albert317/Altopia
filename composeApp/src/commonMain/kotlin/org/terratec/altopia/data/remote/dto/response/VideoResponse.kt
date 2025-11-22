package org.terratec.altopia.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
    val id: Long,
    @SerialName("created_at")
    val createdAt: String,
    val videoLink: String,
    val isFinished: Boolean? = null
)
