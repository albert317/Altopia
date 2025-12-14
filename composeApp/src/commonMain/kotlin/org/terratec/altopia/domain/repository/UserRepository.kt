package org.terratec.altopia.domain.repository

import org.terratec.altopia.domain.model.AppUser
import org.terratec.altopia.domain.model.Person
import org.terratec.altopia.domain.model.UserProfile
import org.terratec.altopia.domain.model.Role
import org.terratec.altopia.domain.model.Video

interface UserRepository {
    suspend fun getVideos(): Result<List<Video>>
    suspend fun getUserRoles(userId: String): Result<List<Role>>
    suspend fun getPerson(authId: String): Result<Person>
    suspend fun getUserByPersonId(personId: String): Result<AppUser>
    suspend fun getUserProfiles(userId: String): Result<List<UserProfile>>
}
