package org.terratec.altopia.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Login/Refresh token response from Supabase auth.
 * Contains access token, refresh token, and user information.
 */
@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("expires_at") val expiresAt: Long,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: UserResponse,
    @SerialName("weak_password") val weakPassword: Boolean? = null // or String? JSON said null, usually boolean or object.
)
