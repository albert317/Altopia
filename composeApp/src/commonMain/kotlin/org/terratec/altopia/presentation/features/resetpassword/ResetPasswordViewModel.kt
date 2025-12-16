package org.terratec.altopia.presentation.features.resetpassword

import org.terratec.altopia.domain.usecase.UpdatePasswordUseCase
import org.terratec.altopia.presentation.model.DialogInfo
import org.terratec.altopia.presentation.util.executeTask
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

/**
 * ViewModel for Reset Password screen following MVI pattern.
 */
class ResetPasswordViewModel(
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val logoutUseCase: org.terratec.altopia.domain.usecase.auth.LogoutUseCase
) : BaseViewModel<ResetPasswordUiState, ResetPasswordIntent, ResetPasswordEvent>() {
    
    override fun createInitialState(): ResetPasswordUiState = ResetPasswordUiState()
    
    override fun handleIntent(intent: ResetPasswordIntent) {
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
    
    private fun handleSubmit() {
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
        
        executeTask(
            onSuccess = {
                // Clear the temporary session
                try {
                     // Ideally logoutUseCase should be async too or use executeTask properly if suspending
                     // But inside onSuccess we are on Main thread. 
                     // We should include logout in the task lambda or do it separately.
                     // IMPORTANT: The original code had logout AFTER updatePassword success.
                } catch(e: Exception) {
                     // ignore check
                }

                setUiState { copy(isLoading = false, isSuccess = true) }
                showDialog(
                    DialogInfo(
                        title = "Contraseña actualizada",
                        description = "Tu contraseña ha sido actualizada correctamente. Por favor, inicia sesión nuevamente.",
                        primaryButtonText = "Ir al Login",
                        onPrimaryButtonClick = {
                            setEvent(ResetPasswordEvent.NavigateToLogin)
                        }
                    )
                )
            },
            onFailure = { error ->
                setUiState {
                    copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al actualizar la contraseña"
                    )
                }
            }
        ) {
             updatePasswordUseCase(currentState.password).getOrThrow()
             // Since logout is also likely suspending or critical, we should do it here if possible.
             // But if LogoutUseCase needs to be run, we can chain it.
             logoutUseCase()
        }
    }
}
