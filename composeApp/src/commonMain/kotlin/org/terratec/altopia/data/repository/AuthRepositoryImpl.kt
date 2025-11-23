package org.terratec.altopia.data.repository

import org.terratec.altopia.data.local.session.SessionManager
import org.terratec.altopia.data.mapper.AuthMapper
import org.terratec.altopia.data.remote.api.AuthApiService
import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.repository.AuthRepository

/**
 * Implementation of AuthRepository.
 * Coordinates authentication operations between API service and session manager.
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager,
    private val mapper: AuthMapper
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApiService.login(email, password)
            val session = mapper.loginResponseToAuthSession(response)
            sessionManager.saveSession(session)
            Result.success(session.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            // Try to logout from server
            authApiService.logout()
            // Always clear local session, even if server logout fails
            sessionManager.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            // Clear local session even if server logout fails
            sessionManager.clearSession()
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): User? {
        return sessionManager.getSession()?.user
    }
    
    override suspend fun refreshSession(): Result<Unit> {
        return try {
            val currentSession = sessionManager.getSession()
                ?: return Result.failure(Exception("No active session to refresh"))
            
            val response = authApiService.refreshToken(currentSession.refreshToken)
            val newSession = mapper.loginResponseToAuthSession(response)
            sessionManager.saveSession(newSession)
            
            Result.success(Unit)
        } catch (e: Exception) {
            // If refresh fails, clear the session
            sessionManager.clearSession()
            Result.failure(e)
        }
    }
    
    override fun isAuthenticated(): Boolean {
        return sessionManager.isSessionValid()
    }
}
