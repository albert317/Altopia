package org.terratec.altopia.domain.usecase.auth

import org.terratec.altopia.domain.repository.AuthRepository

/**
 * Use case for checking if the user is currently authenticated.
 * Returns true if there is a valid session, false otherwise.
 */
class IsAuthenticatedUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.isAuthenticated()
    }
}
