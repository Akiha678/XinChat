package com.seanchen.xinchat.core.data.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.seanchen.xinchat.core.data.model.User
import com.seanchen.xinchat.core.data.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "xinchat_session")

@Singleton
internal class SessionStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = context.sessionDataStore

    val session: Flow<UserSession?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences.toSession() }
        .distinctUntilChanged()

    suspend fun current(): UserSession? = session.first()

    suspend fun save(session: UserSession) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_ID] = session.user.id
            preferences[Keys.USERNAME] = session.user.username
            preferences[Keys.DISPLAY_NAME] = session.user.displayName
            preferences[Keys.EMAIL] = session.user.email
            preferences[Keys.AVATAR_COLOR] = session.user.avatarColor
            preferences[Keys.ACCESS_TOKEN] = session.accessToken
            preferences[Keys.EXPIRES_AT] = session.expiresAt
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    private fun Preferences.toSession(): UserSession? {
        val token = this[Keys.ACCESS_TOKEN] ?: return null
        return UserSession(
            user = User(
                id = this[Keys.USER_ID] ?: return null,
                username = this[Keys.USERNAME] ?: return null,
                displayName = this[Keys.DISPLAY_NAME] ?: return null,
                email = this[Keys.EMAIL] ?: return null,
                avatarColor = this[Keys.AVATAR_COLOR] ?: 0,
            ),
            accessToken = token,
            expiresAt = this[Keys.EXPIRES_AT].orEmpty(),
        )
    }

    private object Keys {
        val USER_ID = longPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val EMAIL = stringPreferencesKey("email")
        val AVATAR_COLOR = intPreferencesKey("avatar_color")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val EXPIRES_AT = stringPreferencesKey("expires_at")
    }
}
