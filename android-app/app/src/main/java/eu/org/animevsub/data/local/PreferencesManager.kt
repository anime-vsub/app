package eu.org.animevsub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.org.animevsub.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "animevsub_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Auth keys
    private val KEY_TOKEN_NAME = stringPreferencesKey("token_name")
    private val KEY_TOKEN_VALUE = stringPreferencesKey("token_value")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    private val KEY_USER_NAME = stringPreferencesKey("user_name")
    private val KEY_USER_SEX = stringPreferencesKey("user_sex")
    private val KEY_USER_USERNAME = stringPreferencesKey("user_username")
    private val KEY_USER_AVATAR = stringPreferencesKey("user_avatar")

    // Settings keys
    private val KEY_AUTO_NEXT = booleanPreferencesKey("auto_next")
    private val KEY_AUTO_SKIP = booleanPreferencesKey("auto_skip")
    private val KEY_VOLUME = floatPreferencesKey("volume")
    private val KEY_SERVER = stringPreferencesKey("server")
    private val KEY_MOVIE_MODE = booleanPreferencesKey("movie_mode")
    private val KEY_SHOW_COMMENTS = booleanPreferencesKey("show_comments")
    private val KEY_INFINITE_SCROLL = booleanPreferencesKey("infinite_scroll")

    // Search history
    private val KEY_SEARCH_HISTORY = stringSetPreferencesKey("search_history")

    // Auth
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_TOKEN_NAME] != null && prefs[KEY_TOKEN_VALUE] != null && prefs[KEY_USER_EMAIL] != null
    }

    val user: Flow<User?> = context.dataStore.data.map { prefs ->
        val email = prefs[KEY_USER_EMAIL] ?: return@map null
        val name = prefs[KEY_USER_NAME] ?: return@map null
        User(
            avatar = prefs[KEY_USER_AVATAR],
            email = email,
            name = name,
            sex = prefs[KEY_USER_SEX] ?: "",
            username = prefs[KEY_USER_USERNAME] ?: ""
        )
    }

    val cookie: Flow<String?> = context.dataStore.data.map { prefs ->
        val tokenName = prefs[KEY_TOKEN_NAME] ?: return@map null
        val tokenValue = prefs[KEY_TOKEN_VALUE] ?: return@map null
        "$tokenName=$tokenValue"
    }

    suspend fun saveAuth(user: User, cookie: String) {
        val parts = cookie.split(";")[0].split("=", limit = 2)
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN_NAME] = parts[0]
            prefs[KEY_TOKEN_VALUE] = parts.getOrElse(1) { "" }
            prefs[KEY_USER_EMAIL] = user.email
            prefs[KEY_USER_NAME] = user.name
            prefs[KEY_USER_SEX] = user.sex
            prefs[KEY_USER_USERNAME] = user.username
            prefs[KEY_USER_AVATAR] = user.avatar ?: ""
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN_NAME)
            prefs.remove(KEY_TOKEN_VALUE)
            prefs.remove(KEY_USER_EMAIL)
            prefs.remove(KEY_USER_NAME)
            prefs.remove(KEY_USER_SEX)
            prefs.remove(KEY_USER_USERNAME)
            prefs.remove(KEY_USER_AVATAR)
        }
    }

    // Settings
    val autoNext: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_NEXT] ?: true }
    val autoSkip: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_SKIP] ?: false }
    val volume: Flow<Float> = context.dataStore.data.map { it[KEY_VOLUME] ?: 1f }
    val server: Flow<String> = context.dataStore.data.map { it[KEY_SERVER] ?: "DU" }
    val movieMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_MOVIE_MODE] ?: false }
    val showComments: Flow<Boolean> = context.dataStore.data.map { it[KEY_SHOW_COMMENTS] ?: true }
    val infiniteScroll: Flow<Boolean> = context.dataStore.data.map { it[KEY_INFINITE_SCROLL] ?: true }

    suspend fun setAutoNext(value: Boolean) = context.dataStore.edit { it[KEY_AUTO_NEXT] = value }
    suspend fun setAutoSkip(value: Boolean) = context.dataStore.edit { it[KEY_AUTO_SKIP] = value }
    suspend fun setVolume(value: Float) = context.dataStore.edit { it[KEY_VOLUME] = value }
    suspend fun setServer(value: String) = context.dataStore.edit { it[KEY_SERVER] = value }
    suspend fun setMovieMode(value: Boolean) = context.dataStore.edit { it[KEY_MOVIE_MODE] = value }
    suspend fun setShowComments(value: Boolean) = context.dataStore.edit { it[KEY_SHOW_COMMENTS] = value }
    suspend fun setInfiniteScroll(value: Boolean) = context.dataStore.edit { it[KEY_INFINITE_SCROLL] = value }

    // Search history
    val searchHistory: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_SEARCH_HISTORY]?.toList() ?: emptyList()
    }

    suspend fun addSearchHistory(keyword: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_SEARCH_HISTORY]?.toMutableSet() ?: mutableSetOf()
            current.add(keyword)
            // Keep only last 20
            val trimmed = current.toList().takeLast(20).toSet()
            prefs[KEY_SEARCH_HISTORY] = trimmed
        }
    }

    suspend fun clearSearchHistory() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_SEARCH_HISTORY)
        }
    }
}
