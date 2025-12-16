package org.terratec.altopia.presentation.features.profile_selection

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.terratec.altopia.domain.model.RoleType
import org.terratec.altopia.domain.usecase.auth.GetAuthSessionLocalUseCase
import org.terratec.altopia.domain.usecase.user.GetPersonUseCase
import org.terratec.altopia.domain.usecase.user.GetUserProfilesUseCase
import org.terratec.altopia.domain.usecase.user.GetUserUseCase
import org.terratec.altopia.presentation.util.executeTask
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

    override fun handleIntent(intent: ProfileSelectionIntent) {
        when (intent) {
            is ProfileSelectionIntent.SelectOption -> {
                setUiState { copy(selectedOption = intent.option) }
            }
            ProfileSelectionIntent.ConfirmSelection -> handleConfirmation()
            is ProfileSelectionIntent.CheckPerson -> handleCheckPerson(intent.session)
            is ProfileSelectionIntent.CheckBusinessUser -> handleCheckBusinessUser(intent.personId, intent.session)
            is ProfileSelectionIntent.CheckProfiles -> handleCheckProfiles(intent.businessUserId, intent.session)
        }
    }

    private fun loadProfileOptions() {
        setUiState { copy(isLoading = true) }
        
        executeTask<org.terratec.altopia.domain.model.AuthSession?>(
            onSuccess = { session ->
                if (session != null) {
                    setIntent(ProfileSelectionIntent.CheckPerson(session))
                } else {
                    setUiState { copy(isLoading = false, error = "No active session") }
                }
            },
            onFailure = { 
                setUiState { copy(isLoading = false, error = "No active session") }
            }
        ) {
             getAuthSessionLocalUseCase().getOrThrow()
        }
    }

    private fun handleCheckPerson(session: org.terratec.altopia.domain.model.AuthSession) {
        executeTask<org.terratec.altopia.domain.model.Person>(
            onSuccess = { person ->
                setUiState { copy(userName = "${person.nombre} ${person.apellidos}") }
                setIntent(ProfileSelectionIntent.CheckBusinessUser(person.id, session))
            },
            onFailure = {
                 setUiState { copy(isLoading = false, error = "Person not found") }
            }
        ) {
            getPersonUseCase(session.user?.id ?: "").getOrThrow()
        }
    }

    private fun handleCheckBusinessUser(personId: String, session: org.terratec.altopia.domain.model.AuthSession) {
        executeTask<org.terratec.altopia.domain.model.AppUser>(
            onSuccess = { businessUser ->
                 setIntent(ProfileSelectionIntent.CheckProfiles(businessUser.id, session))
            },
            onFailure = {
                 setUiState { copy(isLoading = false, error = "Business User not found") }
            }
        ) {
            getUserUseCase(personId).getOrThrow()
        }
    }

    private fun handleCheckProfiles(businessUserId: String, session: org.terratec.altopia.domain.model.AuthSession) {
        executeTask<List<org.terratec.altopia.domain.model.UserProfile>>(
            onSuccess = { profiles ->
                 // Need person details to update UI properly, assume we can get it from session or reload it, 
                 // but typically chained intents pass data. 
                 // For now, let's just use what we have or reload person if needed.
                 // Ideally, the previous intent should have passed the Person object or we store it in a temporary state/variable if needed.
                 // However, MVI suggests State is the source of truth. 
                 // Let's reload Person quickly or assume we modify the specific handler to pass Person.
                 // Better pattern: CheckBusinessUser should assume we have Person data. 
                 
                 // REVISIT: To avoid reloading Person, we should probably pass it down or update UiState incrementally.
                 // Updating UiState incrementally is safer.
                 
                 // Process profiles
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

                // NOTE: We need the person name here. 
                // Since this is a chain, we lost the Person object from previous scope unless we passed it.
                // To solve this cleanly without passing too much data in Intent, 
                // we can update UiState with Person name in handleCheckPerson.
                
                setUiState {
                    copy(
                        isLoading = false,
                        // userName = ... wait, we need to update userName in HandleCheckPerson!
                        availableOptions = options,
                        selectedOption = options.firstOrNull()
                    )
                }
            },
            onFailure = { error ->
                setUiState {
                    copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        ) {
            getUserProfilesUseCase(businessUserId).getOrThrow()
        }
    }

    private fun handleConfirmation() {
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
