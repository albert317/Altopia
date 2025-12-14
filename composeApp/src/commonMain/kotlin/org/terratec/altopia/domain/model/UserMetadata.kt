package org.terratec.altopia.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserMetadata(
    val name: String? = null,
    val avatarUrl: String? = null,
    val emailVerified: Boolean? = null
)
