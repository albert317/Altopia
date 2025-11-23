package org.terratec.altopia.domain.model

/**
 * Represents an authenticated user session.
 * 
 * @property accessToken JWT token for API authentication
 * @property refreshToken Token used to obtain new access tokens
 * @property expiresAt Unix timestamp (in seconds) when the access token expires
 * @property user The authenticated user's information
 */
data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val user: User
)
