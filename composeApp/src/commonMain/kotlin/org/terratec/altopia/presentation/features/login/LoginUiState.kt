package org.terratec.altopia.presentation.features.login

/**
 * UI State for Login screen.
 * Represents the complete state of the login UI.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true
)
