package git.shin.animevsub.ui.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val isLoadingMore: Boolean = false
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
        _uiState.value = _uiState.value.copy(
            typeNormal = typeNormal,
            value = value,
            title = value.replace("-", " ").replaceFirstChar { it.uppercase() }
        )
        loadPage(1)
    }

    fun loadPage(page: Int) {
        viewModelScope.launch {
            if (page == 1) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            } else {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            }

            repository.getCategoryPage(_uiState.value.typeNormal, _uiState.value.value, page)
                .onSuccess { categoryPage ->
                    val newItems = if (page == 1) categoryPage.items
                    else _uiState.value.items + categoryPage.items
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        items = newItems,
                        currentPage = page,
                        totalPages = categoryPage.totalPages,
                        title = categoryPage.title.ifEmpty { _uiState.value.title },
                        error = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = e.message
                    )
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
