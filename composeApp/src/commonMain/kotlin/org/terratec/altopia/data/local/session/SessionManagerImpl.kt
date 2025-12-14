package org.terratec.altopia.data.local.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.terratec.altopia.data.local.util.getCurrentTimeMillis
import org.terratec.altopia.domain.model.AuthSession

/**
 * Implementation of SessionManager using DataStore for persistent storage.
 */
class SessionManagerImpl(
    private val dataStore: DataStore<Preferences>
) : SessionManager {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    override suspend fun saveSession(session: AuthSession) {
        dataStore.edit { preferences ->
            preferences[SESSION_DATA_KEY] = json.encodeToString(session)
        }
    }
    
    override suspend fun getSession(): AuthSession? {
        val preferences = dataStore.data.first()
        val sessionJson = preferences[SESSION_DATA_KEY] ?: return null
        
        return try {
            json.decodeFromString<AuthSession>(sessionJson)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(SESSION_DATA_KEY)
        }
    }
    
    // With JSON storage, we need to read the entire object, modify it, and write it back.
    override suspend fun updateAccessToken(accessToken: String, expiresAt: Long) {
        val currentSession = getSession() ?: return
        val updatedSession = currentSession.copy(
            accessToken = accessToken,
            expiresAt = expiresAt
        )
        saveSession(updatedSession)
    }
    
    override fun isSessionValid(): Boolean {
        return runBlocking {
            val session = getSession() ?: return@runBlocking false
            val currentTime = getCurrentTimeMillis() / 1000
            currentTime < session.expiresAt
        }
    }
    
    companion object {
        private val SESSION_DATA_KEY = stringPreferencesKey("session_data")
    }
}
