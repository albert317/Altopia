package org.terratec.altopia.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User response from Supabase auth endpoints.
 */
@Serializable
data class UserResponse(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("email_confirmed_at") val emailConfirmedAt: String? = null,
    @SerialName("user_metadata") val userMetadata: UserMetadata? = null
)
