package org.terratec.altopia.data.repository

import org.terratec.altopia.data.mapper.UserMapper
import org.terratec.altopia.data.remote.datasource.UserRemoteDataSource
import org.terratec.altopia.data.remote.datasource.VideoRemoteDataSource
import org.terratec.altopia.domain.model.AppUser
import org.terratec.altopia.domain.model.Person
import org.terratec.altopia.domain.model.Role
import org.terratec.altopia.domain.model.Video
import org.terratec.altopia.domain.model.UserProfile
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

    override suspend fun getVideos(): Result<List<Video>> {
        return videoRemoteDataSource.getVideos()
            .map { responses -> responses.map { mapper.videoResponseToDomain(it) } }
    }

    override suspend fun getUserRoles(userId: String): Result<List<Role>> {
        return userRemoteDataSource.getUserRoles(userId)
            .map { list ->
                list.map { mapper.userRoleResponseToDomain(it) }
            }
    }

    override suspend fun getPerson(authId: String): Result<Person> {
        return userRemoteDataSource.getPerson(authId)
            .map { mapper.personResponseToDomain(it) }
    }

    override suspend fun getUserByPersonId(personId: String): Result<AppUser> {
        return userRemoteDataSource.getUserByPersonId(personId)
            .map { mapper.userResponseToDomain(it) }
    }

    override suspend fun getUserProfiles(userId: String): Result<List<UserProfile>> {
        return userRemoteDataSource.getUserProfiles(userId)
            .map { list ->
                list.map { mapper.userProfileResponseToDomain(it) }
            }
    }
}
