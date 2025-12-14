package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.dto.response.PersonResponse
import org.terratec.altopia.data.remote.dto.response.UserProfileResponse
import org.terratec.altopia.data.remote.dto.response.RoleResponse
import org.terratec.altopia.data.remote.dto.response.UserResponse
import org.terratec.altopia.data.remote.dto.response.UserRoleResponse

/**
 * Remote Data Source interface for User operations.
 * This layer is responsible for choosing which API endpoint to call and wrapping the response in Result.
 */
interface UserRemoteDataSource {
    /**
     * Get person details by auth_id
     * @param authId Auth ID
     * @return Result with PersonResponse or error
     */
    suspend fun getPerson(authId: String): Result<PersonResponse>

    /**
     * Get a user by ID
     * @param userId User ID
     * @return Result with UserResponse or error
     */
    suspend fun getUser(userId: String): Result<UserResponse>

    /**
     * Get all users from remote source
     * @return Result with list of UserResponse or error
     */
    suspend fun getUsers(): Result<List<UserResponse>>

    /**
     * Get user roles
     * @param userId User ID
     * @return Result with list of UserRoleResponse or error
     */
    suspend fun getUserRoles(userId: String): Result<List<UserRoleResponse>>

    /**
     * Get business user by persona ID
     * @param personId Persona ID
     * @return Result with UserResponse or error
     */
    suspend fun getUserByPersonId(personId: String): Result<UserResponse>

    /**
     * Get user profiles
     * @param userId User ID
     * @return Result with list of UserProfileResponse or error
     */
    suspend fun getUserProfiles(userId: String): Result<List<UserProfileResponse>>
}
