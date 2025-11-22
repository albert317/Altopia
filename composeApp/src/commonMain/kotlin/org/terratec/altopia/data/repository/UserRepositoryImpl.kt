package org.terratec.altopia.data.repository

import org.terratec.altopia.data.mapper.UserMapper
import org.terratec.altopia.data.remote.datasource.UserRemoteDataSource
import org.terratec.altopia.data.remote.datasource.VideoRemoteDataSource
import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.model.Video
import org.terratec.altopia.domain.repository.UserRepository

/**
 * Implementation of UserRepository.
 * Coordinates data from remote data sources and maps DTOs to domain models.
 */
class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val mapper: UserMapper
) : UserRepository {

    override suspend fun getUser(userId: Long): Result<User> {
        return userRemoteDataSource.getUser(userId)
            .map { response -> mapper.userResponseToDomain(response) }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return userRemoteDataSource.getUsers()
            .map { responses -> responses.map { mapper.userResponseToDomain(it) } }
    }

    override suspend fun getVideos(): Result<List<Video>> {
        return videoRemoteDataSource.getVideos()
            .map { responses -> responses.map { mapper.videoResponseToDomain(it) } }
    }
}
