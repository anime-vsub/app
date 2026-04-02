package git.shin.animevsub.ui.screens.category

import androidx.lifecycle.SavedStateHandle
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

data class CategoryUiState(
  val isLoading: Boolean = true,
  val items: List<AnimeCard> = emptyList(),
  val error: String? = null,
  val typeNormal: String = "",
  val value: String = "",
  val title: String = "",
  val currentPage: Int = 1,
  val totalPages: Int = 1,
  val isLoadingMore: Boolean = false,
  val filterGroups: List<FilterGroup> = emptyList(),
  val selectedFilters: List<SelectedFilter> = emptyList(),
  val isFilterLoading: Boolean = false
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
  private val repository: AnimeRepository,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val _uiState = MutableStateFlow(CategoryUiState())
  val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

  init {
    val typeNormal = savedStateHandle.get<String>("typeNormal") ?: ""
    val value = savedStateHandle.get<String>("value") ?: ""
    _uiState.update {
      it.copy(
        typeNormal = typeNormal,
        value = value,
        title = value.replace("-", " ").replaceFirstChar { char -> char.uppercase() }
      )
    }
    loadPage(1)
    if (typeNormal == "danh-sach") {
      loadFilters()
    }
  }

  private fun loadFilters() {
    viewModelScope.launch {
      _uiState.update { it.copy(isFilterLoading = true) }
      repository.getFilters("/danh-sach/${_uiState.value.value}/")
        .onSuccess { groups ->
          _uiState.update { it.copy(filterGroups = groups, isFilterLoading = false) }
        }
        .onFailure {
          _uiState.update { it.copy(isFilterLoading = false) }
        }
    }
  }

  fun loadPage(page: Int) {
    viewModelScope.launch {
      if (page == 1) {
        _uiState.update { it.copy(isLoading = true, error = null) }
      } else {
        _uiState.update { it.copy(isLoadingMore = true) }
      }

      repository.getCategory(
        type = _uiState.value.typeNormal,
        value = _uiState.value.value,
        filters = _uiState.value.selectedFilters,
        page = page
      ).onSuccess { categoryPage ->
        val newItems = if (page == 1) categoryPage.items
        else _uiState.value.items + categoryPage.items
        _uiState.update {
          it.copy(
            isLoading = false,
            isLoadingMore = false,
            items = newItems,
            currentPage = page,
            totalPages = categoryPage.totalPages,
            title = categoryPage.title.ifEmpty { it.title },
            error = null
          )
        }
      }.onFailure { e ->
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
