package org.terratec.altopia.presentation.features.profile_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.terratec.altopia.domain.model.RoleType
import org.terratec.altopia.domain.usecase.auth.GetCurrentUserUseCase

class ProfileSelectionViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSelectionContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ProfileSelectionContract.Event>()
    val event = _event.receiveAsFlow()

    init {
        loadProfileOptions()
    }

    fun setIntent(intent: ProfileSelectionContract.Intent) {
        when (intent) {
            is ProfileSelectionContract.Intent.SelectOption -> {
                _uiState.value = _uiState.value.copy(selectedOption = intent.option)
            }
            ProfileSelectionContract.Intent.ConfirmSelection -> handleConfirmation()
        }
    }

    private fun loadProfileOptions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getCurrentUserUseCase()
                .onSuccess { user ->
                    user?.let {
                        val options = mutableListOf<ProfileSelectionContract.ProfileOption>()

                        // Add Admin Option
                        if (it.roles.any { role -> role.name == RoleType.ADMIN }) {
                            options.add(
                                ProfileSelectionContract.ProfileOption(
                                    id = "ADMIN_DASHBOARD",
                                    type = RoleType.ADMIN,
                                    title = "Administrador",
                                    subtitle = "Gestión global del sistema"
                                )
                            )
                        }

                        // Add Property Options
                        it.properties.forEach { props ->
                            options.add(
                                ProfileSelectionContract.ProfileOption(
                                    id = props.id,
                                    type = RoleType.PROPIETARIO,
                                    propertyId = props.id,
                                    title = props.unitCode,
                                    subtitle = props.condoName
                                )
                            )
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            userName = it.name,
                            availableOptions = options,
                            selectedOption = options.firstOrNull() // Default select first
                        )
                    }
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    // In real app, handle error
                }
        }
    }

    private fun handleConfirmation() {
        viewModelScope.launch {
            val selected = _uiState.value.selectedOption
            if (selected != null) {
                if (selected.type == RoleType.ADMIN) {
                    _event.send(ProfileSelectionContract.Event.NavigateToAdminDashboard)
                } else {
                    // Logic to set "current property" context could go here
                    _event.send(ProfileSelectionContract.Event.NavigateToOwnerHome)
                }
            }
        }
    }
}
