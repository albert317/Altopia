package org.terratec.altopia.presentation.features.login

import org.terratec.altopia.domain.usecase.auth.LoginUseCase
import org.terratec.altopia.presentation.model.DialogInfo
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

/**
 * ViewModel for Login screen following MVI pattern.
 * Handles user authentication with email and password.
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginUiState, LoginIntent, LoginEvent>() {

    override fun createInitialState(): LoginUiState = LoginUiState()

    override suspend fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> handleEmailChanged(intent.email)
            is LoginIntent.PasswordChanged -> handlePasswordChanged(intent.password)
            LoginIntent.SubmitCredentials -> handleSubmitCredentials()
            LoginIntent.ClearError -> handleClearError()
            LoginIntent.ForgotPasswordClicked -> handleForgotPassword()
        }
    }

    private fun handleEmailChanged(email: String) {
        setUiState {
            copy(
                email = email,
                isEmailValid = email.isBlank() || isValidEmail(email),
                errorMessage = null
            )
        }
    }

    private fun handlePasswordChanged(password: String) {
        setUiState {
            copy(
                password = password,
                isPasswordValid = password.isBlank() || password.length >= 6,
                errorMessage = null
            )
        }
    }

    private suspend fun handleSubmitCredentials() {
        val currentState = uiState.value
        
        // Validación de credenciales
        if (!validateCredentials(currentState)) {
            return
        }

        // Mostrar estado de carga
        setUiState { copy(isLoading = true, errorMessage = null) }

        // Ejecutar use case de login
        loginUseCase(currentState.email, currentState.password)
            .onSuccess { user ->
                setUiState { copy(isLoading = false) }
                setEvent(LoginEvent.NavigateToHome(user))
            }
            .onFailure { error ->
                setUiState {
                    copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error desconocido"
                    )
                }
                showLoginErrorDialog(error.message ?: "Error desconocido")
            }
    }

    private fun validateCredentials(state: LoginUiState): Boolean {
        val emailValid = isValidEmail(state.email)
        val passwordValid = state.password.length >= 6

        if (!emailValid || !passwordValid) {
            setUiState {
                copy(
                    isEmailValid = emailValid,
                    isPasswordValid = passwordValid,
                    errorMessage = "Por favor, verifica tus credenciales"
                )
            }
            return false
        }

        return true
    }

    private fun handleClearError() {
        setUiState { copy(errorMessage = null) }
    }

    private fun handleForgotPassword() {
        setEvent(LoginEvent.NavigateToForgotPassword)
    }

    private fun isValidEmail(email: String): Boolean {
        // Regex simple para validación de email
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    private fun showLoginErrorDialog(message: String) {
        showDialog(
            DialogInfo(
                title = "Error de Inicio de Sesión",
                description = message,
                primaryButtonText = "Entendido",
                onPrimaryButtonClick = {
                    // El diálogo se cierra automáticamente
                }
            )
        )
    }
}
