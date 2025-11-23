package org.terratec.altopia.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Request body for updating user information.
 * All fields are optional.
 */
@Serializable
data class UserUpdateRequest(
    val email: String? = null,
    val password: String? = null,
    val data: Map<String, String>? = null
)
