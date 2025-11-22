package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.dto.response.UserResponse

/**
 * Remote Data Source for User data.
 * Coordinates data fetching from remote sources (API).
 */
interface UserRemoteDataSource {

    /**
     * Get a user by ID from remote source
     * @param userId User ID
     * @return Result with UserResponse or error
     */
    suspend fun getUser(userId: Long): Result<UserResponse>

    /**
     * Get all users from remote source
     * @return Result with list of UserResponse or error
     */
    suspend fun getUsers(): Result<List<UserResponse>>
}
