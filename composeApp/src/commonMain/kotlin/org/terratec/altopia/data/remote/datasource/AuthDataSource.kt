package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.dto.auth.LoginResponse
import org.terratec.altopia.data.remote.dto.auth.UserResponse

interface AuthDataSource {
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun logout()
    suspend fun refreshToken(refreshToken: String): LoginResponse
    suspend fun getCurrentUser(): UserResponse
}
