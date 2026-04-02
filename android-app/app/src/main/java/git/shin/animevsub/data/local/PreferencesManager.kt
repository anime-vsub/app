package git.shin.animevsub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import git.shin.animevsub.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
  companion object {
    private val USER_KEY = stringPreferencesKey("user_data")
    private val COOKIE_KEY = stringPreferencesKey("user_cookie")
    private val AUTO_NEXT_KEY = booleanPreferencesKey("auto_next")
    private val AUTO_SKIP_KEY = booleanPreferencesKey("auto_skip")
    private val SERVER_KEY = stringPreferencesKey("server")
    private val MOVIE_MODE_KEY = booleanPreferencesKey("movie_mode")
    private val SHOW_COMMENTS_KEY = booleanPreferencesKey("show_comments")
    private val INFINITE_SCROLL_KEY = booleanPreferencesKey("infinite_scroll")
    private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
    private val VOLUME_GESTURE_KEY = booleanPreferencesKey("volume_gesture")
    private val BRIGHTNESS_GESTURE_KEY = booleanPreferencesKey("brightness_gesture")
  }

  val userData: Flow<User?> = context.dataStore.data.map { preferences ->
    val json = preferences[USER_KEY] ?: return@map null
    try {
      Json.decodeFromString<User>(json)
    } catch (e: Exception) {
      null
    }
  }

  val userCookie: Flow<String?> = context.dataStore.data.map { preferences ->
    preferences[COOKIE_KEY]
  }

  val autoNext: Flow<Boolean> = context.dataStore.data.map { it[AUTO_NEXT_KEY] ?: true }
  val autoSkip: Flow<Boolean> = context.dataStore.data.map { it[AUTO_SKIP_KEY] ?: false }
  val server: Flow<String> = context.dataStore.data.map { it[SERVER_KEY] ?: "DU" }
  val movieMode: Flow<Boolean> = context.dataStore.data.map { it[MOVIE_MODE_KEY] ?: false }
  val showComments: Flow<Boolean> = context.dataStore.data.map { it[SHOW_COMMENTS_KEY] ?: true }
  val infiniteScroll: Flow<Boolean> = context.dataStore.data.map { it[INFINITE_SCROLL_KEY] ?: true }
  val volumeGesture: Flow<Boolean> = context.dataStore.data.map { it[VOLUME_GESTURE_KEY] ?: true }
  val brightnessGesture: Flow<Boolean> =
    context.dataStore.data.map { it[BRIGHTNESS_GESTURE_KEY] ?: true }

  val searchHistory: Flow<List<String>> = context.dataStore.data.map { preferences ->
    val json = preferences[SEARCH_HISTORY_KEY] ?: return@map emptyList()
    try {
      Json.decodeFromString<List<String>>(json)
    } catch (e: Exception) {
      emptyList()
    }
  }

  suspend fun saveUser(user: User, cookie: String) {
    context.dataStore.edit { preferences ->
      preferences[USER_KEY] = Json.encodeToString(user)
      preferences[COOKIE_KEY] = cookie
    }
  }

  suspend fun clearUser() {
    context.dataStore.edit { preferences ->
      preferences.remove(USER_KEY)
      preferences.remove(COOKIE_KEY)
    }
  }

  suspend fun setAutoNext(value: Boolean) {
    context.dataStore.edit { it[AUTO_NEXT_KEY] = value }
  }

  suspend fun setAutoSkip(value: Boolean) {
    context.dataStore.edit { it[AUTO_SKIP_KEY] = value }
  }

  suspend fun setServer(value: String) {
    context.dataStore.edit { it[SERVER_KEY] = value }
  }

  suspend fun setMovieMode(value: Boolean) {
    context.dataStore.edit { it[MOVIE_MODE_KEY] = value }
  }

  suspend fun setShowComments(value: Boolean) {
    context.dataStore.edit { it[SHOW_COMMENTS_KEY] = value }
  }

  suspend fun setInfiniteScroll(value: Boolean) {
    context.dataStore.edit { it[INFINITE_SCROLL_KEY] = value }
  }

  suspend fun setVolumeGesture(value: Boolean) {
    context.dataStore.edit { it[VOLUME_GESTURE_KEY] = value }
  }

  suspend fun setBrightnessGesture(value: Boolean) {
    context.dataStore.edit { it[BRIGHTNESS_GESTURE_KEY] = value }
  }

  suspend fun addSearchHistory(query: String) {
    context.dataStore.edit { preferences ->
      val currentHistory = searchHistory.first().toMutableList()
      currentHistory.remove(query)
      currentHistory.add(query)
      if (currentHistory.size > 10) {
        currentHistory.removeAt(0)
      }
      preferences[SEARCH_HISTORY_KEY] = Json.encodeToString(currentHistory)
    }
  }

  suspend fun clearSearchHistory() {
    context.dataStore.edit { preferences ->
      preferences.remove(SEARCH_HISTORY_KEY)
    }
  }
}
