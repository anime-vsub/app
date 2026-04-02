package git.shin.animevsub.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.SearchSuggestion
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
  val query: String = "",
  val suggestions: List<SearchSuggestion> = emptyList(),
  val searchResults: List<AnimeCard> = emptyList(),
  val isLoading: Boolean = false,
  val isSearching: Boolean = false,
  val error: String? = null,
  val searchHistory: List<String> = emptyList(),
  val currentPage: Int = 1,
  val totalPages: Int = 1,
  val isRefreshing: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
  private val repository: AnimeRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(SearchUiState())
  val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

  private var preSearchJob: Job? = null
  private var searchJob: Job? = null

  init {
    viewModelScope.launch {
      repository.searchHistory.collect { history ->
        _uiState.value = _uiState.value.copy(searchHistory = history.reversed())
      }
    }
  }

  fun onQueryChange(query: String) {
    _uiState.value = _uiState.value.copy(query = query, isSearching = false)
    preSearchJob?.cancel()

    if (query.isBlank()) {
      _uiState.value = _uiState.value.copy(suggestions = emptyList(), isLoading = false)
      return
    }

    if (query.length < 2) return

    preSearchJob = viewModelScope.launch {
      delay(300) // debounce
      _uiState.value = _uiState.value.copy(isLoading = true)
      repository.preSearch(query)
        .onSuccess { suggestions ->
          _uiState.value = _uiState.value.copy(
            suggestions = suggestions,
            isLoading = false,
            error = null
          )
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = e.message
          )
        }
    }
  }

  fun onSearch(query: String) {
    if (query.isBlank()) return
    searchJob?.cancel()

    _uiState.value = _uiState.value.copy(
      query = query,
      isSearching = true,
      isLoading = true,
      suggestions = emptyList(),
      searchResults = emptyList(),
      currentPage = 1
    )

    viewModelScope.launch {
      repository.addSearchHistory(query)
    }

    searchJob = viewModelScope.launch {
      repository.search(query, 1)
        .onSuccess { page ->
          _uiState.value = _uiState.value.copy(
            searchResults = page.items,
            totalPages = page.totalPages,
            isLoading = false,
            error = null
          )
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = e.message
          )
        }
    }
  }

  fun loadMore() {
    val state = _uiState.value
    if (state.isLoading || state.currentPage >= state.totalPages || !state.isSearching) return

    _uiState.value = _uiState.value.copy(isLoading = true)
    val nextPage = state.currentPage + 1

    viewModelScope.launch {
      repository.search(state.query, nextPage)
        .onSuccess { page ->
          _uiState.value = _uiState.value.copy(
            searchResults = state.searchResults + page.items,
            currentPage = nextPage,
            isLoading = false,
            error = null
          )
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = e.message
          )
        }
    }
  }

  fun clearHistory() {
    viewModelScope.launch {
      repository.clearSearchHistory()
    }
  }
}
