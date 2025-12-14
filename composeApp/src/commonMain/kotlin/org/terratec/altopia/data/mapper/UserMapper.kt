package org.terratec.altopia.data.mapper

import org.terratec.altopia.data.remote.dto.response.PersonResponse
import org.terratec.altopia.data.remote.dto.response.UserResponse
import org.terratec.altopia.data.remote.dto.response.UserRoleResponse
import org.terratec.altopia.data.remote.dto.response.VideoResponse
import org.terratec.altopia.data.remote.dto.response.UserProfileResponse
import org.terratec.altopia.domain.model.AppUser
import org.terratec.altopia.domain.model.Person
import org.terratec.altopia.domain.model.UserProfile
import org.terratec.altopia.domain.model.Role
import org.terratec.altopia.domain.model.Video

object UserMapper {

    fun videoResponseToDomain(response: VideoResponse): Video {
        return Video(
            id = response.id,
            createdAt = response.createdAt,
            videoLink = response.videoLink,
            isFinished = response.isFinished
        )
    }

    fun userRoleResponseToDomain(response: UserRoleResponse): Role {
        return Role(
            id = response.userId.hashCode().coerceAtLeast(0), // Generating temporary ID from UUID hash
            name = try {
                org.terratec.altopia.domain.model.RoleType.valueOf(response.roleName)
            } catch (e: Exception) {
                org.terratec.altopia.domain.model.RoleType.INQUILINO
            },
            description = null
        )
    }

    fun personResponseToDomain(response: PersonResponse): Person {
        return Person(
            id = response.id,
            nombre = response.nombre,
            apellidos = response.apellidos,
            dniRuc = response.dniRuc,
            telefono = response.telefono,
            emailContacto = response.emailContacto,
            createdAt = response.createdAt,
            authId = response.authId
        )
    }

    fun userResponseToDomain(response: UserResponse): AppUser {
        return AppUser(
            id = response.id,
            personaId = response.personaId,
            estado = response.estado,
            createdAt = response.createdAt
        )
    }

    fun userProfileResponseToDomain(response: UserProfileResponse): UserProfile {
        return UserProfile(
            profileType = response.profileType,
            unitId = response.unitId,
            unitCode = response.unitCode,
            unitFloor = response.unitFloor,
            unitAreaCoefficient = response.unitAreaCoefficient,
            unitUsageType = response.unitUsageType,
            blockId = response.blockId,
            blockName = response.blockName,
            blockDescription = response.blockDescription,
            condoId = response.condoId
        )
    }
}
