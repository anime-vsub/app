package git.shin.animevsub.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.data.model.Playlist
import git.shin.animevsub.data.repository.AnimeRepository
import git.shin.animevsub.data.repository.PlaylistRepository
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
  val playlists: List<Playlist> = emptyList(),
  val isLoadingHistory: Boolean = false,
  val isLoadingFollows: Boolean = false,
  val isLoadingPlaylists: Boolean = false,
  val historyError: String? = null,
  val followsError: String? = null,
  val playlistsError: String? = null,
)

@HiltViewModel
class AccountViewModel @Inject constructor(
  private val repository: AnimeRepository,
  private val playlistRepository: PlaylistRepository
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
            refreshPlaylists()
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

  fun refreshPlaylists() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoadingPlaylists = true, playlistsError = null)
      playlistRepository.getPlaylists()
        .onSuccess { list ->
          _uiState.value = _uiState.value.copy(playlists = list, isLoadingPlaylists = false)
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(isLoadingPlaylists = false, playlistsError = e.message)
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
        follows = emptyList(),
        playlists = emptyList()
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
}
