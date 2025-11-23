package org.terratec.altopia.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Factory for creating DataStore instances.
 * Platform-specific implementations provide the actual DataStore creation.
 */
expect fun createDataStore(): DataStore<Preferences>
