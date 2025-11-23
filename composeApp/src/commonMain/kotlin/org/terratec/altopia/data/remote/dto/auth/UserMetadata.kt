package org.terratec.altopia.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User metadata from Supabase auth response.
 * Contains custom user data like name and avatar.
 */
@Serializable
data class UserMetadata(
    @SerialName("name") val name: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null
)
