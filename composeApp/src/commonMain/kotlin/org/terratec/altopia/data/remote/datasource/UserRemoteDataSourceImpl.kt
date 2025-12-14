package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.api.UserApiService
import org.terratec.altopia.data.remote.dto.request.GetUserProfilesRequest
import org.terratec.altopia.data.remote.dto.request.GetUserRolesRequest
import org.terratec.altopia.data.remote.dto.response.PersonResponse
import org.terratec.altopia.data.remote.dto.response.UserProfileResponse
import org.terratec.altopia.data.remote.dto.response.RoleResponse
import org.terratec.altopia.data.remote.dto.response.UserResponse
import org.terratec.altopia.data.remote.dto.response.UserRoleResponse

/**
 * Implementation of UserRemoteDataSource.
 * Handles remote data operations and error handling.
 */
class UserRemoteDataSourceImpl(
    private val apiService: UserApiService
) : UserRemoteDataSource {
    override suspend fun getPerson(authId: String): Result<PersonResponse> {
        return try {
            val response = apiService.getPerson(authId).first()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<UserResponse> {
        return try {
            val response = apiService.getUser(userId)
            val user = response.firstOrNull() ?: throw Exception("User not found: $userId")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsers(): Result<List<UserResponse>> {
        return try {
            val response = apiService.getUsers()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserRoles(userId: String): Result<List<UserRoleResponse>> {
        return try {
            val response = apiService.getUserRoles(GetUserRolesRequest(userId))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByPersonId(personId: String): Result<UserResponse> {
        return try {
            val response = apiService.getBusinessUserByPersonId(personId)
            val user = response.firstOrNull() ?: throw Exception("User not found for personId: $personId")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getUserProfiles(userId: String): Result<List<UserProfileResponse>> {
        return try {
            val response = apiService.getUserProfiles(GetUserProfilesRequest(userId))
            Result.success(response.sortedBy { it.profileType })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
