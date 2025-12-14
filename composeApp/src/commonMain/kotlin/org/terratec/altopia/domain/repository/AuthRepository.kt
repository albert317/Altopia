package org.terratec.altopia.domain.repository
 
import org.terratec.altopia.domain.model.AuthSession
 
/**
  * Repository interface for authentication operations.
  * Handles user login, logout, and session management.
  */
interface AuthRepository {
    /**
     * Authenticates a user with email and password.
     * 
     * @param email User's email address
     * @param password User's password
     * @return Result containing the authenticated AuthSession on success, or an error on failure
     */
    suspend fun login(email: String, password: String): Result<AuthSession>
    
    /**
     * Logs out the current user and clears the session.
     * 
     * @return Result indicating success or failure
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Gets the currently authenticated user session.
     * 
     * @return The current AuthSession or null if not authenticated
     */
    suspend fun getCurrentUser(): AuthSession?
    
    /**
     * Refreshes the current session by obtaining a new access token.
     * 
     * @return Result indicating success or failure
     */
    suspend fun refreshSession(): Result<Unit>
    
    /**
     * Checks if there is a valid authenticated session.
     * 
     * @return true if the user is authenticated and the session is valid, false otherwise
     */
    fun isAuthenticated(): Boolean
    
    /**
     * Sends a password recovery email to the specified email address.
     * 
     * @param email User's email address
     * @return Result indicating success or failure
     */
    suspend fun recoverPassword(email: String): Result<Unit>
    
    /**
     * Updates the user's password.
     * 
     * @param password New password
     * @return Result indicating success or failure
     */
    suspend fun updatePassword(password: String): Result<Unit>
}
