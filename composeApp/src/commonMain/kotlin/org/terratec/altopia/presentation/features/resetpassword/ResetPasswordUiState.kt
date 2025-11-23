package org.terratec.altopia.presentation.features.resetpassword

/**
 * UI state for Reset Password screen.
 */
data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
