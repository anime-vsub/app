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
import git.shin.animevsub.data.model.FilterGroup
import git.shin.animevsub.data.model.FilterOption
import git.shin.animevsub.data.model.HomeData
import git.shin.animevsub.data.model.InOutroEpisode
import git.shin.animevsub.data.model.NotificationData
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.ScheduleDay
import git.shin.animevsub.data.model.SearchSuggestion
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.remote.AnimeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
  private val prefs: PreferencesManager,
  private val storage: ApiStorage,
  private val json: Json,
  private val analytics: FirebaseAnalytics
) {
  private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  private val _notifications = MutableStateFlow<NotificationData?>(null)

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

      // Sync notifications when logged in
      isLoggedIn.collect { loggedIn ->
        if (loggedIn) {
          getNotifications()
        } else {
          _notifications.value = null
          storage.set("cached_notifications", null)
        }
      }
    }
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

  suspend fun getPlayerLink(chapter: ChapterInfo, server: ServerInfo): Result<PlayerData> =
    runCatching {
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
    detail: AnimeDetail,
    chapter: ChapterInfo
  ): Result<InOutroEpisode?> = runCatching {
    api.getEpisodeSkip(detail, chapter)
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

  suspend fun login(email: String, password: String): Result<User> = runCatching {
    val user = api.login(email, password)
    historyRepository.upsertUser(user)
    analytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
      param(FirebaseAnalytics.Param.METHOD, "email")
    }
    analytics.setUserId(user.username)
    user
  }

  suspend fun logout() {
    api.logout()
  }

  // Settings
  val autoNext = prefs.autoNext
  val autoSkip = prefs.autoSkip
  val volumeGesture = prefs.volumeGesture
  val brightnessGesture = prefs.brightnessGesture

  suspend fun setAutoNext(value: Boolean) = prefs.setAutoNext(value)
  suspend fun setAutoSkip(value: Boolean) = prefs.setAutoSkip(value)
  suspend fun setVolumeGesture(value: Boolean) = prefs.setVolumeGesture(value)
  suspend fun setBrightnessGesture(value: Boolean) = prefs.setBrightnessGesture(value)

  // Search History
  val searchHistory = prefs.searchHistory
  suspend fun addSearchHistory(query: String) = prefs.addSearchHistory(query)
  suspend fun clearSearchHistory() = prefs.clearSearchHistory()

  // Notifications
  suspend fun getNotifications(): Result<NotificationData> = runCatching {
    val data = api.getNotifications()
    _notifications.value = data
    storage.set("cached_notifications", json.encodeToString(data))
    data
  }

  suspend fun onTrigger(trigger: git.shin.animevsub.data.model.Trigger): Result<Unit> =
    runCatching {
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
    val result = api.toggleFollow(animeId, follow)
    analytics.logEvent(if (follow) "follow_anime" else "unfollow_anime") {
      param(FirebaseAnalytics.Param.ITEM_ID, animeId)
    }
    result
  }

  // History
  suspend fun getHistory(page: Int) = historyRepository.getHistory(page)
  suspend fun getWatchProgress(seasonId: String) = historyRepository.getWatchProgress(seasonId)
  suspend fun getSingleProgress(seasonId: String, chapId: String) =
    historyRepository.getSingleProgress(seasonId, chapId)

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
//
//  suspend fun getLastChapOfSeason(seasonId: String) =
//    historyRepository.getLastChapOfSeason(seasonId)
}
