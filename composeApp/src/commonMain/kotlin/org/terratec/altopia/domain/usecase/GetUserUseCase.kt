package org.terratec.altopia.domain.usecase

import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.repository.UserRepository

class GetUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(userId: Long): Result<User> {
        return repository.getUser(userId)
    }
}
