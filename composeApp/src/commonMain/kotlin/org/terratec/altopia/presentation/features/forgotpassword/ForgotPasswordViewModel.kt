package org.terratec.altopia.presentation.features.forgotpassword

import org.terratec.altopia.domain.usecase.ForgotPasswordUseCase
import org.terratec.altopia.presentation.model.DialogInfo
import org.terratec.altopia.presentation.util.executeTask
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

/**
 * ViewModel for Forgot Password screen following MVI pattern.
 */
class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : BaseViewModel<ForgotPasswordUiState, ForgotPasswordIntent, ForgotPasswordEvent>() {
    
    override fun createInitialState(): ForgotPasswordUiState = ForgotPasswordUiState()
    
    override fun handleIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailChanged -> handleEmailChanged(intent.email)
            ForgotPasswordIntent.SubmitEmail -> handleSubmitEmail()
            ForgotPasswordIntent.NavigateBack -> setEvent(ForgotPasswordEvent.NavigateToLogin)
        }
    }
    
    private fun handleEmailChanged(email: String) {
        setUiState {
            copy(
                email = email,
                isEmailValid = true,
                errorMessage = null
            )
        }
    }
    
    private fun handleSubmitEmail() {
        val currentState = uiState.value
        
        // Basic validation
        if (currentState.email.isBlank()) {
            setUiState { 
                copy(
                    isEmailValid = false,
                    errorMessage = "El correo no puede estar vacío"
                ) 
            }
            return
        }
        
        setUiState { copy(isLoading = true) }
        
        executeTask(
            onSuccess = {
                setUiState { copy(isLoading = false) }
                showDialog(
                    DialogInfo(
                        title = "Correo enviado",
                        description = "Te hemos enviado un email con instrucciones para recuperar tu contraseña. Por favor, revisa tu bandeja de entrada.",
                        primaryButtonText = "Entendido",
                        onPrimaryButtonClick = {
                            setEvent(ForgotPasswordEvent.NavigateToLogin)
                        }
                    )
                )
            },
            onFailure = { error ->
                setUiState {
                    copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        ) {
            forgotPasswordUseCase(currentState.email).getOrThrow()
        }
    }
}
