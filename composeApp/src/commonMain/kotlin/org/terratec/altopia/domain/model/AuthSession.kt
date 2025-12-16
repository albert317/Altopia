package org.terratec.altopia.domain.model

import kotlinx.serialization.Serializable


/**
 * Represents an authenticated user session.
 * 
 * @property accessToken JWT token for API authentication
 * @property refreshToken Token used to obtain new access tokens
 * @property expiresAt Unix timestamp (in seconds) when the access token expires
 * @property user The authenticated user's information
 */
@Serializable
data class AuthSession(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val expiresAt: Long,
    val refreshToken: String,
    val user: User,
    val weakPassword: Boolean? = null
)
