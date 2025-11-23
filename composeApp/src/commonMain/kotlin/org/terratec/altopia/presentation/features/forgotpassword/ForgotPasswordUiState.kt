package org.terratec.altopia.presentation.features.forgotpassword

/**
 * UI state for Forgot Password screen.
 */
data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isEmailValid: Boolean = true,
    val errorMessage: String? = null
)
