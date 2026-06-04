package git.shin.animevsub.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.R
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.repository.AnimeRepository
import git.shin.animevsub.utils.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
  val autoNext: Boolean = PreferencesManager.DEFAULT_AUTO_NEXT,
  val autoSkip: Boolean = PreferencesManager.DEFAULT_AUTO_SKIP,
  val autoSyncNotify: Boolean = PreferencesManager.DEFAULT_AUTO_SYNC_NOTIFY,
  val enableBackgroundSync: Boolean = PreferencesManager.DEFAULT_ENABLE_BACKGROUND_SYNC,
  val enableNotifications: Boolean = PreferencesManager.DEFAULT_ENABLE_NOTIFICATIONS,
  val notifyInterval: Int = PreferencesManager.DEFAULT_NOTIFY_INTERVAL,
  val dbNotifyInterval: Int = PreferencesManager.DEFAULT_DB_NOTIFY_INTERVAL,
  val volumeGesture: Boolean = PreferencesManager.DEFAULT_VOLUME_GESTURE,
  val brightnessGesture: Boolean = PreferencesManager.DEFAULT_BRIGHTNESS_GESTURE,
  val appLanguage: String = PreferencesManager.DEFAULT_APP_LANGUAGE,
  val isDeveloperMode: Boolean = PreferencesManager.DEFAULT_DEVELOPER_MODE,
  val hideDonationPopup: Boolean = PreferencesManager.DEFAULT_HIDE_DONATION_POPUP,
  val breakReminderEnabled: Boolean = PreferencesManager.DEFAULT_BREAK_REMINDER_ENABLED,
  val breakReminderInterval: Int = PreferencesManager.DEFAULT_BREAK_REMINDER_INTERVAL,
  val bedtimeReminderEnabled: Boolean = PreferencesManager.DEFAULT_BEDTIME_REMINDER_ENABLED,
  val bedtimeReminderStartTime: Long = PreferencesManager.DEFAULT_BEDTIME_REMINDER_START_TIME,
  val bedtimeReminderEndTime: Long = PreferencesManager.DEFAULT_BEDTIME_REMINDER_END_TIME,
  val bedtimeReminderWaitFinish: Boolean = PreferencesManager.DEFAULT_BEDTIME_REMINDER_WAIT_FINISH,
  val screenTransition: String = PreferencesManager.DEFAULT_SCREEN_TRANSITION,
  val dynamicColor: Boolean = PreferencesManager.DEFAULT_DYNAMIC_COLOR,
  val historySyncInterval: Int = PreferencesManager.DEFAULT_HISTORY_SYNC_INTERVAL,
  val appIcon: String = PreferencesManager.DEFAULT_APP_ICON,
  val aiSummaryEnabled: Boolean = PreferencesManager.DEFAULT_AI_SUMMARY_ENABLED,
  val aiRecapEnabled: Boolean = PreferencesManager.DEFAULT_AI_RECAP_ENABLED,
  val aiProvider: String = PreferencesManager.DEFAULT_AI_PROVIDER,
  val geminiApiKey: String = PreferencesManager.DEFAULT_GEMINI_API_KEY,
  val geminiModel: String = PreferencesManager.DEFAULT_GEMINI_MODEL,
  val openaiApiKey: String = PreferencesManager.DEFAULT_OPENAI_API_KEY,
  val openaiModel: String = PreferencesManager.DEFAULT_OPENAI_MODEL,
  val openaiEndpoint: String = PreferencesManager.DEFAULT_OPENAI_ENDPOINT,
  val claudeApiKey: String = PreferencesManager.DEFAULT_CLAUDE_API_KEY,
  val claudeModel: String = PreferencesManager.DEFAULT_CLAUDE_MODEL,
  val claudeEndpoint: String = PreferencesManager.DEFAULT_CLAUDE_ENDPOINT,
  val flagSecure: Boolean = PreferencesManager.DEFAULT_FLAG_SECURE,
  val minBufferMs: Int = PreferencesManager.DEFAULT_MIN_BUFFER_MS,
  val maxBufferMs: Int = PreferencesManager.DEFAULT_MAX_BUFFER_MS,
  val bufferForPlaybackMs: Int = PreferencesManager.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
  val bufferForPlaybackAfterRebufferMs: Int = PreferencesManager.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
  val prioritizeTimeOverSize: Boolean = PreferencesManager.DEFAULT_PRIORITIZE_TIME_OVER_SIZE,
  val dnsMode: String = PreferencesManager.DEFAULT_DNS_MODE,
  val customDnsUrl: String = PreferencesManager.DEFAULT_CUSTOM_DNS_URL,
  val availableModels: List<String> = emptyList(),
  val isLoadingModels: Boolean = false,
  val isTestingKey: Boolean = false,
  val testResult: String? = null,
  val testSuccess: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val repository: AnimeRepository,
  private val geminiRepository: git.shin.animevsub.data.repository.GeminiRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(SettingsUiState())
  val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      repository.autoNext.collect { v ->
        _uiState.update { it.copy(autoNext = v) }
      }
    }
    viewModelScope.launch {
      repository.autoSkip.collect { v ->
        _uiState.update { it.copy(autoSkip = v) }
      }
    }
    viewModelScope.launch {
      repository.autoSyncNotify.collect { v ->
        _uiState.update { it.copy(autoSyncNotify = v) }
      }
    }
    viewModelScope.launch {
      repository.enableBackgroundSync.collect { v ->
        _uiState.update { it.copy(enableBackgroundSync = v) }
      }
    }
    viewModelScope.launch {
      repository.enableNotifications.collect { v ->
        _uiState.update { it.copy(enableNotifications = v) }
      }
    }
    viewModelScope.launch {
      repository.notifyInterval.collect { v ->
        _uiState.update { it.copy(notifyInterval = v) }
      }
    }
    viewModelScope.launch {
      repository.dbNotifyInterval.collect { v ->
        _uiState.update { it.copy(dbNotifyInterval = v) }
      }
    }
    viewModelScope.launch {
      repository.volumeGesture.collect { v ->
        _uiState.update { it.copy(volumeGesture = v) }
      }
    }
    viewModelScope.launch {
      repository.brightnessGesture.collect { v ->
        _uiState.update { it.copy(brightnessGesture = v) }
      }
    }
    viewModelScope.launch {
      repository.breakReminderEnabled.collect { v ->
        _uiState.update { it.copy(breakReminderEnabled = v) }
      }
    }
    viewModelScope.launch {
      repository.breakReminderInterval.collect { v ->
        _uiState.update { it.copy(breakReminderInterval = v) }
      }
    }
    viewModelScope.launch {
      repository.bedtimeReminderEnabled.collect { v ->
        _uiState.update { it.copy(bedtimeReminderEnabled = v) }
      }
    }
    viewModelScope.launch {
      repository.bedtimeReminderStartTime.collect { v ->
        _uiState.update { it.copy(bedtimeReminderStartTime = v) }
      }
    }
    viewModelScope.launch {
      repository.bedtimeReminderEndTime.collect { v ->
        _uiState.update { it.copy(bedtimeReminderEndTime = v) }
      }
    }
    viewModelScope.launch {
      repository.bedtimeReminderWaitFinish.collect { v ->
        _uiState.update { it.copy(bedtimeReminderWaitFinish = v) }
      }
    }
    viewModelScope.launch {
      repository.appLanguage.collect { v ->
        _uiState.update { it.copy(appLanguage = v) }
      }
    }
    viewModelScope.launch {
      repository.developerMode.collect { v ->
        _uiState.update { it.copy(isDeveloperMode = v) }
      }
    }
    viewModelScope.launch {
      repository.hideDonationPopup.collect { v ->
        _uiState.update { it.copy(hideDonationPopup = v) }
      }
    }
    viewModelScope.launch {
      repository.screenTransition.collect { v ->
        _uiState.update { it.copy(screenTransition = v) }
      }
    }
    viewModelScope.launch {
      repository.dynamicColor.collect { v ->
        _uiState.update { it.copy(dynamicColor = v) }
      }
    }
    viewModelScope.launch {
      repository.appIcon.collect { v ->
        _uiState.update { it.copy(appIcon = v) }
      }
    }
    viewModelScope.launch {
      repository.historySyncInterval.collect { v ->
        _uiState.update { it.copy(historySyncInterval = v) }
      }
    }
    viewModelScope.launch {
      repository.aiSummaryEnabled.collect { v ->
        _uiState.update { it.copy(aiSummaryEnabled = v) }
      }
    }
    viewModelScope.launch {
      repository.aiRecapEnabled.collect { v ->
        _uiState.update { it.copy(aiRecapEnabled = v) }
      }
    }
    viewModelScope.launch {
      repository.aiProvider.collect { v ->
        _uiState.update { it.copy(aiProvider = v) }
      }
    }
    viewModelScope.launch {
      repository.openaiApiKey.collect { v ->
        _uiState.update { it.copy(openaiApiKey = v) }
      }
    }
    viewModelScope.launch {
      repository.openaiModel.collect { v ->
        _uiState.update { it.copy(openaiModel = v) }
      }
    }
    viewModelScope.launch {
      repository.openaiEndpoint.collect { v ->
        _uiState.update { it.copy(openaiEndpoint = v) }
      }
    }
    viewModelScope.launch {
      repository.claudeApiKey.collect { v ->
        _uiState.update { it.copy(claudeApiKey = v) }
      }
    }
    viewModelScope.launch {
      repository.claudeModel.collect { v ->
        _uiState.update { it.copy(claudeModel = v) }
      }
    }
    viewModelScope.launch {
      repository.claudeEndpoint.collect { v ->
        _uiState.update { it.copy(claudeEndpoint = v) }
      }
    }
    viewModelScope.launch {
      repository.getGeminiApiKey().let { v ->
        _uiState.update { it.copy(geminiApiKey = v) }
      }
    }
    viewModelScope.launch {
      repository.geminiModel.collect { v ->
        _uiState.update { it.copy(geminiModel = v) }
      }
    }
    viewModelScope.launch {
      repository.flagSecure.collect { v ->
        _uiState.update { it.copy(flagSecure = v) }
      }
    }
    viewModelScope.launch {
      repository.minBufferMs.collect { v ->
        _uiState.update { it.copy(minBufferMs = v) }
      }
    }
    viewModelScope.launch {
      repository.maxBufferMs.collect { v ->
        _uiState.update { it.copy(maxBufferMs = v) }
      }
    }
    viewModelScope.launch {
      repository.bufferForPlaybackMs.collect { v ->
        _uiState.update { it.copy(bufferForPlaybackMs = v) }
      }
    }
    viewModelScope.launch {
      repository.bufferForPlaybackAfterRebufferMs.collect { v ->
        _uiState.update { it.copy(bufferForPlaybackAfterRebufferMs = v) }
      }
    }
    viewModelScope.launch {
      repository.prioritizeTimeOverSize.collect { v ->
        _uiState.update { it.copy(prioritizeTimeOverSize = v) }
      }
    }
    viewModelScope.launch {
      repository.dnsMode.collect { v ->
        _uiState.update { it.copy(dnsMode = v) }
      }
    }
    viewModelScope.launch {
      repository.customDnsUrl.collect { v ->
        _uiState.update { it.copy(customDnsUrl = v) }
      }
    }
  }

  fun setGeminiApiKey(value: String) {
    _uiState.update { it.copy(geminiApiKey = value) }
    viewModelScope.launch { repository.setGeminiApiKey(value) }
  }

  fun loadAvailableModels() {
    val key = _uiState.value.geminiApiKey
    if (key.isBlank()) return

    viewModelScope.launch {
      _uiState.update { it.copy(isLoadingModels = true) }
      val models = geminiRepository.listAvailableModels()
      _uiState.update { it.copy(availableModels = models, isLoadingModels = false) }
    }
  }

  fun setGeminiModel(value: String) {
    viewModelScope.launch {
      repository.setGeminiModel(value)
      geminiRepository.saveModel(value)
    }
  }

  fun testGeminiKey() {
    val key = _uiState.value.geminiApiKey
    val model = _uiState.value.geminiModel
    if (key.isBlank()) return

    viewModelScope.launch {
      _uiState.update { it.copy(isTestingKey = true, testResult = null) }
      val result = geminiRepository.testApiKey(key)
      val message = if (result.isSuccess) {
        val response = result.getOrNull()
        if (response != null) {
          context.getString(R.string.api_key_test_success_with_response, model, response)
        } else {
          context.getString(R.string.api_key_test_success, model)
        }
      } else {
        val exception = result.exceptionOrNull()
        val errorMsg = exception?.message ?: context.getString(R.string.error_occurred)
        context.getString(R.string.api_key_test_error, errorMsg)
      }
      _uiState.update {
        it.copy(
          isTestingKey = false,
          testResult = message,
          testSuccess = result.isSuccess
        )
      }
    }
  }

  fun testOpenAIKey() {
    val key = _uiState.value.openaiApiKey
    if (key.isBlank()) return

    viewModelScope.launch {
      _uiState.update { it.copy(isTestingKey = true, testResult = null) }
      val result = geminiRepository.testOpenAI(key, _uiState.value.openaiModel, _uiState.value.openaiEndpoint)
      val message = if (result.isSuccess) {
        val response = result.getOrNull()
        if (response != null) {
          context.getString(R.string.api_key_test_success_with_response, _uiState.value.openaiModel, response)
        } else {
          context.getString(R.string.api_key_test_success, _uiState.value.openaiModel)
        }
      } else {
        val exception = result.exceptionOrNull()
        val errorMsg = exception?.message ?: context.getString(R.string.error_occurred)
        context.getString(R.string.api_key_test_error, errorMsg)
      }
      _uiState.update {
        it.copy(
          isTestingKey = false,
          testResult = message,
          testSuccess = result.isSuccess
        )
      }
    }
  }

  fun testClaudeKey() {
    val key = _uiState.value.claudeApiKey
    if (key.isBlank()) return

    viewModelScope.launch {
      _uiState.update { it.copy(isTestingKey = true, testResult = null) }
      val result = geminiRepository.testClaude(key, _uiState.value.claudeModel, _uiState.value.claudeEndpoint)
      val message = if (result.isSuccess) {
        val response = result.getOrNull()
        if (response != null) {
          context.getString(R.string.api_key_test_success_with_response, _uiState.value.claudeModel, response)
        } else {
          context.getString(R.string.api_key_test_success, _uiState.value.claudeModel)
        }
      } else {
        val exception = result.exceptionOrNull()
        val errorMsg = exception?.message ?: context.getString(R.string.error_occurred)
        context.getString(R.string.api_key_test_error, errorMsg)
      }
      _uiState.update {
        it.copy(
          isTestingKey = false,
          testResult = message,
          testSuccess = result.isSuccess
        )
      }
    }
  }

  fun setAiSummaryEnabled(value: Boolean) {
    viewModelScope.launch { repository.setAiSummaryEnabled(value) }
  }

  fun setAiRecapEnabled(value: Boolean) {
    viewModelScope.launch { repository.setAiRecapEnabled(value) }
  }

  fun setAiProvider(value: String) {
    viewModelScope.launch { repository.setAiProvider(value) }
    _uiState.update { it.copy(aiProvider = value) }
  }

  fun setOpenaiApiKey(value: String) {
    _uiState.update { it.copy(openaiApiKey = value) }
    viewModelScope.launch { repository.setOpenaiApiKey(value) }
  }

  fun setOpenaiModel(value: String) {
    viewModelScope.launch { repository.setOpenaiModel(value) }
    _uiState.update { it.copy(openaiModel = value) }
  }

  fun setOpenaiEndpoint(value: String) {
    _uiState.update { it.copy(openaiEndpoint = value) }
    viewModelScope.launch { repository.setOpenaiEndpoint(value) }
  }

  fun setClaudeApiKey(value: String) {
    _uiState.update { it.copy(claudeApiKey = value) }
    viewModelScope.launch { repository.setClaudeApiKey(value) }
  }

  fun setClaudeModel(value: String) {
    viewModelScope.launch { repository.setClaudeModel(value) }
    _uiState.update { it.copy(claudeModel = value) }
  }

  fun setClaudeEndpoint(value: String) {
    _uiState.update { it.copy(claudeEndpoint = value) }
    viewModelScope.launch { repository.setClaudeEndpoint(value) }
  }

  fun setAutoNext(value: Boolean) {
    viewModelScope.launch { repository.setAutoNext(value) }
  }

  fun setAutoSkip(value: Boolean) {
    viewModelScope.launch { repository.setAutoSkip(value) }
  }

  fun setAutoSyncNotify(value: Boolean) {
    viewModelScope.launch { repository.setAutoSyncNotify(value) }
  }

  fun setEnableBackgroundSync(value: Boolean) {
    viewModelScope.launch { repository.setEnableBackgroundSync(value) }
  }

  fun setEnableNotifications(value: Boolean) {
    viewModelScope.launch { repository.setEnableNotifications(value) }
  }

  fun setNotifyInterval(value: Int) {
    viewModelScope.launch { repository.setNotifyInterval(value) }
  }

  fun setDbNotifyInterval(value: Int) {
    viewModelScope.launch { repository.setDbNotifyInterval(value) }
  }

  fun setVolumeGesture(value: Boolean) {
    viewModelScope.launch { repository.setVolumeGesture(value) }
  }

  fun setBrightnessGesture(value: Boolean) {
    viewModelScope.launch { repository.setBrightnessGesture(value) }
  }

  fun setBreakReminderEnabled(value: Boolean) {
    viewModelScope.launch { repository.setBreakReminderEnabled(value) }
  }

  fun setBreakReminderInterval(value: Int) {
    viewModelScope.launch { repository.setBreakReminderInterval(value) }
  }

  fun setBedtimeReminderEnabled(value: Boolean) {
    viewModelScope.launch { repository.setBedtimeReminderEnabled(value) }
  }

  fun setBedtimeReminderStartTime(minutes: Long) {
    viewModelScope.launch { repository.setBedtimeReminderStartTime(minutes) }
  }

  fun setBedtimeReminderEndTime(minutes: Long) {
    viewModelScope.launch { repository.setBedtimeReminderEndTime(minutes) }
  }

  fun setBedtimeReminderWaitFinish(value: Boolean) {
    viewModelScope.launch { repository.setBedtimeReminderWaitFinish(value) }
  }

  fun setAppLanguage(value: String) {
    viewModelScope.launch { repository.setAppLanguage(value) }
  }

  fun setAppIcon(value: String) {
    viewModelScope.launch { repository.setAppIcon(value) }
  }

  fun setHideDonationPopup(value: Boolean) {
    viewModelScope.launch { repository.setHideDonationPopup(value) }
  }

  fun setScreenTransition(value: String) {
    viewModelScope.launch { repository.setScreenTransition(value) }
  }

  fun setDynamicColor(value: Boolean) {
    viewModelScope.launch { repository.setDynamicColor(value) }
  }

  fun setHistorySyncInterval(value: Int) {
    viewModelScope.launch { repository.setHistorySyncInterval(value) }
  }

  fun setFlagSecure(value: Boolean) {
    viewModelScope.launch { repository.setFlagSecure(value) }
  }

  fun setMinBufferMs(value: Int) {
    viewModelScope.launch {
      repository.setMinBufferMs(value)

      if (value >= uiState.value.maxBufferMs) {
        repository.setMaxBufferMs(value)
      }
    }
  }

  fun setMaxBufferMs(value: Int) {
    viewModelScope.launch { repository.setMaxBufferMs(value) }
  }

  fun setBufferForPlaybackMs(value: Int) {
    viewModelScope.launch {
      repository.setBufferForPlaybackMs(value)

      if (value >= uiState.value.minBufferMs) {
        repository.setMinBufferMs(value)
      }
      if (value >= uiState.value.maxBufferMs) {
        repository.setMaxBufferMs(value)
      }
    }
  }

  fun setBufferForPlaybackAfterRebufferMs(value: Int) {
    viewModelScope.launch {
      repository.setBufferForPlaybackAfterRebufferMs(value)

      if (value >= uiState.value.minBufferMs) {
        repository.setMinBufferMs(value)
      }
      if (value >= uiState.value.maxBufferMs) {
        repository.setMaxBufferMs(value)
      }
    }
  }

  fun setPrioritizeTimeOverSize(value: Boolean) {
    viewModelScope.launch { repository.setPrioritizeTimeOverSize(value) }
  }

  fun setDnsMode(value: String) {
    viewModelScope.launch { repository.setDnsMode(value) }
  }

  fun setCustomDnsUrl(value: String) {
    viewModelScope.launch { repository.setCustomDnsUrl(value) }
  }

  fun testNotification() {
    val helper = NotificationHelper(context)
    helper.showNotification(
      title = "Test Notification",
      message = "This is a test notification with a thumbnail.",
      imageUrl = "https://picsum.photos/536/354"
    )
  }
}
