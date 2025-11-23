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
import org.terratec.altopia.presentation.navigation.Route

class MainActivity : ComponentActivity(), KoinComponent {
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        var startDestination: Route = Route.Splash
        
        // Handle Deep Link
        intent?.data?.let { uri ->
            println("DeepLink: Received URI: $uri")
            println("DeepLink: Scheme: ${uri.scheme}, Host: ${uri.host}, Path: ${uri.path}")
            
            if (uri.scheme == "io.altopia.app" && uri.host == "auth" && uri.path == "/callback") {
                // Supabase returns tokens in the fragment part of the URL
                val fragment = uri.fragment
                println("DeepLink: Fragment: $fragment")
                
                if (fragment != null) {
                    val params = fragment.split("&").associate {
                        val parts = it.split("=")
                        if (parts.size == 2) parts[0] to parts[1] else "" to ""
                    }
                    
                    val accessToken = params["access_token"]
                    val refreshToken = params["refresh_token"]
                    val type = params["type"]
                    val expiresIn = params["expires_in"]?.toLongOrNull() ?: 3600
                    
                    println("DeepLink: Parsed - AccessToken: ${accessToken?.take(10)}..., Type: $type")
                    
                    if (accessToken != null && refreshToken != null && type == "recovery") {
                        val expiresAt = System.currentTimeMillis() / 1000 + expiresIn
                        
                        // Create a temporary session
                        val session = AuthSession(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            expiresAt = expiresAt,
                            user = User(
                                id = 0, // Temporary ID
                                name = "Reset User",
                                email = "" 
                            )
                        )
                        
                        runBlocking {
                            sessionManager.saveSession(session)
                        }
                        
                        startDestination = Route.ResetPassword
                        println("DeepLink: Session saved, setting startDestination to ResetPassword")
                    } else {
                        println("DeepLink: Missing tokens or wrong type")
                    }
                }
            } else {
                println("DeepLink: URI did not match expected scheme/host/path")
            }
        }

        setContent {
            App(startDestination = startDestination)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}