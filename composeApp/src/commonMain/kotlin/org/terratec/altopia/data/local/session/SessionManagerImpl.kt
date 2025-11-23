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
import org.terratec.altopia.domain.model.User

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
            preferences[ACCESS_TOKEN_KEY] = session.accessToken
            preferences[REFRESH_TOKEN_KEY] = session.refreshToken
            preferences[EXPIRES_AT_KEY] = session.expiresAt
            preferences[USER_JSON_KEY] = json.encodeToString(session.user)
        }
    }
    
    override suspend fun getSession(): AuthSession? {
        val preferences = dataStore.data.first()
        val accessToken = preferences[ACCESS_TOKEN_KEY] ?: return null
        val refreshToken = preferences[REFRESH_TOKEN_KEY] ?: return null
        val expiresAt = preferences[EXPIRES_AT_KEY] ?: return null
        val userJson = preferences[USER_JSON_KEY] ?: return null
        
        return try {
            AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = expiresAt,
                user = json.decodeFromString<User>(userJson)
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    override suspend fun updateAccessToken(accessToken: String, expiresAt: Long) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[EXPIRES_AT_KEY] = expiresAt
        }
    }
    
    override fun isSessionValid(): Boolean {
        return runBlocking {
            dataStore.data.map { preferences ->
                val expiresAt = preferences[EXPIRES_AT_KEY] ?: return@map false
                val currentTime = getCurrentTimeMillis() / 1000
                currentTime < expiresAt
            }.first()
        }
    }
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
        private val USER_JSON_KEY = stringPreferencesKey("user_json")
    }
}
