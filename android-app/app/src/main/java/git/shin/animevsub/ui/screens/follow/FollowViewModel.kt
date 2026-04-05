package git.shin.animevsub.ui.screens.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FollowUiState(
  val isLoading: Boolean = true,
  val items: List<AnimeCard> = emptyList(),
  val error: String? = null,
  val currentPage: Int = 1,
  val totalPages: Int = 1,
  val isLoadingMore: Boolean = false
)

@HiltViewModel
class FollowViewModel @Inject constructor(
  private val repository: AnimeRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(FollowUiState())
  val uiState: StateFlow<FollowUiState> = _uiState.asStateFlow()

  init {
    loadPage(1)
  }

  fun loadPage(page: Int) {
    viewModelScope.launch {
      if (page == 1) {
        _uiState.update { it.copy(isLoading = true, error = null) }
      } else {
        _uiState.update { it.copy(isLoadingMore = true) }
      }

      repository.getFollows(page)
        .onSuccess { categoryPage ->
          _uiState.update {
            it.copy(
              isLoading = false,
              isLoadingMore = false,
              items = if (page == 1) categoryPage.items else it.items + categoryPage.items,
              currentPage = page,
              totalPages = categoryPage.totalPages,
              error = null
            )
          }
        }
        .onFailure { e ->
          _uiState.update {
            it.copy(
              isLoading = false,
              isLoadingMore = false,
              error = e.message
            )
          }
        }
    }
  }

  fun loadMore() {
    if (!_uiState.value.isLoadingMore && _uiState.value.currentPage < _uiState.value.totalPages) {
      loadPage(_uiState.value.currentPage + 1)
    }
  }

  fun retry() {
    loadPage(1)
  }
}
