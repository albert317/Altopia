package org.terratec.altopia.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Property(
    val id: String,
    val unitCode: String,
    val condoName: String,
    val address: String
)
