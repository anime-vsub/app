package git.shin.animevsub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
    const val DEFAULT_AUTO_NEXT = true
    const val DEFAULT_AUTO_SKIP = false
    const val DEFAULT_VOLUME_GESTURE = true
    const val DEFAULT_BRIGHTNESS_GESTURE = true
    const val DEFAULT_DOUBLE_TAP_SKIP = 10
    const val DEFAULT_LONG_PRESS_SPEED = 2.0f
    const val DEFAULT_AUTO_SYNC_NOTIFY = false
    const val DEFAULT_NOTIFY_INTERVAL = 15
    const val DEFAULT_DB_NOTIFY_INTERVAL = 30
    const val DEFAULT_ENABLE_BACKGROUND_SYNC = true
    const val DEFAULT_ENABLE_NOTIFICATIONS = true
    const val DEFAULT_BREAK_REMINDER_ENABLED = false
    const val DEFAULT_BREAK_REMINDER_INTERVAL = 60
    const val DEFAULT_BEDTIME_REMINDER_ENABLED = false
    const val DEFAULT_BEDTIME_REMINDER_START_TIME = (23 * 60 + 0).toLong()
    const val DEFAULT_BEDTIME_REMINDER_END_TIME = (5 * 60 + 0).toLong()
    const val DEFAULT_BEDTIME_REMINDER_WAIT_FINISH = true
    const val DEFAULT_APP_LANGUAGE = "auto"
    const val DEFAULT_LAST_DONATION_ALERT = 0L
    const val DEFAULT_DEVELOPER_MODE = false
    const val DEFAULT_HIDE_DONATION_POPUP = false
    const val DEFAULT_SCREEN_TRANSITION = "system"
    const val DEFAULT_DYNAMIC_COLOR = false
    const val DEFAULT_HISTORY_SYNC_INTERVAL = 20
    const val DEFAULT_APP_ICON = "default"
    const val DEFAULT_AI_SUMMARY_ENABLED = true
    const val DEFAULT_AI_RECAP_ENABLED = true
    const val DEFAULT_AI_PROVIDER = "gemini"
    const val DEFAULT_OPENAI_API_KEY = ""
    const val DEFAULT_OPENAI_MODEL = "gpt-4o-mini"
    const val DEFAULT_OPENAI_ENDPOINT = ""
    const val DEFAULT_CLAUDE_API_KEY = ""
    const val DEFAULT_CLAUDE_MODEL = "claude-sonnet-4-20250514"
    const val DEFAULT_CLAUDE_ENDPOINT = ""
    const val DEFAULT_GEMINI_API_KEY = ""
    const val DEFAULT_GEMINI_MODEL = "gemini-1.5-flash"
    const val DEFAULT_FLAG_SECURE = true
    const val DEFAULT_PREFERRED_SERVER = ""
    const val DEFAULT_MIN_BUFFER_MS = 50_000
    const val DEFAULT_MAX_BUFFER_MS = 120_000
    const val DEFAULT_BUFFER_FOR_PLAYBACK_MS = 10_000
    const val DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 12_000
    const val DEFAULT_PRIORITIZE_TIME_OVER_SIZE = true
    const val DEFAULT_LAST_ACTIVE_CHECK = 0L
    const val DEFAULT_DNS_MODE = "cloudflare"
    const val DEFAULT_CUSTOM_DNS_URL = "https://dns.google/dns-query"

    const val DNS_MODE_SYSTEM = "system"
    const val DNS_MODE_GOOGLE = "google"
    const val DNS_MODE_CLOUDFLARE = "cloudflare"
    const val DNS_MODE_QUAD9 = "quad9"
    const val DNS_MODE_CUSTOM = "custom"

    private val AUTO_NEXT_KEY = booleanPreferencesKey("auto_next")
    private val AUTO_SKIP_KEY = booleanPreferencesKey("auto_skip")
    private val VOLUME_GESTURE_KEY = booleanPreferencesKey("volume_gesture")
    private val BRIGHTNESS_GESTURE_KEY = booleanPreferencesKey("brightness_gesture")
    private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
    private val AUTO_SYNC_NOTIFY_KEY = booleanPreferencesKey("auto_sync_notify")
    private val NOTIFY_INTERVAL_KEY = intPreferencesKey("notify_interval")
    private val DB_NOTIFY_INTERVAL_KEY = intPreferencesKey("db_notify_interval")
    private val ENABLE_BACKGROUND_SYNC_KEY = booleanPreferencesKey("enable_background_sync")
    private val ENABLE_NOTIFICATIONS_KEY = booleanPreferencesKey("enable_notifications")
    private val LAST_ACTIVE_CHECK_KEY = longPreferencesKey("last_active_check")
    private val DOUBLE_TAP_SKIP_KEY = intPreferencesKey("double_tap_skip")
    private val LONG_PRESS_SPEED_KEY = androidx.datastore.preferences.core.floatPreferencesKey("long_press_speed")
    private val BREAK_REMINDER_ENABLED_KEY = booleanPreferencesKey("break_reminder_enabled")
    private val BREAK_REMINDER_INTERVAL_KEY = intPreferencesKey("break_reminder_interval")
    private val BEDTIME_REMINDER_ENABLED_KEY = booleanPreferencesKey("bedtime_reminder_enabled")
    private val BEDTIME_REMINDER_START_TIME_KEY = longPreferencesKey("bedtime_reminder_start_time")
    private val BEDTIME_REMINDER_END_TIME_KEY = longPreferencesKey("bedtime_reminder_end_time")
    private val BEDTIME_REMINDER_WAIT_FINISH_KEY = booleanPreferencesKey("bedtime_reminder_wait_finish")
    private val APP_LANGUAGE_KEY = stringPreferencesKey("app_language")
    private val LAST_DONATION_ALERT_KEY = longPreferencesKey("last_donation_alert")
    private val DEVELOPER_MODE_KEY = booleanPreferencesKey("developer_mode")
    private val HIDE_DONATION_POPUP_KEY = booleanPreferencesKey("hide_donation_popup")
    private val SCREEN_TRANSITION_KEY = stringPreferencesKey("screen_transition")
    private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
    private val HISTORY_SYNC_INTERVAL_KEY = intPreferencesKey("history_sync_interval")
    private val APP_ICON_KEY = stringPreferencesKey("app_icon")
    private val AI_SUMMARY_ENABLED_KEY = booleanPreferencesKey("ai_summary_enabled")
    private val AI_RECAP_ENABLED_KEY = booleanPreferencesKey("ai_recap_enabled")
    private val AI_PROVIDER_KEY = stringPreferencesKey("ai_provider")
    private val OPENAI_API_KEY_KEY = stringPreferencesKey("openai_api_key")
    private val OPENAI_MODEL_KEY = stringPreferencesKey("openai_model")
    private val OPENAI_ENDPOINT_KEY = stringPreferencesKey("openai_endpoint")
    private val CLAUDE_API_KEY_KEY = stringPreferencesKey("claude_api_key")
    private val CLAUDE_MODEL_KEY = stringPreferencesKey("claude_model")
    private val CLAUDE_ENDPOINT_KEY = stringPreferencesKey("claude_endpoint")
    private val GEMINI_API_KEY_KEY = stringPreferencesKey("gemini_api_key")
    private val GEMINI_MODEL_KEY = stringPreferencesKey("gemini_model")
    private val FLAG_SECURE_KEY = booleanPreferencesKey("flag_secure")
    private val PREFERRED_SERVER_KEY = stringPreferencesKey("preferred_server")
    private val MIN_BUFFER_MS_KEY = intPreferencesKey("min_buffer_ms")
    private val MAX_BUFFER_MS_KEY = intPreferencesKey("max_buffer_ms")
    private val BUFFER_FOR_PLAYBACK_MS_KEY = intPreferencesKey("buffer_for_playback_ms")
    private val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS_KEY = intPreferencesKey("buffer_for_playback_after_rebuffer_ms")
    private val PRIORITIZE_TIME_OVER_SIZE_KEY = booleanPreferencesKey("prioritize_time_over_size")
    private val DNS_MODE_KEY = stringPreferencesKey("dns_mode")
    private val CUSTOM_DNS_URL_KEY = stringPreferencesKey("custom_dns_url")
  }

  val autoNext: Flow<Boolean> = context.dataStore.data.map { it[AUTO_NEXT_KEY] ?: DEFAULT_AUTO_NEXT }
  val autoSkip: Flow<Boolean> = context.dataStore.data.map { it[AUTO_SKIP_KEY] ?: DEFAULT_AUTO_SKIP }
  val volumeGesture: Flow<Boolean> = context.dataStore.data.map { it[VOLUME_GESTURE_KEY] ?: DEFAULT_VOLUME_GESTURE }
  val brightnessGesture: Flow<Boolean> =
    context.dataStore.data.map { it[BRIGHTNESS_GESTURE_KEY] ?: DEFAULT_BRIGHTNESS_GESTURE }
  val doubleTapSkip: Flow<Int> = context.dataStore.data.map { it[DOUBLE_TAP_SKIP_KEY] ?: DEFAULT_DOUBLE_TAP_SKIP }
  val longPressSpeed: Flow<Float> = context.dataStore.data.map { it[LONG_PRESS_SPEED_KEY] ?: DEFAULT_LONG_PRESS_SPEED }
  val autoSyncNotify: Flow<Boolean> =
    context.dataStore.data.map { it[AUTO_SYNC_NOTIFY_KEY] ?: DEFAULT_AUTO_SYNC_NOTIFY }

  val notifyInterval: Flow<Int> = context.dataStore.data.map { it[NOTIFY_INTERVAL_KEY] ?: DEFAULT_NOTIFY_INTERVAL }
  val dbNotifyInterval: Flow<Int> = context.dataStore.data.map { it[DB_NOTIFY_INTERVAL_KEY] ?: DEFAULT_DB_NOTIFY_INTERVAL }
  val enableBackgroundSync: Flow<Boolean> = context.dataStore.data.map { it[ENABLE_BACKGROUND_SYNC_KEY] ?: DEFAULT_ENABLE_BACKGROUND_SYNC }
  val enableNotifications: Flow<Boolean> = context.dataStore.data.map { it[ENABLE_NOTIFICATIONS_KEY] ?: DEFAULT_ENABLE_NOTIFICATIONS }

  val breakReminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[BREAK_REMINDER_ENABLED_KEY] ?: DEFAULT_BREAK_REMINDER_ENABLED }
  val breakReminderInterval: Flow<Int> = context.dataStore.data.map { it[BREAK_REMINDER_INTERVAL_KEY] ?: DEFAULT_BREAK_REMINDER_INTERVAL }
  val bedtimeReminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[BEDTIME_REMINDER_ENABLED_KEY] ?: DEFAULT_BEDTIME_REMINDER_ENABLED }
  val bedtimeReminderStartTime: Flow<Long> = context.dataStore.data.map { it[BEDTIME_REMINDER_START_TIME_KEY] ?: DEFAULT_BEDTIME_REMINDER_START_TIME }
  val bedtimeReminderEndTime: Flow<Long> = context.dataStore.data.map { it[BEDTIME_REMINDER_END_TIME_KEY] ?: DEFAULT_BEDTIME_REMINDER_END_TIME }
  val bedtimeReminderWaitFinish: Flow<Boolean> = context.dataStore.data.map { it[BEDTIME_REMINDER_WAIT_FINISH_KEY] ?: DEFAULT_BEDTIME_REMINDER_WAIT_FINISH }
  val appLanguage: Flow<String> = context.dataStore.data.map { it[APP_LANGUAGE_KEY] ?: DEFAULT_APP_LANGUAGE }
  val lastDonationAlert: Flow<Long> = context.dataStore.data.map { it[LAST_DONATION_ALERT_KEY] ?: DEFAULT_LAST_DONATION_ALERT }
  val developerMode: Flow<Boolean> = context.dataStore.data.map { it[DEVELOPER_MODE_KEY] ?: DEFAULT_DEVELOPER_MODE }
  val hideDonationPopup: Flow<Boolean> = context.dataStore.data.map { it[HIDE_DONATION_POPUP_KEY] ?: DEFAULT_HIDE_DONATION_POPUP }
  val screenTransition: Flow<String> = context.dataStore.data.map { it[SCREEN_TRANSITION_KEY] ?: DEFAULT_SCREEN_TRANSITION }
  val dynamicColor: Flow<Boolean> = context.dataStore.data.map { it[DYNAMIC_COLOR_KEY] ?: DEFAULT_DYNAMIC_COLOR }
  val historySyncInterval: Flow<Int> = context.dataStore.data.map { it[HISTORY_SYNC_INTERVAL_KEY] ?: DEFAULT_HISTORY_SYNC_INTERVAL }
  val appIcon: Flow<String> = context.dataStore.data.map { it[APP_ICON_KEY] ?: DEFAULT_APP_ICON }
  val aiSummaryEnabled: Flow<Boolean> = context.dataStore.data.map { it[AI_SUMMARY_ENABLED_KEY] ?: DEFAULT_AI_SUMMARY_ENABLED }
  val aiRecapEnabled: Flow<Boolean> = context.dataStore.data.map { it[AI_RECAP_ENABLED_KEY] ?: DEFAULT_AI_RECAP_ENABLED }
  val aiProvider: Flow<String> = context.dataStore.data.map { it[AI_PROVIDER_KEY] ?: DEFAULT_AI_PROVIDER }
  val openaiApiKey: Flow<String> = context.dataStore.data.map { it[OPENAI_API_KEY_KEY] ?: DEFAULT_OPENAI_API_KEY }
  val openaiModel: Flow<String> = context.dataStore.data.map { it[OPENAI_MODEL_KEY] ?: DEFAULT_OPENAI_MODEL }
  val openaiEndpoint: Flow<String> = context.dataStore.data.map { it[OPENAI_ENDPOINT_KEY] ?: DEFAULT_OPENAI_ENDPOINT }
  val claudeApiKey: Flow<String> = context.dataStore.data.map { it[CLAUDE_API_KEY_KEY] ?: DEFAULT_CLAUDE_API_KEY }
  val claudeModel: Flow<String> = context.dataStore.data.map { it[CLAUDE_MODEL_KEY] ?: DEFAULT_CLAUDE_MODEL }
  val claudeEndpoint: Flow<String> = context.dataStore.data.map { it[CLAUDE_ENDPOINT_KEY] ?: DEFAULT_CLAUDE_ENDPOINT }
  val geminiApiKey: Flow<String> = context.dataStore.data.map { it[GEMINI_API_KEY_KEY] ?: DEFAULT_GEMINI_API_KEY }
  val geminiModel: Flow<String> = context.dataStore.data.map { it[GEMINI_MODEL_KEY] ?: DEFAULT_GEMINI_MODEL }
  val flagSecure: Flow<Boolean> = context.dataStore.data.map { it[FLAG_SECURE_KEY] ?: DEFAULT_FLAG_SECURE }
  val preferredServer: Flow<String> = context.dataStore.data.map { it[PREFERRED_SERVER_KEY] ?: DEFAULT_PREFERRED_SERVER }

  val minBufferMs: Flow<Int> = context.dataStore.data.map { it[MIN_BUFFER_MS_KEY] ?: DEFAULT_MIN_BUFFER_MS }
  val maxBufferMs: Flow<Int> = context.dataStore.data.map { it[MAX_BUFFER_MS_KEY] ?: DEFAULT_MAX_BUFFER_MS }
  val bufferForPlaybackMs: Flow<Int> = context.dataStore.data.map { it[BUFFER_FOR_PLAYBACK_MS_KEY] ?: DEFAULT_BUFFER_FOR_PLAYBACK_MS }
  val bufferForPlaybackAfterRebufferMs: Flow<Int> = context.dataStore.data.map { it[BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS_KEY] ?: DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS }
  val prioritizeTimeOverSize: Flow<Boolean> = context.dataStore.data.map { it[PRIORITIZE_TIME_OVER_SIZE_KEY] ?: DEFAULT_PRIORITIZE_TIME_OVER_SIZE }

  val dnsMode: Flow<String> = context.dataStore.data.map { it[DNS_MODE_KEY] ?: DEFAULT_DNS_MODE }
  val customDnsUrl: Flow<String> = context.dataStore.data.map { it[CUSTOM_DNS_URL_KEY] ?: DEFAULT_CUSTOM_DNS_URL }

  val searchHistory: Flow<List<String>> = context.dataStore.data.map { preferences ->
    val json = preferences[SEARCH_HISTORY_KEY] ?: return@map emptyList()
    try {
      Json.decodeFromString<List<String>>(json)
    } catch (e: Exception) {
      print(e)
      emptyList()
    }
  }

  val lastActiveCheck: Flow<Long> = context.dataStore.data.map { it[LAST_ACTIVE_CHECK_KEY] ?: DEFAULT_LAST_ACTIVE_CHECK }
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

  suspend fun setLastDonationAlert(value: Long) {
    context.dataStore.edit { it[LAST_DONATION_ALERT_KEY] = value }
  }

  suspend fun setDeveloperMode(value: Boolean) {
    context.dataStore.edit { it[DEVELOPER_MODE_KEY] = value }
  }

  suspend fun setHideDonationPopup(value: Boolean) {
    context.dataStore.edit { it[HIDE_DONATION_POPUP_KEY] = value }
  }

  suspend fun setScreenTransition(value: String) {
    context.dataStore.edit { it[SCREEN_TRANSITION_KEY] = value }
  }

  suspend fun setDynamicColor(value: Boolean) {
    context.dataStore.edit { it[DYNAMIC_COLOR_KEY] = value }
  }

  suspend fun setAppIcon(value: String) {
    context.dataStore.edit { it[APP_ICON_KEY] = value }
  }

  suspend fun setHistorySyncInterval(value: Int) {
    context.dataStore.edit { it[HISTORY_SYNC_INTERVAL_KEY] = value }
  }

  suspend fun setAiSummaryEnabled(value: Boolean) {
    context.dataStore.edit { it[AI_SUMMARY_ENABLED_KEY] = value }
  }

  suspend fun setAiRecapEnabled(value: Boolean) {
    context.dataStore.edit { it[AI_RECAP_ENABLED_KEY] = value }
  }

  suspend fun setAiProvider(value: String) {
    context.dataStore.edit { it[AI_PROVIDER_KEY] = value }
  }

  suspend fun setOpenaiApiKey(value: String) {
    context.dataStore.edit { it[OPENAI_API_KEY_KEY] = value }
  }

  suspend fun setOpenaiModel(value: String) {
    context.dataStore.edit { it[OPENAI_MODEL_KEY] = value }
  }

  suspend fun setOpenaiEndpoint(value: String) {
    context.dataStore.edit { it[OPENAI_ENDPOINT_KEY] = value }
  }

  suspend fun setClaudeApiKey(value: String) {
    context.dataStore.edit { it[CLAUDE_API_KEY_KEY] = value }
  }

  suspend fun setClaudeModel(value: String) {
    context.dataStore.edit { it[CLAUDE_MODEL_KEY] = value }
  }

  suspend fun setClaudeEndpoint(value: String) {
    context.dataStore.edit { it[CLAUDE_ENDPOINT_KEY] = value }
  }

  suspend fun setGeminiApiKey(value: String) {
    context.dataStore.edit { it[GEMINI_API_KEY_KEY] = value }
  }

  suspend fun setGeminiModel(value: String) {
    context.dataStore.edit { it[GEMINI_MODEL_KEY] = value }
  }

  suspend fun setFlagSecure(value: Boolean) {
    context.dataStore.edit { it[FLAG_SECURE_KEY] = value }
  }

  suspend fun setPreferredServer(value: String) {
    context.dataStore.edit { it[PREFERRED_SERVER_KEY] = value }
  }

  suspend fun setMinBufferMs(value: Int) {
    context.dataStore.edit { it[MIN_BUFFER_MS_KEY] = value }
  }

  suspend fun setMaxBufferMs(value: Int) {
    context.dataStore.edit { it[MAX_BUFFER_MS_KEY] = value }
  }

  suspend fun setBufferForPlaybackMs(value: Int) {
    context.dataStore.edit { it[BUFFER_FOR_PLAYBACK_MS_KEY] = value }
  }

  suspend fun setBufferForPlaybackAfterRebufferMs(value: Int) {
    context.dataStore.edit { it[BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS_KEY] = value }
  }

  suspend fun setPrioritizeTimeOverSize(value: Boolean) {
    context.dataStore.edit { it[PRIORITIZE_TIME_OVER_SIZE_KEY] = value }
  }

  suspend fun setDnsMode(value: String) {
    context.dataStore.edit { it[DNS_MODE_KEY] = value }
  }

  suspend fun setCustomDnsUrl(value: String) {
    context.dataStore.edit { it[CUSTOM_DNS_URL_KEY] = value }
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
