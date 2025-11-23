package org.terratec.altopia.data.local.session

import org.terratec.altopia.domain.model.AuthSession

/**
 * Interface for managing user authentication sessions.
 * Handles secure storage and retrieval of auth tokens and user data.
 */
interface SessionManager {
    /**
     * Saves the authentication session.
     * 
     * @param session The AuthSession to save
     */
    suspend fun saveSession(session: AuthSession)
    
    /**
     * Retrieves the current authentication session.
     * 
     * @return The saved AuthSession or null if no session exists
     */
    suspend fun getSession(): AuthSession?
    
    /**
     * Clears the current authentication session.
     */
    suspend fun clearSession()
    
    /**
     * Updates only the access token and expiration time.
     * Used when refreshing the token.
     * 
     * @param accessToken New access token
     * @param expiresAt New expiration timestamp
     */
    suspend fun updateAccessToken(accessToken: String, expiresAt: Long)
    
    /**
     * Checks if the current session is valid (not expired).
     * 
     * @return true if session exists and is not expired, false otherwise
     */
    fun isSessionValid(): Boolean
}
