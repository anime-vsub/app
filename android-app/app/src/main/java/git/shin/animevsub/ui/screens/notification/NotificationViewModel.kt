package git.shin.animevsub.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.data.model.NotificationData
import git.shin.animevsub.data.model.Trigger
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NotificationUiEvent {
  data class ShowToast(val messageRes: Int? = null, val message: String? = null) : NotificationUiEvent()
}

data class NotificationUiState(
  val isLoading: Boolean = true,
  val isRefreshing: Boolean = false,
  val data: NotificationData? = null,
  val error: String? = null,
  val isLoggedIn: Boolean = false,
  val isAuthReady: Boolean = false
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
  private val repository: AnimeRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(NotificationUiState())
  val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

  private val _uiEvent = MutableSharedFlow<NotificationUiEvent>()
  val uiEvent: SharedFlow<NotificationUiEvent> = _uiEvent.asSharedFlow()

  init {
    viewModelScope.launch {

      launch {
        repository.isLoggedIn.collect { loggedIn ->
          _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn, isAuthReady = true)
          if (loggedIn) {
            loadNotifications()
          } else {
            _uiState.value = _uiState.value.copy(isLoading = false, data = null)
          }
        }
      }
    }
  }

  fun loadNotifications(isRefreshing: Boolean = false) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        isLoading = !isRefreshing,
        isRefreshing = isRefreshing,
        error = null
      )
      repository.getNotifications()
        .onSuccess { data ->
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            isRefreshing = false,
            data = data,
            error = null
          )
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            isRefreshing = false,
            error = e.message
          )
        }
    }
  }

  fun refresh() {
    loadNotifications(isRefreshing = true)
  }

  fun retry() {
    loadNotifications()
  }

  fun onTrigger(trigger: Trigger) {
    val previousData = _uiState.value.data ?: return
    val currentItems = previousData.items

    // Optimistic Update
    val newList = currentItems.filter { it.closeTrigger != trigger }
    _uiState.value = _uiState.value.copy(
      data = previousData.copy(items = newList)
    )

    viewModelScope.launch {
      repository.onTrigger(trigger)
        .onFailure { e ->
          // Rollback
          _uiState.value = _uiState.value.copy(data = previousData)
          _uiEvent.emit(
            if (e.message != null) NotificationUiEvent.ShowToast(message = e.message)
            else NotificationUiEvent.ShowToast(messageRes = R.string.error_occurred)
          )
        }
    }
  }
}
