package git.shin.animevsub.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.R
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
  val autoNext: Boolean = true,
  val autoSkip: Boolean = false,
  val autoSyncNotify: Boolean = true,
  val enableBackgroundSync: Boolean = true,
  val enableNotifications: Boolean = true,
  val notifyInterval: Int = 15,
  val dbNotifyInterval: Int = 30,
  val volumeGesture: Boolean = true,
  val brightnessGesture: Boolean = true,
  val appLanguage: String = "auto",
  val isDeveloperMode: Boolean = false,
  val hideDonationPopup: Boolean = false,
  val breakReminderEnabled: Boolean = false,
  val breakReminderInterval: Int = 60,
  val bedtimeReminderEnabled: Boolean = false,
  val bedtimeReminderStartTime: Long = 23 * 60,
  val bedtimeReminderEndTime: Long = 5 * 60,
  val bedtimeReminderWaitFinish: Boolean = true,
  val screenTransition: String = "system",
  val dynamicColor: Boolean = false,
  val historySyncInterval: Int = 20,
  val appIcon: String = "default",
  val aiSummaryEnabled: Boolean = true,
  val aiRecapEnabled: Boolean = true,
  val geminiApiKey: String = "",
  val geminiModel: String = "gemini-2.5-flash",
  val availableModels: List<String> = emptyList(),
  val isLoadingModels: Boolean = false,
  val isTestingKey: Boolean = false,
  val testResult: String? = null,
  val testSuccess: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
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
      repository.getGeminiApiKey()?.let { v ->
        _uiState.update { it.copy(geminiApiKey = v) }
      }
    }
    viewModelScope.launch {
      repository.geminiModel.collect { v ->
        _uiState.update { it.copy(geminiModel = v) }
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

  fun setAiSummaryEnabled(value: Boolean) {
    viewModelScope.launch { repository.setAiSummaryEnabled(value) }
  }

  fun setAiRecapEnabled(value: Boolean) {
    viewModelScope.launch { repository.setAiRecapEnabled(value) }
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
}
