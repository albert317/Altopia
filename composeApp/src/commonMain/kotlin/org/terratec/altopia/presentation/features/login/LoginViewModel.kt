package org.terratec.altopia.presentation.features.login

import org.terratec.altopia.domain.usecase.auth.LoginUseCase
import org.terratec.altopia.domain.usecase.user.GetPersonUseCase
import org.terratec.altopia.domain.usecase.user.GetUserProfilesUseCase
import org.terratec.altopia.domain.usecase.user.GetUserUseCase
import org.terratec.altopia.presentation.model.DialogInfo
import org.terratec.altopia.presentation.util.executeTask
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

/**
 * ViewModel for Login screen following MVI pattern.
 * Handles user authentication with email and password.
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val getPersonUseCase: GetPersonUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getUserProfilesUseCase: GetUserProfilesUseCase
) : BaseViewModel<LoginUiState, LoginIntent, LoginEvent>() {

    override fun createInitialState(): LoginUiState = LoginUiState()

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> handleEmailChanged(intent.email)
            is LoginIntent.PasswordChanged -> handlePasswordChanged(intent.password)
            LoginIntent.SubmitCredentials -> handleSubmitCredentials()
            LoginIntent.ClearError -> handleClearError()
            LoginIntent.ForgotPasswordClicked -> handleForgotPassword()
            is LoginIntent.CheckPerson -> handleCheckPerson(intent.session)
            is LoginIntent.CheckUser -> handleCheckUser(intent.personId, intent.session)
            is LoginIntent.CheckProfiles -> handleCheckProfiles(
                intent.businessUserId,
                intent.session
            )
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

    private fun handleSubmitCredentials() {
        val currentState = uiState.value

        // Validación de credenciales
        if (!validateCredentials(currentState)) {
            return
        }

        performLogin(currentState.email, currentState.password)
    }

    private fun performLogin(email: String, password: String) {
        setUiState { copy(isLoading = true, errorMessage = null) }

        executeTask(
            onSuccess = { session ->
                setIntent(LoginIntent.CheckPerson(session))
            },
            onFailure = { error ->
                setUiState {
                    copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
                showLoginErrorDialog(error.message)
            }
        ) {
            loginUseCase(email, password).getOrThrow()
        }
    }

    private fun handleCheckPerson(session: org.terratec.altopia.domain.model.AuthSession) {
        if (session.user != null) {
            executeTask(
                onSuccess = { person ->
                    setIntent(LoginIntent.CheckUser(personId = person.id, session = session))
                },
                onFailure = {
                    handleLoginPostError("Persona no encontrada")
                }
            ) {
                getPersonUseCase(session.user.id).getOrThrow()
            }
        } else {
            handleLoginPostError("Sesión inválida")
        }
    }

    private fun handleCheckUser(
        personId: String,
        session: org.terratec.altopia.domain.model.AuthSession
    ) {
        executeTask(
            onSuccess = { businessUser ->
                setIntent(
                    LoginIntent.CheckProfiles(
                        businessUserId = businessUser.id,
                        session = session
                    )
                )
            },
            onFailure = {
                handleLoginPostError("Usuario de negocio no encontrado")
            }
        ) {
            getUserUseCase(personId).getOrThrow()
        }
    }

    private fun handleCheckProfiles(
        businessUserId: String,
        session: org.terratec.altopia.domain.model.AuthSession
    ) {
        executeTask(
            onSuccess = { profiles ->
                processProfiles(profiles, session)
                setUiState { copy(isLoading = false) }
            },
            onFailure = {
                handleLoginPostError("Error obteniendo perfiles")
            }
        ) {
            getUserProfilesUseCase(businessUserId).getOrThrow()
        }
    }

    private fun processProfiles(
        profiles: List<org.terratec.altopia.domain.model.UserProfile>,
        session: org.terratec.altopia.domain.model.AuthSession
    ) {
        // Lógica de diagrama
        val adminProfile =
            profiles.find { it.profileType == "ADMINISTRADOR" || it.profileType == "ADMIN" }

        if (adminProfile != null && profiles.size == 1) {
            // Solo perfil administrador -> DashboardScreen
            setEvent(LoginEvent.NavigateToAdminDashboard)
            return
        }

        if (profiles.size > 1) {
            // Más de un perfil -> ProfileSelectionScreen
            setEvent(LoginEvent.NavigateToProfileSelection)
            return
        }

        if (profiles.isNotEmpty()) {
            // Un perfil propietario (o inquilino/familiar) -> HomeScreen
            setEvent(LoginEvent.NavigateToHome(session))
        } else {
            handleLoginPostError("Usuario sin perfiles asignados")
        }
    }

    private fun handleLoginPostError(message: String) {
        setUiState {
            copy(isLoading = false, errorMessage = message)
        }
        showLoginErrorDialog(message)
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
