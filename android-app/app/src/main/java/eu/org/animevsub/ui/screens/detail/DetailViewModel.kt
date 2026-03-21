package eu.org.animevsub.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.org.animevsub.data.model.AnimeDetail
import eu.org.animevsub.data.model.ChapterData
import eu.org.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val isLoading: Boolean = true,
    val detail: AnimeDetail? = null,
    val chapterData: ChapterData? = null,
    val error: String? = null,
    val seasonId: String = ""
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: AnimeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        val seasonId = savedStateHandle.get<String>("seasonId") ?: ""
        _uiState.value = _uiState.value.copy(seasonId = seasonId)
        loadDetail(seasonId)
    }

    fun loadDetail(seasonId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getAnimeDetail(seasonId)
                .onSuccess { detail ->
                    _uiState.value = _uiState.value.copy(
                        detail = detail,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }

            // Also load chapters
            repository.getChapters(seasonId)
                .onSuccess { chapterData ->
                    _uiState.value = _uiState.value.copy(chapterData = chapterData)
                }
        }
    }

    fun retry() {
        loadDetail(_uiState.value.seasonId)
    }
}
