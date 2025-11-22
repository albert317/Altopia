package org.terratec.altopia

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.terratec.altopia.di.initKoin

class AltopiaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AltopiaApplication)
        }
    }
}
