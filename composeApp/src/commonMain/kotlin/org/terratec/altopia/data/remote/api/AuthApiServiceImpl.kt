package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.terratec.altopia.data.local.session.SessionManager
import org.terratec.altopia.data.remote.dto.auth.LoginRequest
import org.terratec.altopia.data.remote.dto.auth.LoginResponse
import org.terratec.altopia.data.remote.dto.auth.RefreshTokenRequest
import org.terratec.altopia.data.remote.dto.auth.UserResponse

/**
 * Implementation of AuthApiService using Ktor HttpClient.
 * Makes direct API calls to Supabase auth endpoints.
 */
class AuthApiServiceImpl(
    private val httpClient: HttpClient,
    private val sessionManager: SessionManager
) : AuthApiService {
    
    override suspend fun login(email: String, password: String): LoginResponse {
        return httpClient.post("auth/v1/token") {
            parameter("grant_type", "password")
            setBody(LoginRequest(email, password))
        }.body()
    }
    
    override suspend fun logout() {
        val session = sessionManager.getSession()
        session?.let {
            httpClient.post("auth/v1/logout") {
                header("Authorization", "Bearer ${it.accessToken}")
            }
        }
    }
    
    override suspend fun refreshToken(refreshToken: String): LoginResponse {
        return httpClient.post("auth/v1/token") {
            parameter("grant_type", "refresh_token")
            setBody(RefreshTokenRequest(refreshToken))
        }.body()
    }
    
    override suspend fun getCurrentUser(): UserResponse {
        val session = sessionManager.getSession()
            ?: throw IllegalStateException("No active session")
        
        return httpClient.get("auth/v1/user") {
            header("Authorization", "Bearer ${session.accessToken}")
        }.body()
    }
}
