package org.terratec.altopia.presentation.features.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.terratec.altopia.domain.model.AuthSession
import org.terratec.altopia.presentation.ui.components.BaseScreen

/**
 * Splash screen that validates user session.
 * Navigates to Home if authenticated, Login otherwise.
 */
@Composable
fun SplashScreen(
    onNavigateToHome: (AuthSession) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileSelection: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SplashEvent.NavigateToHome -> onNavigateToHome(event.session)
                SplashEvent.NavigateToLogin -> onNavigateToLogin()
                SplashEvent.NavigateToProfileSelection -> onNavigateToProfileSelection()
                SplashEvent.NavigateToAdminDashboard -> onNavigateToAdminDashboard()
            }
        }
    }
    
    SplashContent(
        uiState = uiState,
        onIntent = viewModel::setIntent
    )
}

@Composable
private fun SplashContent(
    uiState: SplashUiState,
    onIntent: (SplashIntent) -> Unit
) {
    BaseScreen(
        showProgress = false // We show our own progress indicator in the center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashContent(
            uiState = SplashUiState(isLoading = true),
            onIntent = {}
        )
    }
}
