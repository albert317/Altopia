package org.terratec.altopia.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val aud: String,
    val role: String,
    val email: String,
    val phone: String? = null,
    val emailConfirmedAt: String? = null,
    val lastSignInAt: String? = null,
    val userMetadata: UserMetadata? = null,
    val createdAt: String,
    val updatedAt: String,
    val isAnonymous: Boolean = false,
    val appRoles: List<Role> = emptyList(),
    val properties: List<Property> = emptyList()
)
