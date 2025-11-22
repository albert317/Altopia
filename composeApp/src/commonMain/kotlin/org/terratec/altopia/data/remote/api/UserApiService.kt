package org.terratec.altopia.data.remote.api

import org.terratec.altopia.data.remote.dto.response.UserResponse

/**
 * API Service for User-related endpoints.
 * Defines the contract for HTTP requests/responses with the backend API.
 */
interface UserApiService {

    /**
     * Get a user by ID
     * @param userId User ID
     * @return UserResponse DTO
     */
    suspend fun getUser(userId: Long): UserResponse

    /**
     * Get all users
     * @return List of UserResponse DTOs
     */
    suspend fun getUsers(): List<UserResponse>
}
