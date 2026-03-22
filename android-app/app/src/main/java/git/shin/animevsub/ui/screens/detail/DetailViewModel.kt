package git.shin.animevsub.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.AnimeDetail
import git.shin.animevsub.data.model.ChapterData
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
  val isLoading: Boolean = true,
  val detail: AnimeDetail? = null,
  val chapterData: ChapterData? = null,
  val isChaptersLoading: Boolean = false,
  val chaptersError: String? = null,
  val error: String? = null,
  val animeId: String = "",
  val initialChapterId: String? = null,
  // Player state
  val isPlayerLoading: Boolean = false,
  val videoUrl: String? = null,
  val playerError: String? = null,
  val currentChapId: String = "",
  val currentPlay: String = "",
  val currentHash: String = "",
  val currentChapIndex: Int = -1,
  val autoNext: Boolean = true,
  val autoSkip: Boolean = false,
  val currentSeasonId: String = ""
)

@HiltViewModel
class DetailViewModel @Inject constructor(
  private val repository: AnimeRepository,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val _uiState = MutableStateFlow(DetailUiState())
  val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

  private val chapterCache = mutableMapOf<String, ChapterData>()
  private val detailCache = mutableMapOf<String, AnimeDetail>()

  init {
    val animeId = savedStateHandle.get<String>("animeId") ?: ""
    val chapterId = savedStateHandle.get<String>("chapterId")
    _uiState.update {
      it.copy(
        animeId = animeId,
        initialChapterId = chapterId,
        currentSeasonId = animeId
      )
    }
    loadDetail(animeId, chapterId)

    viewModelScope.launch {
      val autoNext = repository.autoNext.first()
      val autoSkip = repository.autoSkip.first()
      _uiState.update {
        it.copy(
          autoNext = autoNext,
          autoSkip = autoSkip
        )
      }
    }
  }

  fun loadChaptersOnly(seasonId: String) {
    _uiState.update {
      it.copy(
        currentSeasonId = seasonId,
        isChaptersLoading = !chapterCache.containsKey(seasonId),
        chaptersError = null
      )
    }

    chapterCache[seasonId]?.let { cachedChapters ->
      _uiState.update { it.copy(chapterData = cachedChapters, isChaptersLoading = false) }
      updateCurrentChapterIndex(cachedChapters)
    } ?: run {
      viewModelScope.launch {
        repository.getChapters(seasonId)
          .onSuccess { chapterData ->
            chapterCache[seasonId] = chapterData
            _uiState.update { it.copy(chapterData = chapterData, isChaptersLoading = false) }
            updateCurrentChapterIndex(chapterData)
          }
          .onFailure { e ->
            _uiState.update { it.copy(isChaptersLoading = false, chaptersError = e.message) }
          }
      }
    }
  }

  fun loadDetail(
    animeId: String,
    targetChapterId: String? = null,
    isSwitchingSeason: Boolean = false
  ) {
    _uiState.update {
      it.copy(
        animeId = animeId,
        currentSeasonId = animeId,
        isLoading = !detailCache.containsKey(animeId),
        isChaptersLoading = !chapterCache.containsKey(animeId),
        error = null,
        chaptersError = null
      )
    }

    detailCache[animeId]?.let { cachedDetail ->
      _uiState.update { it.copy(detail = cachedDetail, isLoading = false) }
    } ?: run {
      viewModelScope.launch {
        repository.getAnimeDetail(animeId)
          .onSuccess { detail ->
            detailCache[animeId] = detail
            _uiState.update { it.copy(detail = detail, isLoading = false) }
          }
          .onFailure { e ->
            _uiState.update { it.copy(isLoading = false, error = e.message) }
          }
      }
    }

    chapterCache[animeId]?.let { cachedChapters ->
      _uiState.update { it.copy(chapterData = cachedChapters, isChaptersLoading = false) }
      if (!isSwitchingSeason) {
        handleInitialChapter(cachedChapters, targetChapterId)
      } else {
        updateCurrentChapterIndex(cachedChapters)
      }
    } ?: run {
      viewModelScope.launch {
        repository.getChapters(animeId)
          .onSuccess { chapterData ->
            chapterCache[animeId] = chapterData
            _uiState.update { it.copy(chapterData = chapterData, isChaptersLoading = false) }
            if (!isSwitchingSeason) {
              handleInitialChapter(chapterData, targetChapterId)
            } else {
              updateCurrentChapterIndex(chapterData)
            }
          }
          .onFailure { e ->
            _uiState.update { it.copy(isChaptersLoading = false, chaptersError = e.message) }
          }
      }
    }
  }

  private fun handleInitialChapter(chapterData: ChapterData, targetChapterId: String?) {
    if (chapterData.chaps.isNotEmpty()) {
      val chapterToPlay = if (targetChapterId != null) {
        chapterData.chaps.find { it.id == targetChapterId } ?: chapterData.chaps.first()
      } else {
        chapterData.chaps.first()
      }
      playChapter(chapterToPlay, _uiState.value.animeId)
    }
  }

  private fun updateCurrentChapterIndex(chapterData: ChapterData) {
    val currentChapId = _uiState.value.currentChapId
    if (currentChapId.isNotEmpty() && _uiState.value.currentSeasonId == _uiState.value.animeId) {
      val newIndex = chapterData.chaps.indexOfFirst { it.id == currentChapId }
      _uiState.update { it.copy(currentChapIndex = newIndex) }
    } else {
      _uiState.update { it.copy(currentChapIndex = -1) }
    }
  }

  fun playChapter(chap: ChapterInfo, seasonId: String) {
    val data = chapterCache[seasonId] ?: _uiState.value.chapterData ?: return
    val index = data.chaps.indexOfFirst { it.id == chap.id }
    val isNewSeason = seasonId != _uiState.value.animeId

    _uiState.update {
      it.copy(
        currentChapId = chap.id,
        currentPlay = chap.play,
        currentHash = chap.hash,
        currentChapIndex = index,
        animeId = seasonId,
        isPlayerLoading = true,
        playerError = null
      )
    }

    if (isNewSeason) {
      loadDetail(seasonId, chap.id, isSwitchingSeason = true)
    }

    loadPlayer(chap.id, chap.play, chap.hash)
  }

  private fun loadPlayer(id: String, play: String, hash: String) {
    viewModelScope.launch {
      repository.getPlayerLink(id, play, hash)
        .onSuccess { url ->
          _uiState.update {
            it.copy(
              isPlayerLoading = false,
              videoUrl = url,
              playerError = null
            )
          }
        }
        .onFailure { e ->
          _uiState.update {
            it.copy(
              isPlayerLoading = false,
              playerError = e.message
            )
          }
        }
    }
  }

  fun playNext(): Boolean {
    val data = chapterCache[_uiState.value.animeId] ?: return false
    val nextIndex = _uiState.value.currentChapIndex + 1
    if (nextIndex < data.chaps.size) {
      playChapter(data.chaps[nextIndex], _uiState.value.animeId)
      return true
    }
    return false
  }

  fun retryPlayer() {
    val state = _uiState.value
    if (state.currentChapId.isNotEmpty()) {
      loadPlayer(state.currentChapId, state.currentPlay, state.currentHash)
    }
  }

  fun retry() {
    loadDetail(_uiState.value.animeId, _uiState.value.initialChapterId)
  }
}
