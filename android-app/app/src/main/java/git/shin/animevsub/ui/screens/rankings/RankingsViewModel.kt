package git.shin.animevsub.ui.screens.rankings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.RankingItem
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RankingsUiState(
    val isLoading: Boolean = true,
    val items: List<RankingItem> = emptyList(),
    val error: String? = null,
    val selectedType: String = "day"
)

@HiltViewModel
class RankingsViewModel @Inject constructor(
    private val repository: AnimeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingsUiState())
    val uiState: StateFlow<RankingsUiState> = _uiState.asStateFlow()

    init {
        val type = savedStateHandle.get<String>("type") ?: "day"
        _uiState.value = _uiState.value.copy(selectedType = type)
        loadRankings(type)
    }

    fun loadRankings(type: String) {
        _uiState.value = _uiState.value.copy(selectedType = type, isLoading = true, error = null)
        viewModelScope.launch {
            repository.getRankings(type)
                .onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = items,
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

    fun retry() {
        loadRankings(_uiState.value.selectedType)
    }
}
