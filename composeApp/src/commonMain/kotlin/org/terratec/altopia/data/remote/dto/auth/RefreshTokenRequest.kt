package org.terratec.altopia.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for refreshing access token.
 */
@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String
)
