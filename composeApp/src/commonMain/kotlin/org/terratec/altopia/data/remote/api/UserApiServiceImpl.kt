package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.terratec.altopia.data.remote.dto.request.GetUserProfilesRequest
import org.terratec.altopia.data.remote.dto.request.GetUserRolesRequest
import org.terratec.altopia.data.remote.dto.response.PersonResponse
import org.terratec.altopia.data.remote.dto.response.UserProfileResponse
import org.terratec.altopia.data.remote.dto.response.RoleResponse
import org.terratec.altopia.data.remote.dto.response.UserRoleResponse
import org.terratec.altopia.data.remote.dto.response.UserResponse
import org.terratec.altopia.data.remote.util.safeApiCall

/**
 * Implementation of UserApiService using Ktor HttpClient.
 * Handles the actual HTTP communication with the backend API.
 * Uses a pre-configured HttpClient with base URL already set.
 */
class UserApiServiceImpl(
    private val httpClient: HttpClient
) : UserApiService {
    override suspend fun getPerson(authId: String): List<PersonResponse> {
        return safeApiCall {
            httpClient.get("personas") {
                url {
                    parameters.append("auth_id", "eq.$authId")
                    parameters.append("select", "*")
                    parameters.append("limit", "1")
                }
            }
        }
    }

    override suspend fun getUser(userId: String): List<UserResponse> {
        return safeApiCall {
             httpClient.get("usuarios") {
                url {
                    parameters.append("id", "eq.$userId")
                    parameters.append("select", "*")
                    parameters.append("limit", "1")
                }
            }
        }
    }

    override suspend fun getUsers(): List<UserResponse> {
        // Use relative URL without leading slash - base URL is configured in the injected HttpClient
        return safeApiCall {
            httpClient.get("usuarios")
        }
    }

    override suspend fun getUserRoles(request: GetUserRolesRequest): List<UserRoleResponse> {
        return safeApiCall {
            httpClient.post("rpc/get_user_roles") {
                setBody(request)
            }
        }
    }

    override suspend fun getBusinessUserByPersonId(personId: String): List<UserResponse> {
        return safeApiCall {
            httpClient.get("usuarios") {
                url.parameters.append("persona_id", "eq.$personId")
                url.parameters.append("limit", "1")
            }
        }
    }

    override suspend fun getUserProfiles(request: GetUserProfilesRequest): List<UserProfileResponse> {
        return safeApiCall {
            httpClient.post("rpc/obtener_perfiles_usuario") {
                setBody(request)
            }
        }
    }
}
