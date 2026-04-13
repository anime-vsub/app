package git.shin.animevsub.ui.screens.rankings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.FilterOption
import git.shin.animevsub.data.repository.AnimeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RankingsUiState(
  val isLoading: Boolean = true,
  val items: List<AnimeCard> = emptyList(),
  val error: String? = null,
  val errorRes: Int? = null,
  val selectedType: String = "",
  val rankingTypes: List<FilterOption> = emptyList()
)

@HiltViewModel
class RankingsViewModel @Inject constructor(
  private val repository: AnimeRepository,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val _uiState = MutableStateFlow(RankingsUiState())
  val uiState: StateFlow<RankingsUiState> = _uiState.asStateFlow()

  init {
    val initialType = savedStateHandle.get<String?>("type")

    if (!initialType.isNullOrEmpty()) {
      _uiState.update { it.copy(selectedType = initialType) }
      loadRankings(initialType)
      loadRankingTypes(shouldPickDefault = false)
    } else {
      loadRankingTypes(shouldPickDefault = true)
    }
  }

  private fun loadRankingTypes(shouldPickDefault: Boolean) {
    viewModelScope.launch {
      repository.getRankingTypes()
        .onSuccess { types ->
          _uiState.update { it.copy(rankingTypes = types) }

          if (shouldPickDefault && types.isNotEmpty()) {
            val firstType = types.first().id
            _uiState.update { it.copy(selectedType = firstType) }
            loadRankings(firstType)
          } else if (types.isEmpty()) {
            _uiState.update {
              it.copy(
                isLoading = false,
                errorRes = R.string.error_no_ranking_types
              )
            }
          }
        }
        .onFailure { e ->
          if (_uiState.value.selectedType.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
          }
        }
    }
  }

  fun loadRankings(type: String) {
    _uiState.update {
      it.copy(
        selectedType = type,
        isLoading = true,
        error = null,
        errorRes = null
      )
    }
    viewModelScope.launch {
      repository.getRankings(type)
        .onSuccess { items ->
          _uiState.update {
            it.copy(
              isLoading = false,
              items = items,
              error = null,
              errorRes = null
            )
          }
        }
        .onFailure { e ->
          _uiState.update {
            it.copy(
              isLoading = false,
              error = e.message,
              errorRes = null
            )
          }
        }
    }
  }

  fun retry() {
    val currentType = _uiState.value.selectedType
    if (currentType.isEmpty()) {
      loadRankingTypes(shouldPickDefault = true)
    } else {
      loadRankings(currentType)
    }
  }
}
