package org.terratec.altopia.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.terratec.altopia.presentation.features.forgotpassword.ForgotPasswordScreen
import org.terratec.altopia.presentation.features.login.LoginScreen
import org.terratec.altopia.presentation.ui.HomeScreen
import org.terratec.altopia.presentation.ui.SplashScreen

/**
 * Main navigation host for the app.
 * Defines all navigation routes and screens.
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: Route = Route.Splash
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Splash> {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
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
                onNavigateToHome = { user ->
                    navController.navigate(Route.Home) {
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
                }
            )
        }
        
        composable<Route.Home> {
            HomeScreen(
                onLogout = {
                    navController.navigate(Route.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
