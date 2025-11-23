package org.terratec.altopia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.terratec.altopia.domain.usecase.auth.IsAuthenticatedUseCase

/**
 * ViewModel for Splash screen.
 * Validates if there's an active session and navigates accordingly.
 */
class SplashViewModel(
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthentication()
    }
    
    private fun checkAuthentication() {
        viewModelScope.launch {
            // Delay of 1 second + session validation time
            delay(1000)
            
            val isAuthenticated = isAuthenticatedUseCase()
            
            _uiState.value = if (isAuthenticated) {
                SplashUiState.NavigateToHome
            } else {
                SplashUiState.NavigateToLogin
            }
        }
    }
}

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data object NavigateToHome : SplashUiState()
    data object NavigateToLogin : SplashUiState()
}
