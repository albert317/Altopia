package org.terratec.altopia.presentation.features.login

import org.terratec.altopia.domain.model.AuthSession

/**
 * One-time events for Login screen.
 * These events are consumed only once by the View.
 */
sealed interface LoginEvent {
    /**
     * Navigate to home screen after successful login.
     */
    data class NavigateToHome(val session: AuthSession) : LoginEvent
    data object NavigateToProfileSelection : LoginEvent
    data object NavigateToAdminDashboard : LoginEvent
    
    /**
     * Navigate to forgot password screen.
     */
    data object NavigateToForgotPassword : LoginEvent
    
    /**
     * Show a toast message.
     */
    data class ShowToast(val message: String) : LoginEvent
}
