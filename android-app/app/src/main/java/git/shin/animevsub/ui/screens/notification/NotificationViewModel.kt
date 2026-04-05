package git.shin.animevsub.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
  data class ShowToast(val message: String) : NotificationUiEvent()
}

data class NotificationUiState(
  val isLoading: Boolean = true,
  val isRefreshing: Boolean = false,
  val data: NotificationData? = null,
  val error: String? = null,
  val isLoggedIn: Boolean = false
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
      repository.notifications.collect { data ->
        _uiState.value = _uiState.value.copy(
          data = data,
          isLoading = false,
          isRefreshing = false
        )
      }
    }

    viewModelScope.launch {
      repository.isLoggedIn.collect { loggedIn ->
        _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn)
        if (loggedIn) {
          loadNotifications()
        } else {
          _uiState.value = _uiState.value.copy(isLoading = false)
        }
      }
    }
  }

  fun loadNotifications(isRefreshing: Boolean = false) {
    viewModelScope.launch {
      if (!isRefreshing) {
        _uiState.value = _uiState.value.copy(isLoading = true)
      } else {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
      }
      _uiState.value = _uiState.value.copy(error = null)

      repository.getNotifications()
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
          _uiEvent.emit(NotificationUiEvent.ShowToast(e.message ?: "Có lỗi xảy ra"))
        }
    }
  }
}
