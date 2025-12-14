package org.terratec.altopia.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserRolesRequest(
    @SerialName("p_usuario_id")
    val userId: String
)
