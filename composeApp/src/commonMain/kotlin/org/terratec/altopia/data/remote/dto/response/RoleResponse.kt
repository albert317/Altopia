package org.terratec.altopia.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("nombre")
    val name: String,
    @SerialName("descripcion")
    val description: String? = null
)
