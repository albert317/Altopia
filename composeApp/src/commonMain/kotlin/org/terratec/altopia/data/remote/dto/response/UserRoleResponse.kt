package org.terratec.altopia.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRoleResponse(
    @SerialName("usuario_id") val userId: String,
    @SerialName("rol_asignado") val roleName: String
)
