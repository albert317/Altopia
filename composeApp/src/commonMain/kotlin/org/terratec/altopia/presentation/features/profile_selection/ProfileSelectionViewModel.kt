package org.terratec.altopia.presentation.features.profile_selection

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.terratec.altopia.domain.model.RoleType
import org.terratec.altopia.domain.usecase.auth.GetAuthSessionLocalUseCase
import org.terratec.altopia.domain.usecase.user.GetPersonUseCase
import org.terratec.altopia.domain.usecase.user.GetUserProfilesUseCase
import org.terratec.altopia.domain.usecase.user.GetUserUseCase
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

class ProfileSelectionViewModel(
    private val getAuthSessionLocalUseCase: GetAuthSessionLocalUseCase,
    private val getPersonUseCase: GetPersonUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getUserProfilesUseCase: GetUserProfilesUseCase
) : BaseViewModel<ProfileSelectionUiState, ProfileSelectionIntent, ProfileSelectionEvent>() {

    override fun createInitialState(): ProfileSelectionUiState = ProfileSelectionUiState()

    init {
        loadProfileOptions()
    }

    override suspend fun handleIntent(intent: ProfileSelectionIntent) {
        when (intent) {
            is ProfileSelectionIntent.SelectOption -> {
                setUiState { copy(selectedOption = intent.option) }
            }

            ProfileSelectionIntent.ConfirmSelection -> handleConfirmation()
        }
    }

    private fun loadProfileOptions() {
        setUiState { copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // 1. Get Current User Session (Auth)
                val userResult = getAuthSessionLocalUseCase()
                val session = userResult.getOrNull()

                // Access ID safely
                val userId = session?.user?.id

                if (userId != null) {
                    // 2. Get Person details using Auth ID
                    val person = getPersonUseCase(userId).getOrNull()
                        ?: throw Exception("Person not found for authId: $userId")

                    // 3. Get Business User using Person ID
                    val businessUser = getUserUseCase(person.id).getOrNull()
                        ?: throw Exception("Business User not found for personId: ${person.id}")

                    println("USER: $businessUser")

                    // 4. Fetch User Profiles using Business User ID
                    val profilesResult = getUserProfilesUseCase(businessUser.id)

                    profilesResult.onSuccess { profiles ->
                        val options = profiles.mapNotNull { profile ->
                            when (profile.profileType) {
                                "ADMINISTRADOR", "ADMIN" -> {
                                    ProfileOption(
                                        id = "ADMIN_DASHBOARD",
                                        type = RoleType.ADMIN,
                                        title = "Administrador",
                                        subtitle = "Gestión global del sistema"
                                    )
                                }

                                "PROPIETARIO", "INQUILINO", "FAMILIAR" -> {
                                    val unitInfo = profile.unitCode ?: "Sin Unidad"
                                    val blockInfo = profile.blockName ?: "Sin Bloque"

                                    val type = when (profile.profileType) {
                                        "INQUILINO" -> RoleType.INQUILINO
                                        else -> RoleType.PROPIETARIO
                                    }

                                    ProfileOption(
                                        id = profile.unitId ?: "UNKNOWN_${profile.hashCode()}",
                                        type = type,
                                        propertyId = profile.unitId,
                                        title = "Unidad $unitInfo",
                                        subtitle = "Bloque $blockInfo - ${profile.profileType}"
                                    )
                                }

                                else -> null
                            }
                        }

                        setUiState {
                            copy(
                                isLoading = false,
                                userName = "${person.nombre} ${person.apellidos}",
                                availableOptions = options,
                                selectedOption = options.firstOrNull()
                            )
                        }
                    }.onFailure { error ->
                        setUiState {
                            copy(
                                isLoading = false,
                                error = error.message
                            )
                        }
                    }
                } else {
                    setUiState { copy(isLoading = false, error = "No active session") }
                }
            } catch (e: Exception) {
                setUiState { copy(isLoading = false, error = e.message) }
                println("Error loading profile options: ${e.message}")
            }
        }
    }

    private fun handleConfirmation() {
        viewModelScope.launch {
            val selected = uiState.value.selectedOption
            if (selected != null) {
                if (selected.type == RoleType.ADMIN) {
                    setEvent(ProfileSelectionEvent.NavigateToAdminDashboard)
                } else {
                    setEvent(ProfileSelectionEvent.NavigateToOwnerHome)
                }
            }
        }
    }
}
