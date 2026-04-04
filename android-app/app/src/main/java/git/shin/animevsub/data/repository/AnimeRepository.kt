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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
  private val api: AnimeApi,
  private val historyRepository: HistoryRepository,
  private val prefs: PreferencesManager
) {
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
  val isLoggedIn: Flow<Boolean> = api.getUser().map { it != null }

  suspend fun login(email: String, password: String): Result<User> = runCatching {
    api.login(email, password)
  }

  suspend fun logout() {
    api.logout()
  }

  // Settings
  val autoNext = prefs.autoNext
  val autoSkip = prefs.autoSkip

  suspend fun setAutoNext(value: Boolean) = prefs.setAutoNext(value)
  suspend fun setAutoSkip(value: Boolean) = prefs.setAutoSkip(value)

  // Search History
  val searchHistory = prefs.searchHistory
  suspend fun addSearchHistory(query: String) = prefs.addSearchHistory(query)
  suspend fun clearSearchHistory() = prefs.clearSearchHistory()

  // Notifications
  suspend fun getNotifications(): Result<NotificationData> = runCatching {
    api.getNotifications()
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
