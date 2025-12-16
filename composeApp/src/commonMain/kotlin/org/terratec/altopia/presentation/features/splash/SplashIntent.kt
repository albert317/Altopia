package org.terratec.altopia.presentation.features.splash

import org.terratec.altopia.domain.model.AuthSession

/**
 * Intents for Splash Screen.
 * Actions that can be triggered by the UI.
 */
sealed interface SplashIntent {
    data object CheckSession : SplashIntent
    data class CheckPerson(val session: AuthSession) : SplashIntent
    data class CheckUser(val personId: String, val session: AuthSession) : SplashIntent
    data class CheckProfiles(val businessUserId: String, val session: AuthSession) : SplashIntent
}
