package org.terratec.altopia.presentation.features.login

import org.terratec.altopia.domain.model.AuthSession

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

    // Chained Intents for Post-Login Validation
    data class CheckPerson(val session: AuthSession) : LoginIntent
    data class CheckUser(val personId: String, val session: AuthSession) : LoginIntent
    data class CheckProfiles(val businessUserId: String, val session: AuthSession) : LoginIntent
}
