package org.terratec.altopia.presentation.features.profile_selection

import androidx.compose.runtime.Immutable
import org.terratec.altopia.domain.model.Property
import org.terratec.altopia.domain.model.RoleType

object ProfileSelectionContract {
    @Immutable
    data class UiState(
        val isLoading: Boolean = false,
        val userName: String = "",
        val availableOptions: List<ProfileOption> = emptyList(),
        val selectedOption: ProfileOption? = null
    )

    sealed interface Intent {
        data class SelectOption(val option: ProfileOption) : Intent
        data object ConfirmSelection : Intent
    }

    sealed interface Event {
        data object NavigateToOwnerHome : Event
        data object NavigateToAdminDashboard : Event
    }

    // Model for the selection list
    data class ProfileOption(
        val id: String,
        val type: RoleType,
        val title: String,
        val subtitle: String,
        val propertyId: String? = null
    )
}
