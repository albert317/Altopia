package org.terratec.altopia.presentation.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Handler for managing deep link navigation events.
 * Allows the MainActivity to communicate deep link intents to the UI layer.
 */
class DeepLinkHandler(
    private val sessionManager: org.terratec.altopia.data.local.session.SessionManager
) : ViewModel() {
    private val _deepLinkEvent = MutableSharedFlow<Route>(replay = 1)
    val deepLinkEvent: SharedFlow<Route> = _deepLinkEvent.asSharedFlow()

    suspend fun emitDeepLink(route: Route) {
        _deepLinkEvent.emit(route)
    }

    suspend fun handleDeepLink(urlString: String) {
        println("DeepLinkHandler: Processing URL: $urlString")
        
        // Basic parsing for io.altopia.app://auth/callback#access_token=...
        // We need to handle both "io.altopia.app://auth/callback" and potentially other formats if they vary by platform
        // But for now assuming the structure matches what we saw in Android
        
        try {
            // Check scheme and path
            // Note: KMP doesn't have a standard URI parser in stdlib, so we do string manipulation
            if (urlString.startsWith("io.altopia.app://auth/callback")) {
                val fragmentPart = urlString.substringAfter("#", "")
                if (fragmentPart.isNotEmpty()) {
                    val params = fragmentPart.split("&").associate {
                        val parts = it.split("=")
                        if (parts.size == 2) parts[0] to parts[1] else "" to ""
                    }
                    
                    val accessToken = params["access_token"]
                    val refreshToken = params["refresh_token"]
                    val type = params["type"]
                    val expiresIn = params["expires_in"]?.toLongOrNull() ?: 3600
                    
                    if (accessToken != null && refreshToken != null && type == "recovery") {
                        val expiresAt = org.terratec.altopia.data.local.util.getCurrentTimeMillis() / 1000 + expiresIn
                        
                        val session = org.terratec.altopia.domain.model.AuthSession(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            tokenType = "bearer",
                            expiresIn = expiresIn.toInt(),
                            expiresAt = expiresAt,
                            user = org.terratec.altopia.domain.model.User(
                                id = "0",
                                aud = "authenticated",
                                role = "authenticated",
                                email = "",
                                createdAt = "",
                                updatedAt = "",
                                userMetadata = org.terratec.altopia.domain.model.UserMetadata(name = "Reset User")
                            )
                        )
                        
                        sessionManager.saveSession(session)
                        println("DeepLinkHandler: Session saved, emitting ResetPassword route")
                        emitDeepLink(Route.ResetPassword)
                    }
                }
            }
        } catch (e: Exception) {
            println("DeepLinkHandler: Error parsing URL: ${e.message}")
        }
    }
}
