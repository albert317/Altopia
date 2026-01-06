package org.terratec.altopia.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnitDto(
    @SerialName("id") val id: String,
    @SerialName("bloque_id") val bloqueId: String,
    @SerialName("bloque_nombre") val bloqueNombre: String?, // Nullable just in case of left join issues, though typically present
    @SerialName("codigo") val codigo: String,
    @SerialName("piso") val piso: Int,
    @SerialName("coeficiente_area") val coeficienteArea: Double,
    @SerialName("tipo_uso") val tipoUso: String,
    @SerialName("created_at") val createdAt: String
)
