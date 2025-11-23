package org.terratec.altopia.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Request body for login endpoint.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
