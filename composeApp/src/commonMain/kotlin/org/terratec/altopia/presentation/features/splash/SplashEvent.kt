package org.terratec.altopia.presentation.features.splash

import org.terratec.altopia.domain.model.AuthSession

/**
 * Events for Splash Screen.
 * One-shot events like navigation.
 */
sealed interface SplashEvent {
    data class NavigateToHome(val session: AuthSession) : SplashEvent
    data object NavigateToLogin : SplashEvent
    data object NavigateToProfileSelection : SplashEvent
    data object NavigateToAdminDashboard : SplashEvent
}
