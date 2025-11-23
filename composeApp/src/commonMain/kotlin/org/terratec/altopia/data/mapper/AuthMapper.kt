package org.terratec.altopia.data.mapper

import org.terratec.altopia.data.remote.dto.auth.LoginResponse
import org.terratec.altopia.data.remote.dto.auth.UserResponse
import org.terratec.altopia.domain.model.AuthSession
import org.terratec.altopia.domain.model.User

/**
 * Mapper for converting auth DTOs to domain models.
 */
class AuthMapper {
    
    /**
     * Converts LoginResponse to AuthSession.
     */
    fun loginResponseToAuthSession(response: LoginResponse): AuthSession {
        return AuthSession(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresAt = response.expiresAt,
            user = userResponseToDomain(response.user)
        )
    }
    
    /**
     * Converts UserResponse to User domain model.
     * Note: Supabase user ID is a UUID string, but domain User has Long id.
     * For now, we use a hash of the UUID string as the Long id.
     */
    fun userResponseToDomain(response: UserResponse): User {
        return User(
            id = response.id.hashCode().toLong(),
            name = response.userMetadata?.name ?: response.email.substringBefore('@'),
            email = response.email
        )
    }
}
