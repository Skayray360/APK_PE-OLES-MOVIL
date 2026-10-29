package com.example.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "penoles_conecta_prefs")

data class LocalUserPreferences(
    val darkThemeEnabled: Boolean = false,
    val autoAdvanceCarousel: Boolean = true,
    val pushNotificationsConsent: Boolean = false,
    val subscribedNotificationTopics: Set<String> = setOf("Noticias importantes", "Sostenibilidad", "Eventos"),
    val activeSessionUsername: String = "",
    val activeSessionRole: String = "",
    val activeSessionEmail: String = ""
)

class UserPreferencesRepository(private val context: Context) {
    private val appContext = context.applicationContext

    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme_enabled")
        val AUTO_CAROUSEL = booleanPreferencesKey("auto_advance_carousel")
        val PUSH_CONSENT = booleanPreferencesKey("push_notifications_consent")
        val TOPICS = stringSetPreferencesKey("subscribed_notification_topics")
        val SESSION_USERNAME = stringPreferencesKey("active_session_username")
        val SESSION_ROLE = stringPreferencesKey("active_session_role")
        val SESSION_EMAIL = stringPreferencesKey("active_session_email")
    }

    val preferencesFlow: Flow<LocalUserPreferences> = appContext.dataStore.data.map { prefs ->
        LocalUserPreferences(
            darkThemeEnabled = prefs[Keys.DARK_THEME] ?: false,
            autoAdvanceCarousel = prefs[Keys.AUTO_CAROUSEL] ?: true,
            pushNotificationsConsent = prefs[Keys.PUSH_CONSENT] ?: false,
            subscribedNotificationTopics = prefs[Keys.TOPICS]
                ?: setOf("Noticias importantes", "Sostenibilidad", "Eventos"),
            activeSessionUsername = prefs[Keys.SESSION_USERNAME] ?: "",
            activeSessionRole = prefs[Keys.SESSION_ROLE] ?: "",
            activeSessionEmail = prefs[Keys.SESSION_EMAIL] ?: ""
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        appContext.dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setAutoAdvanceCarousel(enabled: Boolean) {
        appContext.dataStore.edit { it[Keys.AUTO_CAROUSEL] = enabled }
    }

    suspend fun setPushNotificationsConsent(enabled: Boolean) {
        appContext.dataStore.edit { it[Keys.PUSH_CONSENT] = enabled }
    }

    suspend fun toggleNotificationTopic(topic: String) {
        appContext.dataStore.edit { prefs ->
            val current = prefs[Keys.TOPICS]?.toMutableSet()
                ?: mutableSetOf("Noticias importantes", "Sostenibilidad", "Eventos")
            if (current.contains(topic)) {
                current.remove(topic)
            } else {
                current.add(topic)
            }
            prefs[Keys.TOPICS] = current
        }
    }

    suspend fun saveLocalSession(username: String, role: String, email: String) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.SESSION_USERNAME] = username
            prefs[Keys.SESSION_ROLE] = role
            prefs[Keys.SESSION_EMAIL] = email
        }
    }

    suspend fun clearLocalSession() {
        appContext.dataStore.edit { prefs ->
            prefs.remove(Keys.SESSION_USERNAME)
            prefs.remove(Keys.SESSION_ROLE)
            prefs.remove(Keys.SESSION_EMAIL)
        }
    }
}
