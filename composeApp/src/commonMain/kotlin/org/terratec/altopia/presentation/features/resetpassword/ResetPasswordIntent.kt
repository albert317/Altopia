package org.terratec.altopia.presentation.features.resetpassword

/**
 * User intents for Reset Password screen.
 */
sealed class ResetPasswordIntent {
    data class PasswordChanged(val password: String) : ResetPasswordIntent()
    data class ConfirmPasswordChanged(val password: String) : ResetPasswordIntent()
    data object SubmitPassword : ResetPasswordIntent()
    data object NavigateToHome : ResetPasswordIntent()
}
