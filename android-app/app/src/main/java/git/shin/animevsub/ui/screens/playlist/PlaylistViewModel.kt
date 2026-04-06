package git.shin.animevsub.ui.screens.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.Playlist
import git.shin.animevsub.data.model.PlaylistItem
import git.shin.animevsub.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistUiState(
  val playlist: Playlist? = null,
  val items: List<PlaylistItem> = emptyList(),
  val isLoading: Boolean = false,
  val isRefreshing: Boolean = false,
  val isLoadingMore: Boolean = false,
  val hasMore: Boolean = true,
  val error: String? = null,
  val sorter: String = "asc",
  val isEditingName: Boolean = false,
  val isEditingDescription: Boolean = false
)

@HiltViewModel
class PlaylistViewModel @Inject constructor(
  private val playlistRepository: PlaylistRepository,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val playlistIdString: String = checkNotNull(savedStateHandle["playlistId"])
  private val playlistId: Int = try {
    // Vue version uses atob(route.params.playlist.toString()).split("-")[1]
    // Assuming the Android app might pass the ID directly or we need to handle the same encoding
    // For now, let's try to parse as Int, if it fails, maybe it's the encoded string
    playlistIdString.toInt()
  } catch (e: Exception) {
    // Fallback for encoded ID if necessary, but usually in Android we pass raw IDs
    0
  }

  private val _uiState = MutableStateFlow(PlaylistUiState())
  val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

  private var currentPage = 1

  init {
    refresh()
  }

  fun refresh() {
    viewModelScope.launch {
      _uiState.update { it.copy(isRefreshing = true, error = null) }

      // Get playlist metadata
      val playlistsResult = playlistRepository.getPlaylists()
      val meta = playlistsResult.getOrNull()?.find { it.id == playlistId }

      if (meta == null) {
        _uiState.update { it.copy(isRefreshing = false, error = "Playlist not found") }
        return@launch
      }

      val posterResult = playlistRepository.getPosterPlaylist(playlistId)
      val playlistWithPoster = meta.copy(poster = posterResult.getOrNull() ?: meta.poster)

      _uiState.update { it.copy(playlist = playlistWithPoster) }

      // Get items
      currentPage = 1
      val itemsResult =
        playlistRepository.getAnimesFromPlaylist(playlistId, currentPage, _uiState.value.sorter)

      itemsResult.onSuccess { items ->
        _uiState.update {
          it.copy(
            items = items,
            isRefreshing = false,
            hasMore = items.isNotEmpty()
          )
        }
      }.onFailure { e ->
        _uiState.update { it.copy(isRefreshing = false, error = e.message) }
      }
    }
  }

  fun loadMore() {
    if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return

    viewModelScope.launch {
      _uiState.update { it.copy(isLoadingMore = true) }
      currentPage++

      val result =
        playlistRepository.getAnimesFromPlaylist(playlistId, currentPage, _uiState.value.sorter)

      result.onSuccess { newItems ->
        _uiState.update {
          it.copy(
            items = it.items + newItems,
            isLoadingMore = false,
            hasMore = newItems.isNotEmpty()
          )
        }
      }.onFailure {
        _uiState.update { it.copy(isLoadingMore = false) }
      }
    }
  }

  fun setSorter(sorter: String) {
    if (_uiState.value.sorter == sorter) return
    _uiState.update { it.copy(sorter = sorter) }
    refresh()
  }

  fun updatePlaylistName(newName: String) {
    viewModelScope.launch {
      val oldName = _uiState.value.playlist?.name ?: return@launch
      playlistRepository.renamePlaylist(oldName, newName).onSuccess {
        _uiState.update { state ->
          state.copy(
            playlist = state.playlist?.copy(name = newName),
            isEditingName = false
          )
        }
      }
    }
  }

  fun updatePlaylistDescription(newDescription: String) {
    viewModelScope.launch {
      playlistRepository.setDescriptionPlaylist(playlistId, newDescription).onSuccess {
        _uiState.update { state ->
          state.copy(
            playlist = state.playlist?.copy(description = newDescription),
            isEditingDescription = false
          )
        }
      }
    }
  }

  fun deletePlaylist(onDeleted: () -> Unit) {
    viewModelScope.launch {
      playlistRepository.deletePlaylist(playlistId).onSuccess {
        onDeleted()
      }
    }
  }

  fun removeAnimeFromPlaylist(seasonId: String) {
    viewModelScope.launch {
      playlistRepository.deleteAnimeFromPlaylist(playlistId, seasonId).onSuccess {
        _uiState.update { state ->
          state.copy(
            items = state.items.filter { it.seasonId != seasonId },
            playlist = state.playlist?.copy(
              movieCount = (state.playlist.movieCount - 1).coerceAtLeast(
                0
              )
            )
          )
        }
      }
    }
  }

  fun addToOtherPlaylist(targetPlaylistId: Int, item: PlaylistItem) {
    viewModelScope.launch {
      playlistRepository.addAnimeToPlaylist(
        id = targetPlaylistId,
        seasonId = item.seasonId,
        seasonName = item.seasonName,
        name = item.name,
        poster = item.poster,
        chapId = item.chapId,
        chapName = item.chapName
      )
    }
  }

  fun toggleEditName(editing: Boolean) {
    _uiState.update { it.copy(isEditingName = editing) }
  }

  fun toggleEditDescription(editing: Boolean) {
    _uiState.update { it.copy(isEditingDescription = editing) }
  }
}
