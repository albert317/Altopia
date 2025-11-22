package org.terratec.altopia.data.mapper

import org.terratec.altopia.data.remote.dto.response.UserResponse
import org.terratec.altopia.data.remote.dto.response.VideoResponse
import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.model.Video

object UserMapper {
    fun userResponseToDomain(response: UserResponse): User {
        return User(
            id = response.id,
            name = response.name,
            email = response.email
        )
    }

    fun videoResponseToDomain(response: VideoResponse): Video {
        return Video(
            id = response.id,
            createdAt = response.createdAt,
            videoLink = response.videoLink,
            isFinished = response.isFinished
        )
    }
}
