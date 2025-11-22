package org.terratec.altopia.data.mapper

import org.terratec.altopia.data.remote.UserDto
import org.terratec.altopia.data.remote.VideoDto
import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.model.Video

object UserMapper {
    fun dtoToDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            name = dto.name,
            email = dto.email
        )
    }

    fun dtoToDomain(dto: VideoDto): Video {
        return Video(
            id = dto.id,
            createdAt = dto.createdAt,
            videoLink = dto.videoLink,
            isFinished = dto.isFinished
        )
    }
}
