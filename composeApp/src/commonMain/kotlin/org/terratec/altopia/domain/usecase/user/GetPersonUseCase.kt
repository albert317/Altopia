package org.terratec.altopia.domain.usecase.user

import org.terratec.altopia.domain.model.Person
import org.terratec.altopia.domain.repository.UserRepository

class GetPersonUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(authId: String): Result<Person> {
        return userRepository.getPerson(authId)
    }
}
