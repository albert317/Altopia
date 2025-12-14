package org.terratec.altopia.domain.usecase.user

import org.terratec.altopia.domain.model.UserProfile
import org.terratec.altopia.domain.repository.UserRepository

class GetUserProfilesUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<List<UserProfile>> {
        return userRepository.getUserProfiles(userId)
    }
}
