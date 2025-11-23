package org.terratec.altopia.presentation.features.login

/**
 * User intentions/actions for Login screen.
 * Represents all possible user interactions.
 */
sealed interface LoginIntent {
    /**
     * User changed email input.
     */
    data class EmailChanged(val email: String) : LoginIntent
    
    /**
     * User changed password input.
     */
    data class PasswordChanged(val password: String) : LoginIntent
    
    /**
     * User clicked login button.
     */
    data object SubmitCredentials : LoginIntent
    
    /**
     * User dismissed error message.
     */
    data object ClearError : LoginIntent
    
    /**
     * User clicked forgot password link.
     */
    data object ForgotPasswordClicked : LoginIntent
}
