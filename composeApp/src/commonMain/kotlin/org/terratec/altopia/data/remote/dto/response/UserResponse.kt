package org.terratec.altopia.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val name: String,
    val email: String
)
