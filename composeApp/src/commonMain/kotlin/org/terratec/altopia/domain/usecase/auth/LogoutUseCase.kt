package org.terratec.altopia.domain.usecase.auth

import org.terratec.altopia.domain.repository.AuthRepository

/**
 * Use case for logging out the current user.
 * Clears the session and invalidates tokens.
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
