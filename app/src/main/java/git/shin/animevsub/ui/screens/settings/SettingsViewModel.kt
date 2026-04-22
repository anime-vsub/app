package git.shin.animevsub.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
  val screenTransition: String = "system"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val repository: AnimeRepository
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

  fun setHideDonationPopup(value: Boolean) {
    viewModelScope.launch { repository.setHideDonationPopup(value) }
  }

  fun setScreenTransition(value: String) {
    viewModelScope.launch { repository.setScreenTransition(value) }
  }
}
