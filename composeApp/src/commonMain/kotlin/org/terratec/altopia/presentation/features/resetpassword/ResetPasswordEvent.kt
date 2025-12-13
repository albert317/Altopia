package org.terratec.altopia.presentation.features.resetpassword

/**
 * One-shot events for Reset Password screen.
 */
sealed class ResetPasswordEvent {
    data object NavigateToHome : ResetPasswordEvent()
    data object NavigateToLogin : ResetPasswordEvent()
}
