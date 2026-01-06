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
import org.terratec.altopia.presentation.features.splash.SplashScreen
import org.terratec.altopia.presentation.features.admindashboard.AdminDashboardScreen
import org.terratec.altopia.presentation.features.units.UnitsScreen // Added import for UnitsScreen

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
                    // Navigate to Home, clearing back stack
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToProfileSelection = {
                    navController.navigate(Route.ProfileSelection) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToAdminDashboard = {
                    // Route.Dashboard exists now.
                    navController.navigate(Route.AdminDashboard) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Route.Login> {
            LoginScreen(
                onNavigateToHome = { session ->
                    // Navigate to Home, clearing back stack
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Route.ForgotPassword)
                },
                onNavigateToProfileSelection = {
                    navController.navigate(Route.ProfileSelection) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToAdminDashboard = {
                    navController.navigate(Route.AdminDashboard) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
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
                    navController.navigate(Route.AdminDashboard) {
                        popUpTo(Route.ProfileSelection) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.AdminDashboard> {
            AdminDashboardScreen(
                onNavigateToUnits = { navController.navigate(Route.Units) }, // Implemented navigation to Units
                onNavigateToUsers = { /* TODO */ },
                onNavigateToDistribution = { /* TODO */ },
                onNavigateToExpenses = { /* TODO */ },
                onNavigateToTransactions = { /* TODO */ }
            )
        }

        composable<Route.Units> {
            UnitsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

/**
 * Determines the next screen based on user roles and properties.
 */
private fun determineStartDestination(session: AuthSession): Route {
    // Logic temporarily simplified as appRoles and properties are removed from AuthSession/User
    // They should be fetched via GetUserProfileUseCase if needed logic relies on them.
    // Defaulting to ProfileSelection as safe fallback.
    return Route.ProfileSelection
}