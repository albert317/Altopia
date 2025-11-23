package org.terratec.altopia.di

import org.koin.core.context.startKoin

fun doInitKoin() {
    startKoin {
        modules(
            networkModule,
            appModule,
            authModule
        )
    }
}
