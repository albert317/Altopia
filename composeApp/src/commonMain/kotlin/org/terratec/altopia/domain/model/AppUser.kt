package org.terratec.altopia.domain.model

data class AppUser(
    val id: String,
    val personaId: String?,
    val estado: Boolean,
    val createdAt: String
)
