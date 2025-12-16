package org.terratec.altopia.presentation.features.splash

import kotlinx.coroutines.delay
import org.terratec.altopia.domain.model.AuthSession
import org.terratec.altopia.domain.model.UserProfile
import org.terratec.altopia.domain.usecase.auth.GetAuthSessionLocalUseCase
import org.terratec.altopia.domain.usecase.user.GetPersonUseCase
import org.terratec.altopia.domain.usecase.user.GetUserProfilesUseCase
import org.terratec.altopia.domain.usecase.user.GetUserUseCase
import org.terratec.altopia.presentation.util.executeTask
import org.terratec.altopia.presentation.viewmodel.BaseViewModel

/**
 * ViewModel for Splash screen.
 * Validates if there's an active session and navigates accordingly.
 */
class SplashViewModel(
    private val getAuthSessionLocalUseCase: GetAuthSessionLocalUseCase,
    private val getPersonUseCase: GetPersonUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getUserProfilesUseCase: GetUserProfilesUseCase
) : BaseViewModel<SplashUiState, SplashIntent, SplashEvent>() {

    override fun createInitialState(): SplashUiState = SplashUiState()

    init {
        setIntent(SplashIntent.CheckSession)
    }

    override fun handleIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.CheckSession -> handleCheckSession()
            is SplashIntent.CheckPerson -> handleCheckPerson(intent.session)
            is SplashIntent.CheckUser -> handleCheckUser(intent.personId, intent.session)
            is SplashIntent.CheckProfiles -> handleCheckProfiles(intent.businessUserId, intent.session)
        }
    }

    private fun handleCheckSession() {
        executeTask(
            onSuccess = { session ->
                if (session != null) {
                    setIntent(SplashIntent.CheckPerson(session))
                } else {
                    setEvent(SplashEvent.NavigateToLogin)
                }
            },
            onFailure = {
                setEvent(SplashEvent.NavigateToLogin)
            }
        ) {
            // Delay of 1 second time to show splash logo/animation
            delay(1000)
            getAuthSessionLocalUseCase().getOrThrow()
        }
    }

    private fun handleCheckPerson(session: AuthSession) {
        val userId = session.user.id
        
        executeTask(
            onSuccess = { person ->
                setIntent(SplashIntent.CheckUser(personId = person.id, session = session))
            },
            onFailure = {
                setEvent(SplashEvent.NavigateToLogin)
            }
        ) {
             getPersonUseCase(userId).getOrThrow()
        }
    }

    private fun handleCheckUser(personId: String, session: AuthSession) {
        executeTask(
            onSuccess = { businessUser ->
                setIntent(SplashIntent.CheckProfiles(businessUserId = businessUser.id, session = session))
            },
            onFailure = {
                setEvent(SplashEvent.NavigateToLogin)
            }
        ) {
            getUserUseCase(personId).getOrThrow()
        }
    }

    private fun handleCheckProfiles(businessUserId: String, session: AuthSession) {
        executeTask(
            onSuccess = { profiles ->
                processProfiles(profiles, session)
                setUiState { copy(isLoading = false) }
            },
            onFailure = {
                setEvent(SplashEvent.NavigateToLogin)
                setUiState { copy(isLoading = false) }
            }
        ) {
            getUserProfilesUseCase(businessUserId).getOrThrow()
        }
    }

    private fun processProfiles(profiles: List<UserProfile>, session: AuthSession) {
        // Lógica de diagrama
        val adminProfile = profiles.find { it.profileType == "ADMINISTRADOR" || it.profileType == "ADMIN" }
        
        if (adminProfile != null && profiles.size == 1) {
             // Solo perfil administrador -> DashboardScreen
             setEvent(SplashEvent.NavigateToAdminDashboard)
             return
        }

        if (profiles.size > 1) {
            // Más de un perfil -> ProfileSelectionScreen
            setEvent(SplashEvent.NavigateToProfileSelection)
            return
        }

        if (profiles.isNotEmpty()) {
             // Un perfil propietario (o inquilino/familiar) -> HomeScreen
             setEvent(SplashEvent.NavigateToHome(session))
        } else {
            // Sin perfiles -> Login (o manejar error)
            setEvent(SplashEvent.NavigateToLogin)
        }
    }
}
