package git.shin.animevsub.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
  val isLoggedIn: Boolean = false,
  val user: User? = null,
  val autoNext: Boolean = true,
  val autoSkip: Boolean = false,
  val server: String = "DU",
  val movieMode: Boolean = false,
  val showComments: Boolean = true,
  val infiniteScroll: Boolean = true
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
        repository.server.collect { v ->
          _uiState.value = _uiState.value.copy(server = v)
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

  fun logout() {
    viewModelScope.launch {
      repository.logout()
    }
  }

  fun setAutoNext(value: Boolean) {
    viewModelScope.launch { repository.setAutoNext(value) }
  }

  fun setAutoSkip(value: Boolean) {
    viewModelScope.launch { repository.setAutoSkip(value) }
  }

  fun setServer(value: String) {
    viewModelScope.launch { repository.setServer(value) }
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
