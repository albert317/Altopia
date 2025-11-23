package org.terratec.altopia.presentation.features.forgotpassword

/**
 * User intents for Forgot Password screen.
 */
sealed class ForgotPasswordIntent {
    data class EmailChanged(val email: String) : ForgotPasswordIntent()
    data object SubmitEmail : ForgotPasswordIntent()
    data object NavigateBack : ForgotPasswordIntent()
}
