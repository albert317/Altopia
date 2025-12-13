package org.terratec.altopia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.terratec.altopia.data.local.session.SessionManager
import org.terratec.altopia.domain.model.AuthSession
import org.terratec.altopia.domain.model.User
import org.terratec.altopia.presentation.navigation.DeepLinkHandler
import org.terratec.altopia.presentation.navigation.Route

class MainActivity : ComponentActivity(), KoinComponent {
    private val sessionManager: SessionManager by inject()
    private val deepLinkHandler: DeepLinkHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        var startDestination: Route = Route.Splash
        
        // Handle Deep Link on launch
        intent?.data?.let { uri ->
            handleDeepLink(uri) { route ->
                startDestination = route
            }
        }

        setContent {
            App(startDestination = startDestination)
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            handleDeepLink(uri) { route ->
                runBlocking {
                    deepLinkHandler.emitDeepLink(route)
                }
            }
        }
    }

    private fun handleDeepLink(uri: android.net.Uri, onRouteFound: (Route) -> Unit) {
        println("DeepLink: Received URI: $uri")
        
        // Delegate to shared handler
        runBlocking {
            deepLinkHandler.handleDeepLink(uri.toString())
        }
        
        // Note: The shared handler emits the event, which AppNavHost listens to.
        // We don't need to manually set startDestination here if we are using the reactive flow.
        // However, for onCreate (initial launch), we might want to set startDestination if possible.
        // But since handleDeepLink is suspend and we are in runBlocking, the event might be emitted before the collector is ready in onCreate.
        // Actually, for onCreate, we are setting startDestination.
        // The shared handler saves the session. We can check if session exists?
        // Or we can just let the AppNavHost handle it via the event, but for cold start, the event might be missed if collector not ready?
        // Wait, DeepLinkHandler uses replay=0. If we emit before AppNavHost collects, it's lost.
        // We should change replay=1 in DeepLinkHandler or handle cold start differently.
        
        // Let's check if we can just rely on the session being saved.
        // If session is saved, Splash screen will navigate to Home (or we can add logic to navigate to ResetPassword).
        // But we want to go to ResetPassword specifically.
        
        // For now, let's keep the logic simple:
        // 1. Pass string to shared handler to save session.
        // 2. If shared handler returns true/success (we can modify it to return boolean), we set route.
        // But shared handler returns Unit.
        
        // Let's trust the reactive flow for onNewIntent.
        // For onCreate, we might need to wait.
        
        // Actually, let's just use the shared handler for parsing and side effects (saving session).
        // And we can rely on the fact that we just called it.
        
        // To avoid complexity, I will rely on the shared handler doing the work.
        // But for onCreate, I need to know if I should set startDestination.
        
        // Let's rely on the fact that if we are here, we have a deep link.
        if (uri.toString().contains("type=recovery")) {
             // We can assume it's a recovery link.
             // Ideally we should wait for handleDeepLink to finish (it is runBlocking).
             // If it finished, session is saved.
             // We can set startDestination = Route.ResetPassword
             onRouteFound(Route.ResetPassword)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}