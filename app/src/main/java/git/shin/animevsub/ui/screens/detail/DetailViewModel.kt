package git.shin.animevsub.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.AnimeDetail
import git.shin.animevsub.data.model.ChapterData
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.data.model.Comment
import git.shin.animevsub.data.model.DisplaySeason
import git.shin.animevsub.data.model.DoubleRange
import git.shin.animevsub.data.model.FilterOption
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.Season
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.data.model.Trigger
import git.shin.animevsub.data.model.VoteType
import git.shin.animevsub.data.model.WatchProgress
import git.shin.animevsub.data.repository.AnimeRepository
import git.shin.animevsub.data.repository.PlaylistRepository
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
  val isLoading: Boolean = true,
  val isRefreshing: Boolean = false,
  val detail: AnimeDetail? = null,
  val chapterData: ChapterData? = null,
  val isChaptersLoading: Boolean = false,
  val chaptersError: String? = null,
  val error: String? = null,
  val animeId: String = "",
  val initialChapterId: String? = null,
  // Player state
  val isPlayerLoading: Boolean = false,
  val playerData: PlayerData? = null,
  val playerError: String? = null,
  val currentChapter: ChapterInfo? = null,
  val servers: List<ServerInfo> = emptyList(),
  val currentServer: ServerInfo? = null,
  val autoNext: Boolean = true,
  val autoSkip: Boolean = false,
  val introRange: DoubleRange? = null,
  val outroRange: DoubleRange? = null,
  val episodeNameFromApi: String? = null,
  val lastProgress: Long = 0,
  val chapterProgress: Map<String, WatchProgress> = emptyMap(),

  /**
   * The actual season ID from the API (Real ID).
   * Used for: Fetching data from API, managing Cache (chapterCache).
   * This value is always the original ID from the server (e.g., "movie-123").
   */
  val currentSeasonId: String = "",

  val displaySeasons: List<DisplaySeason> = emptyList(),

  /**
   * The currently selected ID for display in the UI (UI ID).
   * Used for: Highlighting the selected tab, determining the displayed chapter range (max 30),
   * and acting as a key for maintaining separate scroll states for each group of chapters.
   * Can be a virtual ID (e.g., "movie-123_30").
   */
  val activeDisplaySeasonId: String = "",

  val chapterCounts: Map<String, Int> = emptyMap(),
  val isFollowed: Boolean = false,

  // User state
  val currentUser: git.shin.animevsub.data.model.User? = null,

  // Comments state
  val comments: List<Comment> = emptyList(),
  val totalComments: Int = 0,
  val isCommentsLoading: Boolean = false,
  val hasMoreComments: Boolean = false,
  val commentsOffset: Int = 0,
  val commentSort: FilterOption? = null,
  val commentSortOptions: List<FilterOption> = emptyList(),
  val replies: Map<String, List<Comment>> = emptyMap(),
  val repliesOffset: Map<String, Int> = emptyMap(),
  val repliesHasMore: Map<String, Boolean> = emptyMap(),
  val isPostingComment: Boolean = false,
  val commentError: String? = null,
  /** 0: Full, 1: Uploadcomment Only, 2: Disabled */
  val syncMode: Int = 0
) {
  val currentChapIndex: Int
    get() {
      if (currentChapter == null || chapterData == null) return -1
      return chapterData.chaps.indexOfFirst { it.id == currentChapter.id }
    }
}

@HiltViewModel
class DetailViewModel @Inject constructor(
  private val repository: AnimeRepository,
  private val playlistRepository: PlaylistRepository,
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
        currentSeasonId = animeId,
        activeDisplaySeasonId = animeId
      )
    }
    loadDetail(animeId, chapterId)

    viewModelScope.launch {
      repository.user.collect { user ->
        _uiState.update { it.copy(currentUser = user) }
      }
    }

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

    loadCommentSortOptions()
  }

  private fun loadCommentSortOptions() {
    viewModelScope.launch {
      repository.getCommentSortOptions().onSuccess { options ->
        _uiState.update {
          it.copy(
            commentSortOptions = options,
            commentSort = options.firstOrNull()
          )
        }
      }
    }
  }

  /**
   * Switches between Seasons (or Virtual Seasons).
   * Updates activeDisplaySeasonId for UI selection and chapter range.
   */
  fun setActiveDisplaySeason(id: String) {
    val displaySeason = _uiState.value.displaySeasons.find { it.id == id } ?: return
    _uiState.update {
      it.copy(
        activeDisplaySeasonId = id,
        currentSeasonId = displaySeason.realId // Always keep real ID for loading data
      )
    }
    loadChaptersOnly(displaySeason.realId)
  }

  private fun updateChapterCount(seasonId: String, count: Int) {
    _uiState.update {
      it.copy(chapterCounts = it.chapterCounts + (seasonId to count))
    }
    updateDisplaySeasons()
  }

  /**
   * Split seasons with > 30 chapters into "Virtual Seasons" for better UX.
   */
  private fun updateDisplaySeasons() {
    val detail = _uiState.value.detail ?: return
    val counts = _uiState.value.chapterCounts

    val newList = mutableListOf<DisplaySeason>()
    detail.season.forEach { realSeason: Season ->
      val count = counts[realSeason.id]
      if (count != null && count > 30) {
        // Split into 30-chapter segments
        for (i in 0 until count step 30) {
          val end = (i + 30).coerceAtMost(count)
          val id = "${realSeason.id}_$i" // Create virtual ID based on start offset
          newList.add(
            DisplaySeason(
              id = id,
              realId = realSeason.id,
              name = "${realSeason.name} (${i + 1}-$end)",
              range = i until end,
              isVirtual = true
            )
          )
        }
      } else {
        // Keep as is if few chapters
        newList.add(
          DisplaySeason(
            id = realSeason.id,
            realId = realSeason.id,
            name = realSeason.name,
            range = null,
            isVirtual = false
          )
        )
      }
    }

    _uiState.update { state ->
      var activeId = state.activeDisplaySeasonId
      // If current active ID is no longer valid, automatically select the best match
      if (activeId.isEmpty() || newList.none { it.id == activeId }) {
        activeId = newList.find { it.realId == state.currentSeasonId }?.id
          ?: newList.firstOrNull()?.id ?: ""
      }
      state.copy(displaySeasons = newList, activeDisplaySeasonId = activeId)
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
      updateChapterCount(seasonId, cachedChapters.chaps.size)
      loadAllChapterProgress(seasonId)
    } ?: run {
      viewModelScope.launch {
        repository.getChapters(seasonId)
          .onSuccess { chapterData ->
            chapterCache[seasonId] = chapterData
            _uiState.update { it.copy(chapterData = chapterData, isChaptersLoading = false) }
            updateChapterCount(seasonId, chapterData.chaps.size)
            loadAllChapterProgress(seasonId)
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
      updateDisplaySeasons()
      checkFollow()
      loadComments(animeDetail = cachedDetail)
    } ?: run {
      viewModelScope.launch {
        repository.getAnimeDetail(animeId)
          .onSuccess { detail ->
            detailCache[animeId] = detail
            _uiState.update { it.copy(detail = detail, isLoading = false) }
            updateDisplaySeasons()
            checkFollow()
            loadComments(animeDetail = detail)
          }
          .onFailure { e ->
            _uiState.update { it.copy(isLoading = false, error = e.message) }
          }
      }
    }

    chapterCache[animeId]?.let { cachedChapters ->
      _uiState.update { it.copy(chapterData = cachedChapters, isChaptersLoading = false) }
      updateChapterCount(animeId, cachedChapters.chaps.size)
      if (!isSwitchingSeason) {
        viewModelScope.launch {
          handleInitialChapter(cachedChapters, targetChapterId)
        }
      }
    } ?: run {
      viewModelScope.launch {
        repository.getChapters(animeId)
          .onSuccess { chapterData ->
            chapterCache[animeId] = chapterData
            _uiState.update { it.copy(chapterData = chapterData, isChaptersLoading = false) }
            updateChapterCount(animeId, chapterData.chaps.size)
            if (!isSwitchingSeason) {
              handleInitialChapter(chapterData, targetChapterId)
            }
          }
          .onFailure { e ->
            _uiState.update { it.copy(isChaptersLoading = false, chaptersError = e.message) }
          }
      }
    }
  }

  private suspend fun handleInitialChapter(chapterData: ChapterData, targetChapterId: String?) {
    if (chapterData.chaps.isNotEmpty()) {
      val foundChapter = if (targetChapterId != null) {
        chapterData.chaps.find { it.id == targetChapterId }
      } else {
        null
      }

      val chapterToPlay = if (foundChapter != null) {
        foundChapter
      } else {
        // ID doesn't exist or no target ID provided, check watch history for "Continue Watching"
        val lastChapId = repository.getLastChapOfSeason(_uiState.value.animeId).getOrNull()
        if (lastChapId != null) {
          chapterData.chaps.find { it.id == lastChapId } ?: chapterData.chaps.first()
        } else {
          chapterData.chaps.first()
        }
      }

      playChapter(chapterToPlay, _uiState.value.animeId)
    }
  }

  fun playChapter(chapter: ChapterInfo, seasonId: String) {
    val data = chapterCache[seasonId] ?: _uiState.value.chapterData ?: return
    val index = data.chaps.indexOfFirst { it.id == chapter.id }
    val isNewSeason = seasonId != _uiState.value.animeId

    // Determine appropriate display ID (could be virtual) based on chapter index
    val displayId = if (data.chaps.size > 30 && index >= 0) {
      "${seasonId}_${(index / 30) * 30}"
    } else {
      seasonId
    }

    _uiState.update {
      it.copy(
        currentChapter = chapter,
        animeId = seasonId,
        isPlayerLoading = true,
        playerError = null,
        activeDisplaySeasonId = displayId,
        currentSeasonId = seasonId,
        introRange = null,
        outroRange = null,
        episodeNameFromApi = null,
        servers = emptyList(),
        currentServer = null,
        playerData = null,
        lastProgress = 0L
      )
    }
    isInitialPlayback = true

    if (isNewSeason) {
      loadDetail(seasonId, chapter.id, isSwitchingSeason = true)
    }

    loadServers(chapter)
    loadSkipRange(chapter)
    loadHistory(chapter, seasonId)
    loadAllChapterProgress(seasonId)
  }

  private fun loadAllChapterProgress(seasonId: String) {
    viewModelScope.launch {
      repository.getWatchProgress(seasonId)
        .onSuccess { progressList ->
          val progressMap = progressList.associateBy { it.chapId ?: "" }
          _uiState.update {
            it.copy(chapterProgress = it.chapterProgress + progressMap)
          }
        }
    }
  }

  private fun loadHistory(chapter: ChapterInfo, seasonId: String) {
    viewModelScope.launch {
      repository.getSingleProgress(seasonId, chapter.id)
        .onSuccess { progress ->
          if (progress != null) {
            _uiState.update { it.copy(lastProgress = (progress.cur * 1000).toLong()) }
          }
        }
    }
  }

  private var lastUpdateJob: kotlinx.coroutines.Job? = null
  private var isInitialPlayback = true

  fun updateHistory(currentPosMs: Long, durationMs: Long) {
    if (_uiState.value.currentUser == null) return
    val currentPos = currentPosMs / 1000.0
    val duration = durationMs / 1000.0
    val state = _uiState.value
    val chapter = state.currentChapter ?: return

    // Update local progress map for UI consistency (immediate)
    _uiState.update {
      val newMap = it.chapterProgress.toMutableMap()
      newMap[chapter.id] = WatchProgress(cur = currentPos, dur = duration, chapId = chapter.id)
      it.copy(chapterProgress = newMap)
    }

    // Throttle Supabase RPC calls: Only save every 10 seconds
    if (lastUpdateJob?.isActive == true) return
    lastUpdateJob = viewModelScope.launch {
      if (isInitialPlayback) {
        delay(8000) // Wait 8 seconds before first sync after restore
        isInitialPlayback = false
      }

      // Re-fetch latest data from state to get the most recent position
      val latestState = _uiState.value
      val detail = latestState.detail ?: return@launch
      val currentChapter = latestState.currentChapter ?: return@launch
      val latestProgress = latestState.chapterProgress[currentChapter.id] ?: return@launch

      repository.setSingleProgress(
        name = detail.name,
        poster = detail.poster ?: detail.image ?: "",
        seasonId = latestState.currentSeasonId,
        seasonName = detail.season.find { it.id == latestState.currentSeasonId }?.name ?: "",
        chapId = currentChapter.id,
        chapName = currentChapter.name,
        cur = latestProgress.cur,
        dur = latestProgress.dur
      )
      delay(10000) // 10 second throttle
    }
  }

  private fun loadSkipRange(chapter: ChapterInfo) {
    val detail = _uiState.value.detail ?: return
    viewModelScope.launch {
      repository.getSkipRange(_uiState.value.animeId, detail, chapter)
        .onSuccess { result ->
          _uiState.update {
            it.copy(
              introRange = result?.intro,
              outroRange = result?.outro,
              episodeNameFromApi = result?.episodeName
            )
          }
        }
    }
  }

  private fun loadServers(chapter: ChapterInfo) {
    viewModelScope.launch {
      repository.getServers(chapter)
        .onSuccess { servers ->
          _uiState.update { it.copy(servers = servers) }
          if (servers.isNotEmpty()) {
            val defaultServer = servers.first()
            _uiState.update { it.copy(currentServer = defaultServer) }
            loadPlayer(chapter, defaultServer)
          } else {
            _uiState.update {
              it.copy(
                isPlayerLoading = false,
                playerError = "No servers found"
              )
            }
          }
        }
        .onFailure { e ->
          _uiState.update { it.copy(isPlayerLoading = false, playerError = e.message) }
        }
    }
  }

  fun selectServer(server: ServerInfo) {
    val chapter = _uiState.value.currentChapter ?: return
    _uiState.update {
      it.copy(
        currentServer = server,
        isPlayerLoading = true,
        playerError = null,
        playerData = null
      )
    }
    loadPlayer(chapter, server)
  }

  private fun loadPlayer(chapter: ChapterInfo, server: ServerInfo) {
    viewModelScope.launch {
      repository.getPlayerLink(chapter, server)
        .onSuccess { data ->
          _uiState.update {
            it.copy(
              isPlayerLoading = false,
              playerData = data,
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
    if (state.currentChapter != null) {
      _uiState.update { it.copy(playerData = null, playerError = null, isPlayerLoading = true) }
      if (state.currentServer != null) {
        loadPlayer(state.currentChapter, state.currentServer)
      } else {
        loadServers(state.currentChapter)
      }
      loadSkipRange(state.currentChapter)
    }
  }

  suspend fun addToPlaylist(playlistId: Int): Result<Unit> {
    if (!checkLogin()) return Result.failure(Exception("Login required"))
    val detail = _uiState.value.detail ?: return Result.failure(Exception("Detail not found"))
    val chapter =
      _uiState.value.currentChapter ?: return Result.failure(Exception("Chapter not found"))
    return playlistRepository.addAnimeToPlaylist(
      id = playlistId,
      seasonId = _uiState.value.currentSeasonId,
      seasonName = detail.season.find { it.id == _uiState.value.currentSeasonId }?.name
        ?: detail.name,
      name = detail.name,
      poster = detail.poster ?: detail.image ?: "",
      chapId = chapter.id,
      chapName = chapter.name
    ).map { }
  }

  suspend fun removeFromPlaylist(playlistId: Int): Result<Unit> {
    if (!checkLogin()) return Result.failure(Exception("Login required"))
    return playlistRepository.deleteAnimeFromPlaylist(playlistId, _uiState.value.currentSeasonId)
      .map { }
  }
//
//  fun createPlaylistAndAddAnime(name: String) {
//    val detail = _uiState.value.detail ?: return
//    val chapter = _uiState.value.currentChapter ?: return
//    viewModelScope.launch {
//      playlistRepository.createPlaylist(name, false).onSuccess { playlist ->
//        playlistRepository.addAnimeToPlaylist(
//          id = playlist.id,
//          seasonId = _uiState.value.currentSeasonId,
//          seasonName = detail.season.find { it.id == _uiState.value.currentSeasonId }?.name
//            ?: detail.name,
//          name = detail.name,
//          poster = detail.poster ?: detail.image ?: "",
//          chapId = chapter.id,
//          chapName = chapter.name
//        )
//      }
//    }
//  }

  fun refresh() {
    viewModelScope.launch {
      _uiState.update { it.copy(isRefreshing = true) }
      val animeId = _uiState.value.animeId
      val currentSeasonId = _uiState.value.currentSeasonId

      // Reload detail
      repository.getAnimeDetail(animeId)
        .onSuccess { detail ->
          detailCache[animeId] = detail
          _uiState.update { it.copy(detail = detail) }
          updateDisplaySeasons()
          checkFollow()
          loadComments()
        }

      // Reload chapters for current season
      repository.getChapters(currentSeasonId)
        .onSuccess { chapterData ->
          chapterCache[currentSeasonId] = chapterData
          _uiState.update { it.copy(chapterData = chapterData) }
          updateChapterCount(currentSeasonId, chapterData.chaps.size)
          loadAllChapterProgress(currentSeasonId)
        }

      _uiState.update { it.copy(isRefreshing = false) }
    }
  }

  fun retry() {
    loadDetail(_uiState.value.animeId, _uiState.value.initialChapterId)
  }

  fun checkFollow() {
    val animeId = _uiState.value.animeId
    viewModelScope.launch {
      repository.checkFollow(animeId).onSuccess { followed ->
        _uiState.update { it.copy(isFollowed = followed) }
      }
    }
  }

  private val _uiEffect = MutableSharedFlow<DetailUiEffect>()
  val uiEffect: SharedFlow<DetailUiEffect> = _uiEffect.asSharedFlow()

  sealed class DetailUiEffect {
    data class ShowSnackbar(val message: String) : DetailUiEffect()
    data object RequireLogin : DetailUiEffect()
    data object OpenPlaylistSheet : DetailUiEffect()
  }

  private fun checkLogin(): Boolean {
    if (_uiState.value.currentUser == null) {
      viewModelScope.launch {
        _uiEffect.emit(DetailUiEffect.RequireLogin)
      }
      return false
    }
    return true
  }

  fun onSaveClick() {
    if (checkLogin()) {
      viewModelScope.launch {
        _uiEffect.emit(DetailUiEffect.OpenPlaylistSheet)
      }
    }
  }

  fun toggleFollow() {
    if (!checkLogin()) return
    val animeId = _uiState.value.animeId
    val isFollowed = _uiState.value.isFollowed
    viewModelScope.launch {
      repository.toggleFollow(animeId, !isFollowed).onSuccess {
        _uiState.update { it.copy(isFollowed = !isFollowed) }
        _uiEffect.emit(DetailUiEffect.ShowSnackbar(if (!isFollowed) "FOLLOW_SUCCESS" else "UNFOLLOW_SUCCESS"))
      }.onFailure {
        _uiEffect.emit(DetailUiEffect.ShowSnackbar("FOLLOW_ERROR"))
      }
    }
  }

  // ======== Comment Logic ========

  fun loadComments(append: Boolean = false, animeDetail: AnimeDetail? = null) {
    val filmId = _uiState.value.animeId
    val anime = animeDetail ?: _uiState.value.detail ?: return
    val sort = _uiState.value.commentSort
    val offset = if (append) _uiState.value.commentsOffset else 0

    if (_uiState.value.isCommentsLoading && !append) return

    _uiState.update { it.copy(isCommentsLoading = true, commentError = null) }

    viewModelScope.launch {
      repository.getComments(filmId, anime, sort, offset)
        .onSuccess { response ->
          _uiState.update { state ->
            val newList = if (append) state.comments + response.comments else response.comments
            state.copy(
              comments = newList,
              totalComments = response.total,
              commentsOffset = response.offset,
              hasMoreComments = response.hasMore,
              isCommentsLoading = false
            )
          }
        }
        .onFailure { e ->
          _uiState.update { it.copy(isCommentsLoading = false, commentError = e.message) }
        }
    }
  }

  fun loadMoreComments() {
    if (_uiState.value.hasMoreComments) {
      loadComments(append = true)
    }
  }

  fun loadReplies(commentId: String, append: Boolean = false) {
    val sort = _uiState.value.commentSort
    val offset = if (append) _uiState.value.repliesOffset[commentId] ?: 0 else 0

    viewModelScope.launch {
      repository.getReplies(commentId, sort, offset)
        .onSuccess { response ->
          _uiState.update { state ->
            val currentReplies = state.replies[commentId] ?: emptyList()
            val newList = if (append) currentReplies + response.replies else response.replies
            state.copy(
              replies = state.replies + (commentId to newList),
              repliesOffset = state.repliesOffset + (commentId to response.offset),
              repliesHasMore = state.repliesHasMore + (commentId to response.hasMore)
            )
          }
        }
    }
  }

  fun postComment(content: String, isSpoiler: Boolean = false, parentId: String = "0") {
    if (!checkLogin()) return
    if (content.isBlank()) return
    val filmId = _uiState.value.animeId
    val episodeId = _uiState.value.currentChapter?.id
    val threadKey = _uiState.value.currentChapter?.name ?: _uiState.value.animeId

    _uiState.update { it.copy(isPostingComment = true) }

    viewModelScope.launch {
      repository.postComment(filmId, content, isSpoiler, episodeId, parentId, threadKey)
        .onSuccess { response ->
          if (response.success && response.comment != null) {
            _uiState.update { state ->
              if (parentId == "0") {
                state.copy(
                  comments = listOf(response.comment) + state.comments,
                  totalComments = response.total ?: (state.totalComments + 1),
                  isPostingComment = false
                )
              } else {
                val currentReplies = state.replies[parentId] ?: emptyList()
                state.copy(
                  replies = state.replies + (parentId to (currentReplies + response.comment)),
                  isPostingComment = false
                )
              }
            }
          } else {
            _uiState.update { it.copy(isPostingComment = false, commentError = response.error) }
          }
        }
        .onFailure { e ->
          _uiState.update { it.copy(isPostingComment = false, commentError = e.message) }
        }
    }
  }

  fun voteComment(commentId: String, voteType: VoteType) {
    if (!checkLogin()) return
    viewModelScope.launch {
      repository.voteComment(commentId, voteType)
        .onSuccess { response ->
          if (response.success) {
            _uiState.update { state ->
              val updateComment: (Comment) -> Comment = { c ->
                if (c.id == commentId) {
                  c.copy(
                    votesUp = response.votesUp,
                    votesDown = response.votesDown,
                    userVote = voteType
                  )
                } else {
                  c
                }
              }
              state.copy(
                comments = state.comments.map(updateComment),
                replies = state.replies.mapValues { it.value.map(updateComment) }
              )
            }
          }
        }
    }
  }

  fun editComment(commentId: String, content: String, isSpoiler: Boolean = false) {
    if (!checkLogin()) return
    viewModelScope.launch {
      repository.editComment(commentId, content, isSpoiler)
        .onSuccess { response ->
          if (response.success) {
            _uiState.update { state ->
              val updateComment: (Comment) -> Comment = { c ->
                if (c.id == commentId) {
                  c.copy(
                    content = content,
                    isSpoiler = isSpoiler,
                    editedAt = System.currentTimeMillis() / 1000
                  )
                } else {
                  c
                }
              }
              state.copy(
                comments = state.comments.map(updateComment),
                replies = state.replies.mapValues { it.value.map(updateComment) }
              )
            }
          }
        }
    }
  }

  fun onCommentTrigger(trigger: Trigger, parentId: String = "0") {
    if (!checkLogin()) return
    viewModelScope.launch {
      repository.onTrigger(trigger)
        .onSuccess {
          when (trigger.id) {
            "delete_comment" -> {
              val commentId = trigger.extra["comment_id"] ?: return@onSuccess
              _uiState.update { state ->
                if (parentId == "0") {
                  state.copy(
                    comments = state.comments.filter { it.id != commentId },
                    totalComments = state.totalComments - 1
                  )
                } else {
                  val currentReplies = state.replies[parentId] ?: emptyList()
                  state.copy(
                    replies = state.replies + (parentId to currentReplies.filter { it.id != commentId })
                  )
                }
              }
            }

            "report_comment" -> {
              _uiEffect.emit(DetailUiEffect.ShowSnackbar("REPORT_SUCCESS"))
            }
          }
        }
        .onFailure { e ->
          _uiEffect.emit(DetailUiEffect.ShowSnackbar(e.message ?: "ACTION_ERROR"))
        }
    }
  }

  fun updateCommentSort(sort: FilterOption) {
    if (_uiState.value.commentSort?.id == sort.id) return
    _uiState.update { it.copy(commentSort = sort, comments = emptyList(), commentsOffset = 0) }
    loadComments()
  }

  fun toggleSyncMode() {
    _uiState.update {
      it.copy(syncMode = (it.syncMode + 1) % 3)
    }
  }

  fun setSyncMode(mode: Int) {
    _uiState.update { it.copy(syncMode = mode) }
  }
}
