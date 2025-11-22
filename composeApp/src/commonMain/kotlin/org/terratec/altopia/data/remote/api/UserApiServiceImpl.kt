package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.terratec.altopia.data.remote.dto.response.UserResponse

/**
 * Implementation of UserApiService using Ktor HttpClient.
 * Handles the actual HTTP communication with the backend API.
 */
class UserApiServiceImpl(
    private val httpClient: HttpClient
) : UserApiService {
    
    private val baseUrl = "https://jsonplaceholder.typicode.com"
    
    override suspend fun getUser(userId: Long): UserResponse {
        return httpClient.get("$baseUrl/users/$userId").body()
    }
    
    override suspend fun getUsers(): List<UserResponse> {
        return httpClient.get("$baseUrl/users").body()
    }
}
