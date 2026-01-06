package org.terratec.altopia.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation routes for the app.
 * Using @Serializable for type-safe navigation.
 */
sealed interface Route {
    @Serializable
    data object Splash : Route
    
    @Serializable
    data object Login : Route
    
    @Serializable
    data object ForgotPassword : Route
    
    @Serializable
    data object ResetPassword : Route
    
    @Serializable
    data object Home : Route

    @Serializable
    data object ProfileSelection : Route

    @Serializable
    data object AdminDashboard : Route

    @Serializable
    data object Units : Route
}
