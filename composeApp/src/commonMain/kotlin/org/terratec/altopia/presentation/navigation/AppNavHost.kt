package org.terratec.altopia.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import org.terratec.altopia.presentation.features.forgotpassword.ForgotPasswordScreen
import org.terratec.altopia.presentation.features.login.LoginScreen
import org.terratec.altopia.presentation.features.home.HomeScreen
import org.terratec.altopia.presentation.features.profile_selection.ProfileSelectionScreen
import org.terratec.altopia.presentation.ui.SplashScreen
import org.terratec.altopia.domain.model.RoleType
import org.terratec.altopia.domain.model.AuthSession

/**
 * Main navigation host for the app.
 * Defines all navigation routes and screens.
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: Route = Route.Splash
) {
    val deepLinkHandler: DeepLinkHandler = koinInject()
    
    LaunchedEffect(Unit) {
        deepLinkHandler.deepLinkEvent.collect { route ->
            navController.navigate(route) {
                // Clear back stack if needed, or just navigate
                // For ResetPassword, we might want to clear previous screens
                if (route is Route.ResetPassword) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Splash> {
            SplashScreen(
                onNavigateToHome = { session ->
                    val destination = determineStartDestination(session)
                    navController.navigate(destination) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Route.Login> {
            LoginScreen(
                onNavigateToHome = { session ->
                    val destination = determineStartDestination(session)
                    navController.navigate(destination) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Route.ForgotPassword)
                }
            )
        }
        
        composable<Route.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable<Route.ResetPassword> {
            org.terratec.altopia.presentation.features.resetpassword.ResetPasswordScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Route.Home> {
            HomeScreen(
                onNavigateToPayments = {
                    // TODO: Implement Payment Navigation
                }
            )
        }

        composable<Route.ProfileSelection> {
            ProfileSelectionScreen(
                onNavigateToOwnerHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.ProfileSelection)
                    }
                },
                onNavigateToAdminDashboard = {
                    // TODO: Implement Admin Dashboard Navigation
                    // For now, go to Home as fallback or show placeholder
                }
            )
        }
    }
}

/**
 * Determines the next screen based on user roles and properties.
 */
private fun determineStartDestination(session: AuthSession): Route {
    val isAdmin = session.user.appRoles.any { it.name == RoleType.ADMIN }
    val isOwner = session.user.appRoles.any { it.name == RoleType.PROPIETARIO }
    val propertiesCount = session.user.properties.size
    
    return when {
         // Case A: Admin only -> Admin Dashboard (Pending, redirect to ProfileSelection for now if mixed, or Home if not implemented)
        isAdmin && propertiesCount == 0 -> Route.ProfileSelection // Or AdminDashboardRoute when ready
        
        // Case B: Owner w/ Single Property -> Home (Dashboard)
        !isAdmin && isOwner && propertiesCount == 1 -> Route.Home
        
        // Case C: Mixed or Multiple -> Profile Selection
        else -> Route.ProfileSelection
    }
}
