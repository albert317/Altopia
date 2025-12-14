package org.terratec.altopia.domain.usecase

import org.terratec.altopia.domain.model.Role
import org.terratec.altopia.domain.repository.UserRepository

class GetUserRolesUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Role>> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be blank"))
        }
        return repository.getUserRoles(userId)
    }
}
