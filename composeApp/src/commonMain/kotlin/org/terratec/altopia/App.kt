package org.terratec.altopia

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.terratec.altopia.presentation.navigation.AppNavHost
import org.terratec.altopia.presentation.navigation.Route
import org.terratec.altopia.presentation.ui.theme.AppTheme

@Composable
@Preview
fun App(startDestination: Route = Route.Splash) {
    AppTheme {
        AppNavHost(startDestination = startDestination)
    }
}