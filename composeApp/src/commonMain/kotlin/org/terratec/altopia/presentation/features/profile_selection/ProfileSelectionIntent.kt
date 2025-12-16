package org.terratec.altopia.presentation.features.profile_selection

sealed interface ProfileSelectionIntent {
    data class SelectOption(val option: ProfileOption) : ProfileSelectionIntent
    data object ConfirmSelection : ProfileSelectionIntent

    // Chained Intents for Profile Loading
    data class CheckPerson(val session: org.terratec.altopia.domain.model.AuthSession) : ProfileSelectionIntent
    data class CheckBusinessUser(val personId: String, val session: org.terratec.altopia.domain.model.AuthSession) : ProfileSelectionIntent
    data class CheckProfiles(val businessUserId: String, val session: org.terratec.altopia.domain.model.AuthSession) : ProfileSelectionIntent
}
