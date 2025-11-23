package org.terratec.altopia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.usecase.auth.GetCurrentUserUseCase
import org.terratec.altopia.domain.usecase.auth.LogoutUseCase

/**
 * ViewModel for Home screen.
 * Displays current user information and handles logout.
 */
class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase(refresh = false)
                .onSuccess { user ->
                    _uiState.value = if (user != null) {
                        HomeUiState.Success(user)
                    } else {
                        HomeUiState.Error("No se pudo obtener el usuario")
                    }
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(
                        error.message ?: "Error al cargar usuario"
                    )
                }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
                .onSuccess {
                    _uiState.value = HomeUiState.LoggedOut
                }
                .onFailure { error ->
                    // Even if logout fails on server, we cleared local session
                    _uiState.value = HomeUiState.LoggedOut
                }
        }
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val user: User) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    data object LoggedOut : HomeUiState()
}
