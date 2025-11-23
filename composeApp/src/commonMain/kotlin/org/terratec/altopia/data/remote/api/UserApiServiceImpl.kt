package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.terratec.altopia.data.remote.dto.response.UserResponse
import org.terratec.altopia.data.remote.util.safeApiCall

/**
 * Implementation of UserApiService using Ktor HttpClient.
 * Handles the actual HTTP communication with the backend API.
 * Uses a pre-configured HttpClient with base URL already set.
 */
class UserApiServiceImpl(
    private val httpClient: HttpClient
) : UserApiService {

    override suspend fun getUser(userId: Long): UserResponse {
        // Use relative URL without leading slash - base URL is configured in the injected HttpClient
        return safeApiCall {
            httpClient.get("users/$userId")
        }
    }

    override suspend fun getUsers(): List<UserResponse> {
        // Use relative URL without leading slash - base URL is configured in the injected HttpClient
        return safeApiCall {
            httpClient.get("users")
        }
    }
}
