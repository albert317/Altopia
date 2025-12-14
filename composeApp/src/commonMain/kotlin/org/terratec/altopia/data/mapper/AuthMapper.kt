package org.terratec.altopia.data.mapper

import org.terratec.altopia.data.remote.dto.auth.LoginResponse
import org.terratec.altopia.data.remote.dto.auth.UserResponse
import org.terratec.altopia.domain.model.AuthSession

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
            tokenType = response.tokenType,
            expiresIn = response.expiresIn,
            expiresAt = response.expiresAt,
            refreshToken = response.refreshToken,
            user = mapUser(response.user),
            weakPassword = response.weakPassword
        )
    }

    private fun mapUser(userResponse: UserResponse): org.terratec.altopia.domain.model.User {
        return org.terratec.altopia.domain.model.User(
            id = userResponse.id,
            aud = userResponse.aud,
            role = userResponse.role,
            email = userResponse.email,
            phone = userResponse.phone,
            emailConfirmedAt = userResponse.emailConfirmedAt,
            lastSignInAt = userResponse.lastSignInAt,
            userMetadata = mapUserMetadata(userResponse.userMetadata),
            createdAt = userResponse.createdAt,
            updatedAt = userResponse.updatedAt,
            isAnonymous = userResponse.isAnonymous
        )
    }

    private fun mapUserMetadata(metadata: org.terratec.altopia.data.remote.dto.auth.UserMetadata?): org.terratec.altopia.domain.model.UserMetadata? {
        return metadata?.let {
            org.terratec.altopia.domain.model.UserMetadata(
                name = it.name,
                avatarUrl = it.avatarUrl,
                emailVerified = it.emailVerified
            )
        }
    }
}
