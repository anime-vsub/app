package eu.org.animevsub.ui.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.org.animevsub.data.model.ChapterData
import eu.org.animevsub.data.model.ChapterInfo
import eu.org.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val isLoading: Boolean = true,
    val videoUrl: String? = null,
    val error: String? = null,
    val seasonId: String = "",
    val chapId: String = "",
    val play: String = "",
    val hash: String = "",
    val chapterData: ChapterData? = null,
    val currentChapIndex: Int = 0,
    val autoNext: Boolean = true,
    val autoSkip: Boolean = false
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: AnimeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        val seasonId = savedStateHandle.get<String>("seasonId") ?: ""
        val chapId = savedStateHandle.get<String>("chapId") ?: ""
        val play = savedStateHandle.get<String>("play") ?: ""
        val hash = savedStateHandle.get<String>("hash") ?: ""

        _uiState.value = _uiState.value.copy(
            seasonId = seasonId,
            chapId = chapId,
            play = play,
            hash = hash
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                autoNext = repository.autoNext.first(),
                autoSkip = repository.autoSkip.first()
            )
        }

        loadPlayer(chapId, play, hash)
        loadChapters(seasonId)
    }

    private fun loadPlayer(id: String, play: String, hash: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getPlayerLink(id, play, hash)
                .onSuccess { url ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        videoUrl = url,
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

    private fun loadChapters(seasonId: String) {
        viewModelScope.launch {
            repository.getChapters(seasonId)
                .onSuccess { data ->
                    val currentIndex = data.chaps.indexOfFirst { it.id == _uiState.value.chapId }
                    _uiState.value = _uiState.value.copy(
                        chapterData = data,
                        currentChapIndex = if (currentIndex >= 0) currentIndex else 0
                    )
                }
        }
    }

    fun playChapter(chap: ChapterInfo) {
        val data = _uiState.value.chapterData ?: return
        val index = data.chaps.indexOf(chap)
        _uiState.value = _uiState.value.copy(
            chapId = chap.id,
            play = chap.play,
            hash = chap.hash,
            currentChapIndex = if (index >= 0) index else _uiState.value.currentChapIndex
        )
        loadPlayer(chap.id, chap.play, chap.hash)
    }

    fun playNext(): Boolean {
        val data = _uiState.value.chapterData ?: return false
        val nextIndex = _uiState.value.currentChapIndex + 1
        if (nextIndex < data.chaps.size) {
            playChapter(data.chaps[nextIndex])
            return true
        }
        return false
    }

    fun retry() {
        loadPlayer(_uiState.value.chapId, _uiState.value.play, _uiState.value.hash)
    }
}
