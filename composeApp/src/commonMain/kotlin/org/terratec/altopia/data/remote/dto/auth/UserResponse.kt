package org.terratec.altopia.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User response from Supabase auth endpoints.
 */
@Serializable
data class UserResponse(
    @SerialName("id") val id: String,
    @SerialName("aud") val aud: String,
    @SerialName("role") val role: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("email_confirmed_at") val emailConfirmedAt: String? = null,
    @SerialName("confirmed_at") val confirmedAt: String? = null,
    @SerialName("last_sign_in_at") val lastSignInAt: String? = null,
    @SerialName("user_metadata") val userMetadata: UserMetadata? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("is_anonymous") val isAnonymous: Boolean = false
)
