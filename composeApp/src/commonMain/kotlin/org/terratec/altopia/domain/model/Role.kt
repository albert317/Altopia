package org.terratec.altopia.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class RoleType {
    ADMIN, PROPIETARIO, INQUILINO, VIGILANTE
}

@Serializable
data class Role(
    val id: Int,
    val name: RoleType,
    val description: String? = null
)
