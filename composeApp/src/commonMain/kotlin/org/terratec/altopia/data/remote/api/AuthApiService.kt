package org.terratec.altopia.data.remote.api

import org.terratec.altopia.data.remote.dto.auth.LoginResponse
import org.terratec.altopia.data.remote.dto.auth.UserResponse

/**
 * API service interface for Supabase authentication endpoints.
 */
interface AuthApiService {
    /**
     * Authenticates a user with email and password.
     * 
     * @param email User's email
     * @param password User's password
     * @return LoginResponse containing tokens and user data
     */
    suspend fun login(email: String, password: String): LoginResponse
    
    /**
     * Logs out the current user (invalidates the current session).
     */
    suspend fun logout()
    
    /**
     * Refreshes the access token using a refresh token.
     * 
     * @param refreshToken The refresh token
     * @return LoginResponse with new tokens
     */
    suspend fun refreshToken(refreshToken: String): LoginResponse
    
    /**
     * Gets the current authenticated user's information.
     * 
     * @return UserResponse with user data
     */
    suspend fun getCurrentUser(): UserResponse
    
    /**
     * Sends a password recovery email.
     * 
     * @param email User's email
     */
    suspend fun recoverPassword(email: String)
    
    /**
     * Updates user information.
     * 
     * @param password New password (optional)
     * @param data User metadata (optional)
     * @return Updated UserResponse
     */
    suspend fun updateUser(password: String? = null, data: Map<String, String>? = null): UserResponse
}
