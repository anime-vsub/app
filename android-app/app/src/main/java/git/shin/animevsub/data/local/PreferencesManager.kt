package git.shin.animevsub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
  companion object {
    private val AUTO_NEXT_KEY = booleanPreferencesKey("auto_next")
    private val AUTO_SKIP_KEY = booleanPreferencesKey("auto_skip")
    private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
    private val VOLUME_GESTURE_KEY = booleanPreferencesKey("volume_gesture")
    private val BRIGHTNESS_GESTURE_KEY = booleanPreferencesKey("brightness_gesture")
  }

  val autoNext: Flow<Boolean> = context.dataStore.data.map { it[AUTO_NEXT_KEY] ?: true }
  val autoSkip: Flow<Boolean> = context.dataStore.data.map { it[AUTO_SKIP_KEY] ?: false }
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

  suspend fun setAutoNext(value: Boolean) {
    context.dataStore.edit { it[AUTO_NEXT_KEY] = value }
  }

  suspend fun setAutoSkip(value: Boolean) {
    context.dataStore.edit { it[AUTO_SKIP_KEY] = value }
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
