package org.terratec.altopia.domain.usecase

import org.terratec.altopia.domain.repository.AuthRepository

/**
 * Use case for handling forgot password functionality.
 * Validates email and delegates to repository for Supabase API call.
 */
class ForgotPasswordUseCase(
    private val authRepository: AuthRepository
) {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    /**
     * Sends a password recovery email to the provided address.
     * 
     * @param email User's email address
     * @return Result<Unit> Success if email sent, Failure with error message otherwise
     */
    suspend operator fun invoke(email: String): Result<Unit> {
        // Validation: Empty email
        if (email.isBlank()) {
            return Result.failure(Exception("El correo no puede estar vacío"))
        }
        
        // Validation: Email format
        if (!emailRegex.matches(email)) {
            return Result.failure(Exception("Por favor, ingresa un correo válido"))
        }
        
        return try {
            authRepository.recoverPassword(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                Exception(e.message ?: "Error al enviar el correo de recuperación")
            )
        }
    }
}
