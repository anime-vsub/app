package git.shin.animevsub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
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
    private val VOLUME_GESTURE_KEY = booleanPreferencesKey("volume_gesture")
    private val BRIGHTNESS_GESTURE_KEY = booleanPreferencesKey("brightness_gesture")
    private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
    private val AUTO_SYNC_NOTIFY_KEY = booleanPreferencesKey("auto_sync_notify")
    private val NOTIFY_INTERVAL_KEY = androidx.datastore.preferences.core.intPreferencesKey("notify_interval")
    private val DB_NOTIFY_INTERVAL_KEY = androidx.datastore.preferences.core.intPreferencesKey("db_notify_interval")
    private val ENABLE_BACKGROUND_SYNC_KEY = booleanPreferencesKey("enable_background_sync")
    private val ENABLE_NOTIFICATIONS_KEY = booleanPreferencesKey("enable_notifications")
    private val LAST_ACTIVE_CHECK_KEY = longPreferencesKey("last_active_check")
    private val DOUBLE_TAP_SKIP_KEY = androidx.datastore.preferences.core.intPreferencesKey("double_tap_skip")
    private val LONG_PRESS_SPEED_KEY = androidx.datastore.preferences.core.floatPreferencesKey("long_press_speed")
    private val BREAK_REMINDER_ENABLED_KEY = booleanPreferencesKey("break_reminder_enabled")
    private val BREAK_REMINDER_INTERVAL_KEY = androidx.datastore.preferences.core.intPreferencesKey("break_reminder_interval")
    private val BEDTIME_REMINDER_ENABLED_KEY = booleanPreferencesKey("bedtime_reminder_enabled")
    private val BEDTIME_REMINDER_START_TIME_KEY = longPreferencesKey("bedtime_reminder_start_time")
    private val BEDTIME_REMINDER_END_TIME_KEY = longPreferencesKey("bedtime_reminder_end_time")
    private val BEDTIME_REMINDER_WAIT_FINISH_KEY = booleanPreferencesKey("bedtime_reminder_wait_finish")
    private val APP_LANGUAGE_KEY = stringPreferencesKey("app_language")
  }

  val autoNext: Flow<Boolean> = context.dataStore.data.map { it[AUTO_NEXT_KEY] ?: true }
  val autoSkip: Flow<Boolean> = context.dataStore.data.map { it[AUTO_SKIP_KEY] ?: false }
  val volumeGesture: Flow<Boolean> = context.dataStore.data.map { it[VOLUME_GESTURE_KEY] ?: true }
  val brightnessGesture: Flow<Boolean> =
    context.dataStore.data.map { it[BRIGHTNESS_GESTURE_KEY] ?: true }
  val doubleTapSkip: Flow<Int> = context.dataStore.data.map { it[DOUBLE_TAP_SKIP_KEY] ?: 10 }
  val longPressSpeed: Flow<Float> = context.dataStore.data.map { it[LONG_PRESS_SPEED_KEY] ?: 2.0f }
  val autoSyncNotify: Flow<Boolean> =
    context.dataStore.data.map { it[AUTO_SYNC_NOTIFY_KEY] ?: false }

  val notifyInterval: Flow<Int> = context.dataStore.data.map { it[NOTIFY_INTERVAL_KEY] ?: 15 }
  val dbNotifyInterval: Flow<Int> = context.dataStore.data.map { it[DB_NOTIFY_INTERVAL_KEY] ?: 30 }
  val enableBackgroundSync: Flow<Boolean> = context.dataStore.data.map { it[ENABLE_BACKGROUND_SYNC_KEY] ?: true }
  val enableNotifications: Flow<Boolean> = context.dataStore.data.map { it[ENABLE_NOTIFICATIONS_KEY] ?: true }

  val breakReminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[BREAK_REMINDER_ENABLED_KEY] ?: false }
  val breakReminderInterval: Flow<Int> = context.dataStore.data.map { it[BREAK_REMINDER_INTERVAL_KEY] ?: 60 }
  val bedtimeReminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[BEDTIME_REMINDER_ENABLED_KEY] ?: false }
  val bedtimeReminderStartTime: Flow<Long> = context.dataStore.data.map { it[BEDTIME_REMINDER_START_TIME_KEY] ?: (23 * 60 + 0).toLong() } // 23:00
  val bedtimeReminderEndTime: Flow<Long> = context.dataStore.data.map { it[BEDTIME_REMINDER_END_TIME_KEY] ?: (5 * 60 + 0).toLong() } // 05:00
  val bedtimeReminderWaitFinish: Flow<Boolean> = context.dataStore.data.map { it[BEDTIME_REMINDER_WAIT_FINISH_KEY] ?: true }
  val appLanguage: Flow<String> = context.dataStore.data.map { it[APP_LANGUAGE_KEY] ?: "auto" }

  val searchHistory: Flow<List<String>> = context.dataStore.data.map { preferences ->
    val json = preferences[SEARCH_HISTORY_KEY] ?: return@map emptyList()
    try {
      Json.decodeFromString<List<String>>(json)
    } catch (e: Exception) {
      print(e)
      emptyList()
    }
  }

  val lastActiveCheck: Flow<Long> = context.dataStore.data.map { it[LAST_ACTIVE_CHECK_KEY] ?: 0L }
  suspend fun setDoubleTapSkip(value: Int) {
    context.dataStore.edit { it[DOUBLE_TAP_SKIP_KEY] = value }
  }

  suspend fun setLongPressSpeed(value: Float) {
    context.dataStore.edit { it[LONG_PRESS_SPEED_KEY] = value }
  }

  suspend fun setLastActiveCheck(value: Long) {
    context.dataStore.edit { it[LAST_ACTIVE_CHECK_KEY] = value }
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

  suspend fun setAutoSyncNotify(value: Boolean) {
    context.dataStore.edit { it[AUTO_SYNC_NOTIFY_KEY] = value }
  }

  suspend fun setNotifyInterval(value: Int) {
    context.dataStore.edit { it[NOTIFY_INTERVAL_KEY] = value }
  }

  suspend fun setDbNotifyInterval(value: Int) {
    context.dataStore.edit { it[DB_NOTIFY_INTERVAL_KEY] = value }
  }

  suspend fun setEnableBackgroundSync(value: Boolean) {
    context.dataStore.edit { it[ENABLE_BACKGROUND_SYNC_KEY] = value }
  }

  suspend fun setEnableNotifications(value: Boolean) {
    context.dataStore.edit { it[ENABLE_NOTIFICATIONS_KEY] = value }
  }

  suspend fun setBreakReminderEnabled(value: Boolean) {
    context.dataStore.edit { it[BREAK_REMINDER_ENABLED_KEY] = value }
  }

  suspend fun setBreakReminderInterval(value: Int) {
    context.dataStore.edit { it[BREAK_REMINDER_INTERVAL_KEY] = value }
  }

  suspend fun setBedtimeReminderEnabled(value: Boolean) {
    context.dataStore.edit { it[BEDTIME_REMINDER_ENABLED_KEY] = value }
  }

  suspend fun setBedtimeReminderStartTime(minutes: Long) {
    context.dataStore.edit { it[BEDTIME_REMINDER_START_TIME_KEY] = minutes }
  }

  suspend fun setBedtimeReminderEndTime(minutes: Long) {
    context.dataStore.edit { it[BEDTIME_REMINDER_END_TIME_KEY] = minutes }
  }

  suspend fun setBedtimeReminderWaitFinish(value: Boolean) {
    context.dataStore.edit { it[BEDTIME_REMINDER_WAIT_FINISH_KEY] = value }
  }

  suspend fun setAppLanguage(value: String) {
    context.dataStore.edit { it[APP_LANGUAGE_KEY] = value }
  }

  suspend fun addSearchHistory(query: String) {
    context.dataStore.edit { preferences ->
      val currentHistory = searchHistory.first().toMutableList()
      currentHistory.remove(query)
      currentHistory.add(query)
      if (currentHistory.size > 10) {
        currentHistory.removeAt(0)
      }
      preferences[SEARCH_HISTORY_KEY] = Json.encodeToString(currentHistory.toList())
    }
  }

  suspend fun clearSearchHistory() {
    context.dataStore.edit { preferences ->
      preferences.remove(SEARCH_HISTORY_KEY)
    }
  }
}
