package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.dto.auth.LoginResponse
import org.terratec.altopia.data.remote.dto.auth.UserResponse

interface AuthDataSource {
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun logout()
    suspend fun refreshToken(refreshToken: String): LoginResponse
    /**
     * Sends a password recovery email.
     * @param email The user's email
     */
    suspend fun recoverPassword(email: String)
    
    /**
     * Updates user information.
     * @param password New password (optional)
     * @param data User metadata (optional)
     * @return Updated UserResponse
     */
    suspend fun updateUser(password: String? = null, data: Map<String, String>? = null): UserResponse
    
    suspend fun getCurrentUser(): UserResponse
}
