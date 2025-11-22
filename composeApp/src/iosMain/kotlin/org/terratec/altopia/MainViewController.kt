package org.terratec.altopia

import androidx.compose.ui.window.ComposeUIViewController
import org.terratec.altopia.di.initKoin

private var koinInitialized = false

fun MainViewController() = ComposeUIViewController { 
    if (!koinInitialized) {
        initKoin()
        koinInitialized = true
    }
    App() 
}