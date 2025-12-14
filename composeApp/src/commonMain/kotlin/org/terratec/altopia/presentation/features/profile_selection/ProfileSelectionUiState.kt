package org.terratec.altopia.presentation.features.profile_selection

import org.terratec.altopia.domain.model.RoleType

data class ProfileSelectionUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val availableOptions: List<ProfileOption> = emptyList(),
    val selectedOption: ProfileOption? = null,
    val error: String? = null
)

data class ProfileOption(
    val id: String,
    val type: RoleType,
    val propertyId: String? = null,
    val title: String,
    val subtitle: String? = null
)
