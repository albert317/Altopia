package org.terratec.altopia.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUnitRequest(
    @SerialName("bloque_id") val bloqueId: String,
    @SerialName("codigo") val codigo: String,
    @SerialName("piso") val piso: Int,
    @SerialName("coeficiente_area") val coeficienteArea: Double,
    @SerialName("tipo_uso") val tipoUso: String
)
