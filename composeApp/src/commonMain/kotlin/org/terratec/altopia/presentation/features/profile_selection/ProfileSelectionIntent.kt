package org.terratec.altopia.presentation.features.profile_selection

sealed interface ProfileSelectionIntent {
    data class SelectOption(val option: ProfileOption) : ProfileSelectionIntent
    data object ConfirmSelection : ProfileSelectionIntent
}
