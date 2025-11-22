package org.terratec.altopia.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val email: String
)
