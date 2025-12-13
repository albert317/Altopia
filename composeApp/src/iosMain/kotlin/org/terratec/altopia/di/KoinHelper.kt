package org.terratec.altopia.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.terratec.altopia.presentation.navigation.DeepLinkHandler

/**
 * Helper class to access Koin dependencies from iOS (Swift).
 */
class KoinHelper : KoinComponent {
    val deepLinkHandler: DeepLinkHandler by inject()
}
