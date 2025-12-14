package org.terratec.altopia.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id") val id: String,
    @SerialName("persona_id") val personaId: String? = null,
    @SerialName("estado") val estado: Boolean,
    @SerialName("created_at") val createdAt: String,
)
