package org.terratec.altopia

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.terratec.altopia.presentation.navigation.AppNavHost

@Composable
@Preview
fun App() {
    KoinContext {
        MaterialTheme {
            AppNavHost()
        }
    }
}