package org.terratec.altopia.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for password recovery endpoint.
 */
@Serializable
data class RecoverPasswordRequest(
    val email: String,
    @SerialName("redirect_to") val redirectTo: String? = null
)
