package org.terratec.altopia.presentation.features.resetpassword

import org.terratec.altopia.domain.usecase.UpdatePasswordUseCase
import org.terratec.altopia.presentation.model.DialogInfo
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

/**
 * ViewModel for Reset Password screen following MVI pattern.
 */
class ResetPasswordViewModel(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : BaseViewModel<ResetPasswordUiState, ResetPasswordIntent, ResetPasswordEvent>() {
    
    override fun createInitialState(): ResetPasswordUiState = ResetPasswordUiState()
    
    override suspend fun handleIntent(intent: ResetPasswordIntent) {
        when (intent) {
            is ResetPasswordIntent.PasswordChanged -> {
                setUiState { copy(password = intent.password, errorMessage = null) }
            }
            is ResetPasswordIntent.ConfirmPasswordChanged -> {
                setUiState { copy(confirmPassword = intent.password, errorMessage = null) }
            }
            ResetPasswordIntent.SubmitPassword -> handleSubmit()
            ResetPasswordIntent.NavigateToHome -> setEvent(ResetPasswordEvent.NavigateToHome)
        }
    }
    
    private suspend fun handleSubmit() {
        val currentState = uiState.value
        
        // Validation
        if (currentState.password.isBlank()) {
            setUiState { copy(errorMessage = "La contraseña es requerida") }
            return
        }
        
        if (currentState.password.length < 6) {
            setUiState { copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return
        }
        
        if (currentState.password != currentState.confirmPassword) {
            setUiState { copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }
        
        setUiState { copy(isLoading = true) }
        
        updatePasswordUseCase(currentState.password)
            .onSuccess {
                setUiState { copy(isLoading = false, isSuccess = true) }
                showDialog(
                    DialogInfo(
                        title = "Contraseña actualizada",
                        description = "Tu contraseña ha sido actualizada correctamente.",
                        primaryButtonText = "Ir al inicio",
                        onPrimaryButtonClick = {
                            setEvent(ResetPasswordEvent.NavigateToHome)
                        }
                    )
                )
            }
            .onFailure { error ->
                setUiState {
                    copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al actualizar la contraseña"
                    )
                }
            }
    }
}
