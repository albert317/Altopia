package org.terratec.altopia.domain.usecase

import org.terratec.altopia.domain.repository.AuthRepository

/**
 * Use case for updating the user's password.
 */
class UpdatePasswordUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Updates the password for the authenticated user.
     * 
     * @param password New password
     * @return Result<Unit> Success or Failure
     */
    suspend operator fun invoke(password: String): Result<Unit> {
        // Validation
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        
        return authRepository.updatePassword(password)
    }
}
