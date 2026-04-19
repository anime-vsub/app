package git.shin.animevsub.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.data.model.DbNotificationCount
import git.shin.animevsub.data.model.DbNotificationItem
import git.shin.animevsub.data.model.NotificationData
import git.shin.animevsub.data.repository.AnimeRepository
import git.shin.animevsub.data.repository.NotificationDatabaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NotificationUiEvent {
  data class ShowToast(val messageRes: Int? = null, val message: String? = null) : NotificationUiEvent()
}

data class NotificationUiState(
  val isLoading: Boolean = true,
  val isRefreshing: Boolean = false,
  val data: NotificationData? = null,
  val dbNotifications: List<DbNotificationItem> = emptyList(),
  val dbNotificationCount: DbNotificationCount? = null,
  val isSyncing: Boolean = false,
  val error: String? = null,
  val isLoggedIn: Boolean = false,
  val isAuthReady: Boolean = false,
  val dbPage: Int = 1,
  val hasMoreDb: Boolean = true,
  val autoSync: Boolean = false
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
  private val repository: AnimeRepository,
  private val notificationDbRepository: NotificationDatabaseRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(NotificationUiState())
  val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

  private val _uiEvent = MutableSharedFlow<NotificationUiEvent>()
  val uiEvent: SharedFlow<NotificationUiEvent> = _uiEvent.asSharedFlow()

  init {
    viewModelScope.launch {
      launch {
        repository.isLoggedIn.collect { loggedIn ->
          _uiState.update { it.copy(isLoggedIn = loggedIn, isAuthReady = true) }
          if (loggedIn) {
            loadNotifications()
            loadDbNotifications(isRefreshing = true)
          } else {
            _uiState.update {
              it.copy(
                isLoading = false,
                data = null,
                dbNotifications = emptyList(),
                dbNotificationCount = null
              )
            }
          }
        }
      }

      launch {
        repository.notifications.collect { data ->
          _uiState.update { it.copy(data = data) }
        }
      }

      launch {
        notificationDbRepository.dbNotifications.collect { items ->
          _uiState.update { it.copy(dbNotifications = items) }
        }
      }

      launch {
        notificationDbRepository.dbNotificationCount.collect { count ->
          _uiState.update { it.copy(dbNotificationCount = count) }
        }
      }

      launch {
        repository.autoSyncNotify.collect { enabled ->
          _uiState.update { it.copy(autoSync = enabled) }
        }
      }
    }
  }

  fun loadNotifications(isRefreshing: Boolean = false) {
    viewModelScope.launch {
      _uiState.update {
        it.copy(
          isLoading = !isRefreshing,
          isRefreshing = isRefreshing,
          error = null
        )
      }
      repository.getNotifications()
        .onSuccess {
          _uiState.update {
            it.copy(
              isLoading = false,
              isRefreshing = false,
              error = null
            )
          }
        }
        .onFailure { e ->
          _uiState.update {
            it.copy(
              isLoading = false,
              isRefreshing = false,
              error = e.message
            )
          }
        }
    }
  }

  fun loadDbNotifications(isRefreshing: Boolean = false) {
    viewModelScope.launch {
      val currentPage = if (isRefreshing) 1 else _uiState.value.dbPage
      notificationDbRepository.queryNotify(currentPage)
        .onSuccess { items ->
          // Repo handles list merging now, we just update pagination state
          _uiState.update {
            it.copy(
              dbPage = if (items.isEmpty()) currentPage else currentPage + 1,
              hasMoreDb = items.isNotEmpty()
            )
          }
        }
    }
  }

  fun startSync() {
    viewModelScope.launch {
      repository.startSyncNotifications()
    }
  }

  fun refresh() {
    loadNotifications(isRefreshing = true)
    loadDbNotifications(isRefreshing = true)
  }

  fun retry() {
    loadNotifications()
    loadDbNotifications()
  }

  fun onTrigger(trigger: git.shin.animevsub.data.model.Trigger) {
    viewModelScope.launch {
      repository.onTrigger(trigger)
        .onFailure { e ->
          _uiEvent.emit(
            if (e.message != null) {
              NotificationUiEvent.ShowToast(message = e.message)
            } else {
              NotificationUiEvent.ShowToast(messageRes = R.string.error_occurred)
            }
          )
        }
    }
  }

  fun deleteDbNotification(season: String, chapId: String? = null) {
    viewModelScope.launch {
      notificationDbRepository.deleteNotify(season, chapId)
        .onFailure { e ->
          _uiEvent.emit(NotificationUiEvent.ShowToast(message = e.message))
        }
    }
  }

  fun setAutoSync(enabled: Boolean) {
    viewModelScope.launch {
      repository.setAutoSyncNotify(enabled)
    }
  }
}
