package org.terratec.altopia

import android.app.Application
import org.terratec.altopia.data.local.datastore.initDataStore
import org.terratec.altopia.di.initKoin

class AltopiaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDataStore(this)
        initKoin()
    }
}
