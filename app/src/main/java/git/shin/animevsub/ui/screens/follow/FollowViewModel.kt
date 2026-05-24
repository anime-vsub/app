package git.shin.animevsub.ui.screens.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.FilterGroup
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FollowUiState(
  val isLoading: Boolean = true,
  val isRefreshing: Boolean = false,
  val items: List<AnimeCard> = emptyList(),
  val error: String? = null,
  val currentPage: Int = 1,
  val totalPages: Int = 1,
  val isLoadingMore: Boolean = false,
  val filterGroups: List<FilterGroup> = emptyList(),
  val selectedFilters: List<SelectedFilter> = emptyList(),
  val isFilterLoading: Boolean = false
)

@HiltViewModel
class FollowViewModel @Inject constructor(
  private val repository: AnimeRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(FollowUiState())
  val uiState: StateFlow<FollowUiState> = _uiState.asStateFlow()

  init {
    loadPage(1)
    loadFilters()
  }

  private fun loadFilters() {
    viewModelScope.launch {
      _uiState.update { it.copy(isFilterLoading = true) }
      repository.getFollowFilters(_uiState.value.selectedFilters)
        .onSuccess { groups ->
          val currentFilters = _uiState.value.selectedFilters
          val newDefaults = groups.filter { it.default != null }
            .filter { group -> currentFilters.none { it.groupId == group.id } }
            .mapNotNull { group ->
              val option = group.options.find { it.id == group.default }
              option?.let { SelectedFilter(group.id, it.id, it.name) }
            }
          val updatedFilters = currentFilters + newDefaults
          _uiState.update {
            it.copy(filterGroups = groups, selectedFilters = updatedFilters, isFilterLoading = false)
          }
          if (newDefaults.isNotEmpty()) loadPage(1)
        }
        .onFailure {
          _uiState.update { it.copy(isFilterLoading = false) }
        }
    }
  }

  fun loadPage(page: Int, isRefreshing: Boolean = false) {
    viewModelScope.launch {
      if (page == 1) {
        if (isRefreshing) {
          _uiState.update { it.copy(isRefreshing = true, error = null) }
        } else {
          _uiState.update { it.copy(isLoading = true, error = null) }
        }
      } else {
        _uiState.update { it.copy(isLoadingMore = true) }
      }

      repository.getFollows(_uiState.value.selectedFilters, page)
        .onSuccess { categoryPage ->
          _uiState.update {
            it.copy(
              isLoading = false,
              isRefreshing = false,
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
              isRefreshing = false,
              isLoadingMore = false,
              error = e.message
            )
          }
        }
    }
  }

  fun refresh() {
    loadPage(1, isRefreshing = true)
  }

  fun updateFilter(filter: SelectedFilter) {
    val current = _uiState.value.selectedFilters.toMutableList()
    val group = _uiState.value.filterGroups.find { it.id == filter.groupId }
    val isMultiple = group?.isMultiple == true

    if (!isMultiple) {
      current.removeAll { it.groupId == filter.groupId }
    }

    val existingIndex = current.indexOfFirst { it.id == filter.id && it.groupId == filter.groupId }
    if (existingIndex != -1) {
      current.removeAt(existingIndex)
    } else {
      current.add(filter)
    }

    _uiState.update { it.copy(selectedFilters = current) }
    loadPage(1)
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
