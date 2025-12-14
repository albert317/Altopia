package org.terratec.altopia.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonResponse(
    @SerialName("id") val id: String,
    @SerialName("nombre") val nombre: String, // Note: User JSON has "Albert " (trailing space), usually we keep String
    @SerialName("apellidos") val apellidos: String,
    @SerialName("dni_ruc") val dniRuc: String,
    @SerialName("telefono") val telefono: String?,
    @SerialName("email_contacto") val emailContacto: String?,
    @SerialName("created_at") val createdAt: String,
    @SerialName("auth_id") val authId: String
)