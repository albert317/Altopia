package org.terratec.altopia.domain.usecase

import org.terratec.altopia.domain.model.Video
import org.terratec.altopia.domain.repository.UserRepository

class GetVideosUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<Video>> {
        return userRepository.getVideos()
    }
}
