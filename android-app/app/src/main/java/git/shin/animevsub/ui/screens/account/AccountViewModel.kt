package git.shin.animevsub.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
  val isLoggedIn: Boolean = false,
  val user: User? = null,
  val autoNext: Boolean = true,
  val autoSkip: Boolean = false,
  val volumeGesture: Boolean = true,
  val brightnessGesture: Boolean = true,
  val movieMode: Boolean = false,
  val showComments: Boolean = true,
  val infiniteScroll: Boolean = true,
  val server: String = "DU",
  val histories: List<HistoryItem> = emptyList(),
  val follows: List<AnimeCard> = emptyList(),
  val isLoadingHistory: Boolean = false,
  val isLoadingFollows: Boolean = false,
  val historyError: String? = null,
  val followsError: String? = null,
)

@HiltViewModel
class AccountViewModel @Inject constructor(
  private val repository: AnimeRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(AccountUiState())
  val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      launch {
        repository.isLoggedIn.collect { isLoggedIn ->
          _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)
          if (isLoggedIn) {
            refreshHistory()
            refreshFollows()
          }
        }
      }
      launch {
        repository.user.collect { user ->
          _uiState.value = _uiState.value.copy(user = user)
        }
      }
      launch {
        repository.autoNext.collect { v ->
          _uiState.value = _uiState.value.copy(autoNext = v)
        }
      }
      launch {
        repository.autoSkip.collect { v ->
          _uiState.value = _uiState.value.copy(autoSkip = v)
        }
      }
      launch {
        repository.volumeGesture.collect { v ->
          _uiState.value = _uiState.value.copy(volumeGesture = v)
        }
      }
      launch {
        repository.brightnessGesture.collect { v ->
          _uiState.value = _uiState.value.copy(brightnessGesture = v)
        }
      }
      launch {
        repository.movieMode.collect { v ->
          _uiState.value = _uiState.value.copy(movieMode = v)
        }
      }
      launch {
        repository.showComments.collect { v ->
          _uiState.value = _uiState.value.copy(showComments = v)
        }
      }
      launch {
        repository.infiniteScroll.collect { v ->
          _uiState.value = _uiState.value.copy(infiniteScroll = v)
        }
      }
    }
  }

  fun refreshHistory() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoadingHistory = true, historyError = null)
      repository.getHistory(1)
        .onSuccess { list ->
          _uiState.value = _uiState.value.copy(histories = list.take(10), isLoadingHistory = false)
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(isLoadingHistory = false, historyError = e.message)
        }
    }
  }

  fun refreshFollows() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoadingFollows = true, followsError = null)
      repository.getFollows(1)
        .onSuccess { page ->
          _uiState.value = _uiState.value.copy(follows = page.items.take(10), isLoadingFollows = false)
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(isLoadingFollows = false, followsError = e.message)
        }
    }
  }

  fun logout() {
    viewModelScope.launch {
      repository.logout()
      _uiState.value = _uiState.value.copy(
        isLoggedIn = false,
        user = null,
        histories = emptyList(),
        follows = emptyList()
      )
    }
  }

  fun setAutoNext(value: Boolean) {
    viewModelScope.launch { repository.setAutoNext(value) }
  }

  fun setAutoSkip(value: Boolean) {
    viewModelScope.launch { repository.setAutoSkip(value) }
  }

  fun setVolumeGesture(value: Boolean) {
    viewModelScope.launch { repository.setVolumeGesture(value) }
  }

  fun setBrightnessGesture(value: Boolean) {
    viewModelScope.launch { repository.setBrightnessGesture(value) }
  }

  fun setMovieMode(value: Boolean) {
    viewModelScope.launch { repository.setMovieMode(value) }
  }

  fun setShowComments(value: Boolean) {
    viewModelScope.launch { repository.setShowComments(value) }
  }

  fun setInfiniteScroll(value: Boolean) {
    viewModelScope.launch { repository.setInfiniteScroll(value) }
  }
}
