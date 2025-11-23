package org.terratec.altopia.domain.usecase.auth

import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.repository.AuthRepository

/**
 * Use case for getting the current authenticated user.
 * Optionally refreshes the session to get updated user data.
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(refresh: Boolean = false): Result<User?> {
        return try {
            if (refresh) {
                authRepository.refreshSession()
                    .map { authRepository.getCurrentUser() }
            } else {
                Result.success(authRepository.getCurrentUser())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
