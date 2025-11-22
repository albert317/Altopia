package org.terratec.altopia.domain.repository

import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.model.Video

interface UserRepository {
    suspend fun getUser(userId: Long): Result<User>
    suspend fun getUsers(): Result<List<User>>
    suspend fun getVideos(): Result<List<Video>>
}
