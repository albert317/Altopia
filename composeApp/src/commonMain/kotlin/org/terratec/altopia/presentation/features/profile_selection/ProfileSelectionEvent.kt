package org.terratec.altopia.presentation.features.profile_selection

sealed interface ProfileSelectionEvent {
    data object NavigateToOwnerHome : ProfileSelectionEvent
    data object NavigateToAdminDashboard : ProfileSelectionEvent
}
