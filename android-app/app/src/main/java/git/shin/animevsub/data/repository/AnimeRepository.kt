package git.shin.animevsub.data.repository

import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.AnimeDetail
import git.shin.animevsub.data.model.CategoryPage
import git.shin.animevsub.data.model.ChapterData
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.data.model.DoubleRange
import git.shin.animevsub.data.model.FilterGroup
import git.shin.animevsub.data.model.FilterOption
import git.shin.animevsub.data.model.HomeData
import git.shin.animevsub.data.model.CategoryLink
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
  private val api: AnimeApi,
  private val historyRepository: HistoryRepository,
  private val prefs: PreferencesManager
) {
  private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  // Home
  suspend fun getHomePage(): Result<HomeData> = runCatching { api.getHomePage() }

  // Detail
  suspend fun getAnimeDetail(animeId: String): Result<AnimeDetail> = runCatching {
    api.getAnimeDetail(animeId)
  }

  // Chapters
  suspend fun getChapters(animeId: String): Result<ChapterData> = runCatching {
    api.getChapters(animeId)
  }

  // Rankings
  suspend fun getRankingTypes(): Result<List<FilterOption>> = runCatching {
    api.getRankingTypes()
  }

  suspend fun getRankings(type: String): Result<List<AnimeCard>> = runCatching {
    api.getRankings(type)
  }

  // Schedule
  suspend fun getSchedule(): Result<List<ScheduleDay>> = runCatching {
    api.getSchedule()
  }

  // Search
  suspend fun preSearch(keyword: String): Result<List<SearchSuggestion>> = runCatching {
    api.preSearch(keyword)
  }

  suspend fun search(keyword: String, page: Int = 1): Result<CategoryPage> = runCatching {
    api.search(keyword, page)
  }

  // Category
  suspend fun getCategory(
    filters: List<SelectedFilter>,
    page: Int = 1
  ): Result<CategoryPage> = runCatching {
    api.getCategory(filters, page)
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
      api.getPlayerLink(chapter, server)
    }

  // Skip Range
  suspend fun getSkipRange(chapter: ChapterInfo): Result<Pair<DoubleRange?, DoubleRange?>> =
    runCatching {
      api.getSkipRange(chapter)
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
  val movieMode = prefs.movieMode
  val showComments = prefs.showComments
  val infiniteScroll = prefs.infiniteScroll

  suspend fun setAutoNext(value: Boolean) = prefs.setAutoNext(value)
  suspend fun setAutoSkip(value: Boolean) = prefs.setAutoSkip(value)
  suspend fun setVolumeGesture(value: Boolean) = prefs.setVolumeGesture(value)
  suspend fun setBrightnessGesture(value: Boolean) = prefs.setBrightnessGesture(value)
  suspend fun setMovieMode(value: Boolean) = prefs.setMovieMode(value)
  suspend fun setShowComments(value: Boolean) = prefs.setShowComments(value)
  suspend fun setInfiniteScroll(value: Boolean) = prefs.setInfiniteScroll(value)

  // Search History
  val searchHistory = prefs.searchHistory
  suspend fun addSearchHistory(query: String) = prefs.addSearchHistory(query)
  suspend fun clearSearchHistory() = prefs.clearSearchHistory()

  // Notifications
  suspend fun getNotifications(): Result<NotificationData> = runCatching {
    api.getNotifications()
  }

  // Follows
  suspend fun getFollows(page: Int = 1): Result<CategoryPage> = runCatching {
    api.getFollows(page)
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

  suspend fun getLastChapOfSeason(seasonId: String) = historyRepository.getLastChapOfSeason(seasonId)
}
