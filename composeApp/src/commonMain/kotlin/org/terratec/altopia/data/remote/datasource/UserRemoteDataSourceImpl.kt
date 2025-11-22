package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.api.UserApiService
import org.terratec.altopia.data.remote.dto.response.UserResponse

/**
 * Implementation of UserRemoteDataSource.
 * Handles remote data operations and error handling.
 */
class UserRemoteDataSourceImpl(
    private val apiService: UserApiService
) : UserRemoteDataSource {
    
    override suspend fun getUser(userId: Long): Result<UserResponse> {
        return try {
            val response = apiService.getUser(userId)
            Result.success(response)
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
}
