package org.terratec.altopia.presentation.features.splash

/**
 * UI State for Splash Screen.
 * Simple state as we only show loading or nothing before navigation.
 */
data class SplashUiState(
    val isLoading: Boolean = true
)
