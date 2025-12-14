package org.terratec.altopia.domain.usecase.auth

import org.terratec.altopia.domain.model.AuthSession
import org.terratec.altopia.domain.repository.AuthRepository

/**
 * Use case for getting the current authenticated user session.
 * Optionally refreshes the session to get updated user data.
 */
class GetAuthSessionLocalUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<AuthSession?> {
        return try {
            Result.success(authRepository.getCurrentUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
