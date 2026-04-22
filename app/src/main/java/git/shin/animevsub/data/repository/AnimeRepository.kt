package git.shin.animevsub.data.repository

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.AnimeDetail
import git.shin.animevsub.data.model.CategoryPage
import git.shin.animevsub.data.model.ChapterData
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.data.model.CommentResponse
import git.shin.animevsub.data.model.EditCommentResponse
import git.shin.animevsub.data.model.FilterGroup
import git.shin.animevsub.data.model.FilterOption
import git.shin.animevsub.data.model.HomeData
import git.shin.animevsub.data.model.InOutroEpisode
import git.shin.animevsub.data.model.NotificationData
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.PostCommentResponse
import git.shin.animevsub.data.model.ReplyResponse
import git.shin.animevsub.data.model.ScheduleDay
import git.shin.animevsub.data.model.SearchSuggestion
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.data.model.Trigger
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.model.VoteResponse
import git.shin.animevsub.data.model.VoteType
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
  private val api: AnimeApi,
  private val historyRepository: HistoryRepository,
  private val notificationDbRepository: NotificationDatabaseRepository,
  private val prefs: PreferencesManager,
  private val storage: ApiStorage,
  private val json: Json,
  private val analytics: FirebaseAnalytics
) {
  private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  private val _notifications = MutableStateFlow<NotificationData?>(null)
  val notifications = _notifications.asStateFlow()

  private val _authEvent = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
  val authEvent = _authEvent.asSharedFlow()

  init {
    repositoryScope.launch {
      // Load cached notifications
      storage.getString("cached_notifications").first()?.let { cached ->
        try {
          _notifications.value = json.decodeFromString<NotificationData>(cached)
        } catch (e: Exception) {
          print(e)
        }
      }

      // Sync notifications and refresh user when logged in
      isLoggedIn.collect { loggedIn ->
        if (loggedIn) {
          launch { getNotifications() }
          launch { refreshUser() }
          if (prefs.autoSyncNotify.first()) {
            launch { startSyncNotifications() }
          }
        } else {
          _notifications.value = null
          storage.set("cached_notifications", null)
          notificationDbRepository.clear()
        }
      }
    }
  }

  suspend fun refreshUser(): Result<User> = runCatching {
    try {
      val user = api.refreshUser()
      historyRepository.upsertUser(user)
      user
    } catch (e: Exception) {
      if (e !is java.io.IOException && e.message?.contains("Không thể lấy thông tin") == true) {
        _authEvent.tryEmit(AuthEvent.PromptForAction)
      }
      throw e
    }
  }

  sealed class AuthEvent {
    object PromptForAction : AuthEvent()
  }

  // Home
  suspend fun getHomePage(): Result<HomeData> = runCatching { api.getHomePage() }

  // Detail
  suspend fun getAnimeDetail(animeId: String): Result<AnimeDetail> = runCatching {
    val result = api.getAnimeDetail(animeId)
    analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
      param(FirebaseAnalytics.Param.ITEM_ID, animeId)
      param(FirebaseAnalytics.Param.ITEM_CATEGORY, "anime")
    }
    result
  }

  // Chapters
  suspend fun getChapters(animeId: String): Result<ChapterData> = runCatching {
    val result = api.getChapters(animeId)
    analytics.logEvent("view_chapters") {
      param(FirebaseAnalytics.Param.ITEM_ID, animeId)
    }
    result
  }

  // Rankings
  suspend fun getRankingTypes(): Result<List<FilterOption>> = runCatching {
    api.getRankingTypes()
  }

  suspend fun getCommentSortOptions(): Result<List<FilterOption>> = runCatching {
    api.getCommentSortOptions()
  }

  suspend fun getRankings(type: String): Result<List<AnimeCard>> = runCatching {
    val result = api.getRankings(type)
    analytics.logEvent("view_rankings") {
      param("ranking_type", type)
    }
    result
  }

  // Schedule
  suspend fun getSchedule(): Result<List<ScheduleDay>> = runCatching {
    val result = api.getSchedule()
    analytics.logEvent("view_schedule") {
      param("action", "refresh")
    }
    result
  }

  // Search
  suspend fun preSearch(keyword: String): Result<List<SearchSuggestion>> = runCatching {
    api.preSearch(keyword)
  }

  suspend fun search(keyword: String, page: Int = 1): Result<CategoryPage> = runCatching {
    analytics.logEvent(FirebaseAnalytics.Event.SEARCH) {
      param(FirebaseAnalytics.Param.SEARCH_TERM, keyword)
    }
    api.search(keyword, page)
  }

  // Category
  suspend fun getCategory(
    filters: List<SelectedFilter>,
    page: Int = 1
  ): Result<CategoryPage> = runCatching {
    val result = api.getCategory(filters, page)
    analytics.logEvent("view_category") {
      param("filters", filters.joinToString { it.name })
      param("page", page.toLong())
    }
    result
  }

  suspend fun getFilters(filters: List<SelectedFilter>): Result<List<FilterGroup>> = runCatching {
    api.getFilters(filters)
  }

  // Player
  suspend fun getServers(chapter: ChapterInfo): Result<List<ServerInfo>> = runCatching {
    api.getServers(chapter)
  }

  suspend fun getPlayerLink(chapter: ChapterInfo, server: ServerInfo): Result<PlayerData> = runCatching {
    val result = api.getPlayerLink(server)
    analytics.logEvent("play_video") {
      param(FirebaseAnalytics.Param.ITEM_ID, chapter.id)
      param(FirebaseAnalytics.Param.ITEM_NAME, chapter.name)
      param("server_name", server.name)
    }
    result
  }

  // Skip Range
  suspend fun getSkipRange(
    animeId: String,
    detail: AnimeDetail,
    chapter: ChapterInfo
  ): Result<InOutroEpisode?> = runCatching {
    api.getEpisodeSkip(animeId, detail, chapter)
  }

  // Auth
  val user: Flow<User?> = api.getUser()
    .distinctUntilChanged()
    .onEach { user ->
      if (user != null) {
        repositoryScope.launch {
          historyRepository.upsertUser(user)
        }
      }
    }
  val isLoggedIn: Flow<Boolean> = user.map { it != null }

  val loginUrl: String = AnimeApi.LOGIN_URL

//  suspend fun login(email: String, password: String): Result<User> = runCatching {
//    val user = api.login(email, password)
//    historyRepository.upsertUser(user)
//    analytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
//      param(FirebaseAnalytics.Param.METHOD, "email")
//    }
//    analytics.setUserId(user.username)
//    user
//  }

  suspend fun logout() {
    api.logout()
  }

  // Settings
  val autoNext = prefs.autoNext
  val autoSkip = prefs.autoSkip
  val volumeGesture = prefs.volumeGesture
  val brightnessGesture = prefs.brightnessGesture
  val autoSyncNotify = prefs.autoSyncNotify
  val notifyInterval = prefs.notifyInterval
  val dbNotifyInterval = prefs.dbNotifyInterval
  val enableBackgroundSync = prefs.enableBackgroundSync
  val enableNotifications = prefs.enableNotifications
  val appLanguage = prefs.appLanguage
  val developerMode = prefs.developerMode
  val hideDonationPopup = prefs.hideDonationPopup
  val screenTransition = prefs.screenTransition

  val breakReminderEnabled = prefs.breakReminderEnabled
  val breakReminderInterval = prefs.breakReminderInterval
  val bedtimeReminderEnabled = prefs.bedtimeReminderEnabled
  val bedtimeReminderStartTime = prefs.bedtimeReminderStartTime
  val bedtimeReminderEndTime = prefs.bedtimeReminderEndTime
  val bedtimeReminderWaitFinish = prefs.bedtimeReminderWaitFinish

  suspend fun setAutoNext(value: Boolean) = prefs.setAutoNext(value)
  suspend fun setAutoSkip(value: Boolean) = prefs.setAutoSkip(value)
  suspend fun setVolumeGesture(value: Boolean) = prefs.setVolumeGesture(value)
  suspend fun setBrightnessGesture(value: Boolean) = prefs.setBrightnessGesture(value)

  suspend fun setAutoSyncNotify(value: Boolean) = prefs.setAutoSyncNotify(value)
  suspend fun setNotifyInterval(value: Int) = prefs.setNotifyInterval(value)
  suspend fun setDbNotifyInterval(value: Int) = prefs.setDbNotifyInterval(value)
  suspend fun setEnableBackgroundSync(value: Boolean) = prefs.setEnableBackgroundSync(value)
  suspend fun setEnableNotifications(value: Boolean) = prefs.setEnableNotifications(value)

  suspend fun setBreakReminderEnabled(value: Boolean) = prefs.setBreakReminderEnabled(value)
  suspend fun setBreakReminderInterval(value: Int) = prefs.setBreakReminderInterval(value)
  suspend fun setBedtimeReminderEnabled(value: Boolean) = prefs.setBedtimeReminderEnabled(value)
  suspend fun setBedtimeReminderStartTime(minutes: Long) = prefs.setBedtimeReminderStartTime(minutes)
  suspend fun setBedtimeReminderEndTime(minutes: Long) = prefs.setBedtimeReminderEndTime(minutes)
  suspend fun setBedtimeReminderWaitFinish(value: Boolean) = prefs.setBedtimeReminderWaitFinish(value)
  suspend fun setAppLanguage(value: String) = prefs.setAppLanguage(value)
  suspend fun setDeveloperMode(value: Boolean) = prefs.setDeveloperMode(value)
  suspend fun setHideDonationPopup(value: Boolean) = prefs.setHideDonationPopup(value)
  suspend fun setScreenTransition(value: String) = prefs.setScreenTransition(value)

  // Search History
  val searchHistory = prefs.searchHistory
  suspend fun addSearchHistory(query: String) = prefs.addSearchHistory(query)
  suspend fun clearSearchHistory() = prefs.clearSearchHistory()

  // Notifications
  suspend fun getNotifications(): Result<NotificationData> = runCatching {
    val data = api.getNotifications()
    _notifications.value = data
    storage.set("cached_notifications", json.encodeToString(data))
    notificationDbRepository.getCountNotify()

    if (prefs.autoSyncNotify.first() && !notificationDbRepository.isSyncing.value) {
      repositoryScope.launch {
        startSyncNotifications()
      }
    }

    data
  }

  suspend fun startSyncNotifications(): Result<Unit> = notificationDbRepository.startSync(
    getApiNotifications = { getNotifications() },
    onTrigger = { onTrigger(it) }
  )

  suspend fun onTrigger(trigger: Trigger): Result<Unit> = runCatching {
    api.onTrigger(trigger)
  }

  // Follows
  suspend fun getFollows(page: Int = 1): Result<CategoryPage> = runCatching {
    api.getFollows(page)
  }

  suspend fun checkFollow(animeId: String): Result<Boolean> = runCatching {
    api.checkFollow(animeId)
  }

  suspend fun toggleFollow(animeId: String, follow: Boolean): Result<Unit> = runCatching {
    api.toggleFollow(animeId, follow)
    analytics.logEvent(if (follow) "follow_anime" else "unfollow_anime") {
      param(FirebaseAnalytics.Param.ITEM_ID, animeId)
    }
  }

  // Comments
  suspend fun getComments(
    filmId: String,
    anime: AnimeDetail,
    sort: FilterOption?,
    offset: Int = 0
  ): Result<CommentResponse> = runCatching {
    api.getComments(filmId, anime, sort, offset)
  }

  suspend fun getReplies(
    commentId: String,
    sort: FilterOption?,
    offset: Int = 0
  ): Result<ReplyResponse> = runCatching {
    api.getReplies(commentId, sort, offset)
  }

  suspend fun postComment(
    filmId: String,
    content: String,
    isSpoiler: Boolean = false,
    episodeId: String? = null,
    parentId: String = "0",
    threadKey: String? = null
  ): Result<PostCommentResponse> = runCatching {
    api.postComment(filmId, content, isSpoiler, episodeId, parentId, threadKey)
  }

  suspend fun voteComment(commentId: String, voteType: VoteType): Result<VoteResponse> = runCatching {
    api.voteComment(commentId, voteType)
  }

  suspend fun editComment(
    commentId: String,
    content: String,
    isSpoiler: Boolean
  ): Result<EditCommentResponse> = runCatching {
    api.editComment(commentId, content, isSpoiler)
  }

  // History
  suspend fun getHistory(page: Int) = historyRepository.getHistory(page)
  suspend fun getWatchProgress(seasonId: String) = historyRepository.getWatchProgress(seasonId)
  suspend fun getSingleProgress(seasonId: String, chapId: String) = historyRepository.getSingleProgress(seasonId, chapId)

  suspend fun getLastChapOfSeason(seasonId: String) = historyRepository.getLastChapOfSeason(seasonId)

  suspend fun setSingleProgress(
    name: String,
    poster: String,
    seasonId: String,
    seasonName: String,
    chapId: String,
    chapName: String,
    cur: Double,
    dur: Double
  ) = historyRepository.setSingleProgress(
    name, poster, seasonId, seasonName, chapId, chapName, cur, dur
  )
}
