package org.terratec.altopia.presentation.features.forgotpassword

/**
 * One-shot events for Forgot Password screen.
 */
sealed class ForgotPasswordEvent {
    data object NavigateToLogin : ForgotPasswordEvent()
}
