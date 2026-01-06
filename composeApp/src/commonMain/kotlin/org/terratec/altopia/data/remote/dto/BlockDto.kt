package org.terratec.altopia.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockDto(
    @SerialName("id") val id: String,
    @SerialName("nombre") val nombre: String
)
