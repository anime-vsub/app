package git.shin.animevsub.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

data class HistoryUiState(
  val isLoading: Boolean = true,
  val isRefreshing: Boolean = false,
  val groupedItems: Map<String, List<HistoryItem>> = emptyMap(),
  val error: String? = null,
  val currentPage: Int = 1,
  val hasMore: Boolean = true,
  val isLoadingMore: Boolean = false
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
  private val repository: HistoryRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(HistoryUiState())
  val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

  private val allItems = mutableListOf<HistoryItem>()

  init {
    loadHistory(1)
  }

  fun loadHistory(page: Int, isRefreshing: Boolean = false) {
    viewModelScope.launch {
      if (page == 1) {
        if (isRefreshing) {
          _uiState.update { it.copy(isRefreshing = true, error = null) }
        } else {
          _uiState.update { it.copy(isLoading = true, error = null) }
        }
        allItems.clear()
      } else {
        _uiState.update { it.copy(isLoadingMore = true) }
      }

      repository.getHistory(page)
        .onSuccess { items ->
          allItems.addAll(items)
          val grouped = groupItems(allItems)
          _uiState.update {
            it.copy(
              isLoading = false,
              isRefreshing = false,
              isLoadingMore = false,
              groupedItems = grouped,
              currentPage = page,
              hasMore = items.isNotEmpty(),
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
    loadHistory(1, isRefreshing = true)
  }

  private fun groupItems(items: List<HistoryItem>): Map<String, List<HistoryItem>> {
    return items.groupBy { item ->
      val date = try {
        ZonedDateTime.parse(item.createdAt).toLocalDate()
      } catch (e: Exception) {
        print(e)
        LocalDate.now()
      }
      date.toString() // Store as ISO string for key, will format in UI
    }
  }

  fun loadMore() {
    if (!_uiState.value.isLoadingMore && _uiState.value.hasMore) {
      loadHistory(_uiState.value.currentPage + 1)
    }
  }

  fun retry() {
    loadHistory(1)
  }
}
