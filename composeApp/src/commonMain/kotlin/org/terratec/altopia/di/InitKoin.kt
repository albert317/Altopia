package org.terratec.altopia.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin() {
    startKoin {
        modules(
            networkModule,
            appModule,
            authModule
        )
    }
}
