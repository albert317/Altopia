package org.terratec.altopia.domain.model

data class PropertyUnit(
    val id: String,
    val bloqueId: String,
    val bloqueNombre: String?,
    val codigo: String,
    val piso: Int,
    val coeficienteArea: Double,
    val tipoUso: String, // You might want an Enum here later
    val createdAt: String
)
