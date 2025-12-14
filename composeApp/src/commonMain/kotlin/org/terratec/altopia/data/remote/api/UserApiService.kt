package org.terratec.altopia.data.remote.api

import org.terratec.altopia.data.remote.dto.request.GetUserRolesRequest
import org.terratec.altopia.data.remote.dto.request.GetUserProfilesRequest
import org.terratec.altopia.data.remote.dto.response.PersonResponse
import org.terratec.altopia.data.remote.dto.response.UserProfileResponse
import org.terratec.altopia.data.remote.dto.response.RoleResponse
import org.terratec.altopia.data.remote.dto.response.UserResponse
import org.terratec.altopia.data.remote.dto.response.UserRoleResponse

/**
 * API Service for User-related endpoints.
 * Defines the contract for HTTP requests/responses with the backend API.
 */
interface UserApiService {
    /**
     * Get person details by auth_id
     * @param authId Auth ID from Supabase Auth
     * @return List of PersonResponse (Supabase returns an array)
     */
    suspend fun getPerson(authId: String): List<PersonResponse>
    /**
     * Get a user by ID
     * @param userId User ID
     * @return List of UserResponse DTO
     */
    suspend fun getUser(userId: String): List<UserResponse>

    /**
     * Get all users
     * @return List of UserResponse DTOs
     */
    suspend fun getUsers(): List<UserResponse>

    /**
     * Get user roles by calling Supabase RPC 'get_user_roles'
     * @param request GetUserRolesRequest body with user ID
     * @return List of UserRoleResponse DTOs
     */
    suspend fun getUserRoles(request: GetUserRolesRequest): List<UserRoleResponse>

    /**
     * Get business user (usuarios table) by persona_id
     * @param personId Persona ID (UUID)
     * @return List of UserResponse
     */
    suspend fun getBusinessUserByPersonId(personId: String): List<UserResponse>

    /**
     * Get user profiles by calling Supabase RPC 'obtener_perfiles_usuario'
     * @param request GetUserProfilesRequest body with user ID
     * @return List of UserProfileResponse DTOs
     */
    suspend fun getUserProfiles(request: GetUserProfilesRequest): List<UserProfileResponse>
}
