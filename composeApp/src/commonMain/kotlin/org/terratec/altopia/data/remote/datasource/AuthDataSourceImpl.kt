package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.api.AuthApiService
import org.terratec.altopia.data.remote.dto.auth.LoginResponse
import org.terratec.altopia.data.remote.dto.auth.UserResponse

class AuthDataSourceImpl(
    private val apiService: AuthApiService
) : AuthDataSource {
    
    override suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    override suspend fun logout() {
        apiService.logout()
    }

    override suspend fun refreshToken(refreshToken: String): LoginResponse {
        return apiService.refreshToken(refreshToken)
    }

    override suspend fun getCurrentUser(): UserResponse {
        return apiService.getCurrentUser()
    }
}
