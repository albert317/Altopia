package org.terratec.altopia.presentation.ui

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
import org.koin.compose.viewmodel.koinViewModel
import org.terratec.altopia.domain.model.AuthSession
import org.terratec.altopia.presentation.viewmodel.SplashUiState
import org.terratec.altopia.presentation.viewmodel.SplashViewModel

/**
 * Splash screen that validates user session.
 * Navigates to Home if authenticated, Login otherwise.
 */
@Composable
fun SplashScreen(
    onNavigateToHome: (AuthSession) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is SplashUiState.NavigateToHome -> onNavigateToHome(state.session)
            is SplashUiState.NavigateToLogin -> onNavigateToLogin()
            SplashUiState.Loading -> { /* Stay on splash */ }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}
