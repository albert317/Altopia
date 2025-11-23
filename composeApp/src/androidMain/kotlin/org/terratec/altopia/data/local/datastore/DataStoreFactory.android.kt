package org.terratec.altopia.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Android implementation of DataStore factory.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "altopia_datastore")

private lateinit var applicationContext: Context

/**
 * Initialize the DataStore factory with application context.
 * This should be called from the Application class.
 */
fun initDataStore(context: Context) {
    applicationContext = context.applicationContext
}

actual fun createDataStore(): DataStore<Preferences> {
    return applicationContext.dataStore
}
