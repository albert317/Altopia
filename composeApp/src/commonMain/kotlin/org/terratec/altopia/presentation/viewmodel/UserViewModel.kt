package org.terratec.altopia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.model.Video
import org.terratec.altopia.domain.usecase.GetUserUseCase
import org.terratec.altopia.domain.usecase.GetVideosUseCase

class UserViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getVideosUseCase: GetVideosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    fun loadUser(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getUserUseCase(userId)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(isLoading = false, user = user)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
        }
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getVideosUseCase()
                .onSuccess { videos ->
                    _uiState.value = _uiState.value.copy(isLoading = false, videos = videos)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
        }
    }
}

data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val videos: List<Video> = emptyList(),
    val error: String? = null
)
