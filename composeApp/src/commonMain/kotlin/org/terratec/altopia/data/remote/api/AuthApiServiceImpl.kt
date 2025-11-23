package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.terratec.altopia.data.local.session.SessionManager
import org.terratec.altopia.data.remote.dto.auth.LoginRequest
import org.terratec.altopia.data.remote.dto.auth.LoginResponse
import org.terratec.altopia.data.remote.dto.auth.RefreshTokenRequest
import org.terratec.altopia.data.remote.dto.auth.UserResponse
import org.terratec.altopia.data.remote.util.safeApiCall

/**
 * Implementation of AuthApiService using Ktor HttpClient.
 * Makes direct API calls to Supabase auth endpoints.
 */
class AuthApiServiceImpl(
    private val httpClient: HttpClient,
    private val sessionManager: SessionManager
) : AuthApiService {
    
    override suspend fun login(email: String, password: String): LoginResponse {
        return safeApiCall {
            httpClient.post("auth/v1/token") {
                parameter("grant_type", "password")
                setBody(LoginRequest(email, password))
            }
        }
    }
    
    override suspend fun logout() {
        val session = sessionManager.getSession()
        session?.let {
            safeApiCall<Unit> {
                httpClient.post("auth/v1/logout") {
                    header("Authorization", "Bearer ${it.accessToken}")
                }
            }
        }
    }
    
    override suspend fun refreshToken(refreshToken: String): LoginResponse {
        return safeApiCall {
            httpClient.post("auth/v1/token") {
                parameter("grant_type", "refresh_token")
                setBody(RefreshTokenRequest(refreshToken))
            }
        }
    }
    
    override suspend fun getCurrentUser(): UserResponse {
        val session = sessionManager.getSession()
            ?: throw IllegalStateException("No active session")
        
        return safeApiCall {
            httpClient.get("auth/v1/user") {
                header("Authorization", "Bearer ${session.accessToken}")
            }
        }
    }

    override suspend fun recoverPassword(email: String) {
        safeApiCall<Unit> {
            httpClient.post("auth/v1/recover") {
                setBody(org.terratec.altopia.data.remote.dto.auth.RecoverPasswordRequest(
                    email = email,
                    redirectTo = "io.altopia.app://auth/callback"
                ))
            }
        }
    }

    override suspend fun updateUser(password: String?, data: Map<String, String>?): UserResponse {
        val session = sessionManager.getSession()
            ?: throw IllegalStateException("No active session")

        return safeApiCall {
            httpClient.put("auth/v1/user") {
                header("Authorization", "Bearer ${session.accessToken}")
                setBody(org.terratec.altopia.data.remote.dto.auth.UserUpdateRequest(password = password, data = data))
            }
        }
    }
}
