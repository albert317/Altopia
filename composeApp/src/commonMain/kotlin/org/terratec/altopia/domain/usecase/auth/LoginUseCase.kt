package org.terratec.altopia.domain.usecase.auth

import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.repository.AuthRepository

/**
 * Use case for user login with email and password.
 * Validates input and delegates to the repository.
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validate email
        if (email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        if (!emailRegex.matches(email)) {
            return Result.failure(Exception("El email no es válido"))
        }
        
        // Validate password
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        
        return authRepository.login(email, password)
    }
}
