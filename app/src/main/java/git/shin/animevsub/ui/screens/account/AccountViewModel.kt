package git.shin.animevsub.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.data.model.Playlist
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.repository.AnimeRepository
import git.shin.animevsub.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
  val isLoggedIn: Boolean = false,
  val isAuthReady: Boolean = false,
  val user: User? = null,
  val histories: List<HistoryItem> = emptyList(),
  val follows: List<AnimeCard> = emptyList(),
  val downloads: List<git.shin.animevsub.data.local.download.DownloadEntity> = emptyList(),
  val playlists: List<Playlist> = emptyList(),
  val playlistCheckedStates: Map<Int, Boolean> = emptyMap(),
  val isLoadingHistory: Boolean = false,
  val isLoadingFollows: Boolean = false,
  val isLoadingDownloads: Boolean = false,
  val isLoadingPlaylists: Boolean = false,
  val isCheckingUpdate: Boolean = false,
  val historyError: String? = null,
  val followsError: String? = null,
  val downloadsError: String? = null,
  val playlistsError: String? = null,
  val isRefreshing: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
  private val repository: AnimeRepository,
  private val playlistRepository: PlaylistRepository,
  private val downloadRepository: git.shin.animevsub.data.repository.DownloadRepository,
  private val updateManager: git.shin.animevsub.utils.UpdateManager
) : ViewModel() {

  private val _uiState = MutableStateFlow(AccountUiState())
  val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

  val uiEvent = repository.authEvent

  init {
    viewModelScope.launch {
      downloadRepository.allDownloads.collect { downloads ->
        _uiState.update { it.copy(downloads = downloads.take(10)) }
      }
    }
    viewModelScope.launch {
      repository.isLoggedIn.collect { isLoggedIn ->
        _uiState.update { it.copy(isLoggedIn = isLoggedIn, isAuthReady = true) }
        if (isLoggedIn) {
          refreshHistory()
          refreshFollows()
          refreshPlaylists()
        } else {
          _uiState.update {
            it.copy(
              user = null,
              histories = emptyList(),
              follows = emptyList(),
              playlists = emptyList()
            )
          }
        }
      }
    }
    viewModelScope.launch {
      repository.user.collect { user ->
        _uiState.update { it.copy(user = user) }
      }
    }
  }

  fun refresh() {
    viewModelScope.launch {
      _uiState.update { it.copy(isRefreshing = true) }
      if (_uiState.value.isLoggedIn) {
        val historyJob = launch {
          repository.getHistory(1)
            .onSuccess { list ->
              _uiState.update { it.copy(histories = list.take(10), historyError = null) }
            }
            .onFailure { e ->
              _uiState.update { it.copy(historyError = e.message) }
            }
        }
        val followsJob = launch {
          repository.getFollows(1)
            .onSuccess { page ->
              _uiState.update { it.copy(follows = page.items.take(10), followsError = null) }
            }
            .onFailure { e ->
              _uiState.update { it.copy(followsError = e.message) }
            }
        }
        val playlistJob = launch {
          playlistRepository.getPlaylists()
            .onSuccess { list ->
              _uiState.update { it.copy(playlists = list, playlistsError = null) }
            }
            .onFailure { e ->
              _uiState.update { it.copy(playlistsError = e.message) }
            }
        }
        historyJob.join()
        followsJob.join()
        playlistJob.join()
      }
      _uiState.update { it.copy(isRefreshing = false) }
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
          _uiState.value =
            _uiState.value.copy(follows = page.items.take(10), isLoadingFollows = false)
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(isLoadingFollows = false, followsError = e.message)
        }
    }
  }

  fun refreshDownloads() {
    // Downloads are already synced via Flow in init, but we can provide a manual trigger if needed
    // or just leave it for consistency if we add more complex logic later.
  }

  fun refreshPlaylists() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoadingPlaylists = true, playlistsError = null)
      playlistRepository.getPlaylists()
        .onSuccess { list ->
          _uiState.value = _uiState.value.copy(playlists = list, isLoadingPlaylists = false)
        }
        .onFailure { e ->
          _uiState.value =
            _uiState.value.copy(isLoadingPlaylists = false, playlistsError = e.message)
        }
    }
  }

  fun performLogout() {
    viewModelScope.launch {
      repository.logout()
      _uiState.update {
        it.copy(
          isLoggedIn = false,
          user = null,
          histories = emptyList(),
          follows = emptyList(),
          playlists = emptyList()
        )
      }
    }
  }

  fun createPlaylist(name: String, isPublic: Boolean = false) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoadingPlaylists = true, playlistsError = null)
      playlistRepository.createPlaylist(name, isPublic)
        .onSuccess {
          refreshPlaylists()
        }
        .onFailure { e ->
          _uiState.value =
            _uiState.value.copy(isLoadingPlaylists = false, playlistsError = e.message)
        }
    }
  }

  fun checkAnimeInPlaylists(animeId: String) {
    viewModelScope.launch {
      val playlistIds = _uiState.value.playlists.map { it.id }
      if (playlistIds.isNotEmpty()) {
        playlistRepository.hasAnimeOfPlaylists(playlistIds, animeId)
          .onSuccess { results ->
            val newState = playlistIds.zip(results).toMap()
            _uiState.value = _uiState.value.copy(playlistCheckedStates = newState)
          }
      }
    }
  }

  fun togglePlaylistChecked(playlistId: Int, checked: Boolean) {
    val currentStates = _uiState.value.playlistCheckedStates.toMutableMap()
    currentStates[playlistId] = checked
    _uiState.value = _uiState.value.copy(playlistCheckedStates = currentStates)
  }

  fun checkForUpdate(
    onUpdateAvailable: (git.shin.animevsub.data.model.UpdateInfo) -> Unit,
    onNoUpdate: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isCheckingUpdate = true)
      updateManager.checkForUpdate()
        .onSuccess { updateInfo ->
          _uiState.value = _uiState.value.copy(isCheckingUpdate = false)
          if (updateInfo.isNewer) {
            onUpdateAvailable(updateInfo)
          } else {
            onNoUpdate()
          }
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(isCheckingUpdate = false)
          onError(e.message ?: "Unknown error")
        }
    }
  }

  fun downloadUpdate(info: git.shin.animevsub.data.model.UpdateInfo) {
    updateManager.downloadAndInstall(info.downloadUrl, "AnimeVsub_v${info.version}.apk")
  }
}
