package git.shin.animevsub.data.repository

import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.AnimeDetail
import git.shin.animevsub.data.model.CategoryPage
import git.shin.animevsub.data.model.ChapterData
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.data.model.FilterGroup
import git.shin.animevsub.data.model.HomeData
import git.shin.animevsub.data.model.NotificationData
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.RankingItem
import git.shin.animevsub.data.model.ScheduleDay
import git.shin.animevsub.data.model.SearchSuggestion
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.remote.AnimeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
  private val api: AnimeApi,
  private val prefs: PreferencesManager
) {
  // Home
  suspend fun getHomePage(): Result<HomeData> = runCatching { api.getHomePage() }

  // Detail
  suspend fun getAnimeDetail(animeId: String): Result<AnimeDetail> = runCatching {
    val cookie = prefs.userCookie.first()
    api.getAnimeDetail(animeId, cookie)
  }

  // Chapters
  suspend fun getChapters(animeId: String): Result<ChapterData> = runCatching {
    val cookie = prefs.userCookie.first()
    api.getChapters(animeId, cookie)
  }

  // Rankings
  suspend fun getRankings(type: String): Result<List<RankingItem>> = runCatching {
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

  // Category
  suspend fun getCategory(
    type: String,
    value: String,
    filters: List<SelectedFilter> = emptyList(),
    page: Int = 1
  ): Result<CategoryPage> = runCatching {
    api.getCategory(type, value, filters, page)
  }

  suspend fun getFilters(path: String): Result<List<FilterGroup>> = runCatching {
    api.getFilters(path)
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
  suspend fun getSkipRange(chapter: ChapterInfo): Result<Pair<LongRange?, LongRange?>> =
    runCatching {
      api.getSkipRange(chapter)
    }

  // Auth
  val user: Flow<User?> = prefs.userData
  val cookie: Flow<String?> = prefs.userCookie
  val isLoggedIn: Flow<Boolean> = prefs.userCookie.map { it != null }

  suspend fun login(email: String, password: String): Result<User> = runCatching {
    val (user, cookie) = api.login(email, password)
    prefs.saveUser(user, cookie)
    user
  }

  suspend fun logout() {
    prefs.clearUser()
  }

  // Settings
  val autoNext = prefs.autoNext
  val autoSkip = prefs.autoSkip
  val server = prefs.server
  val movieMode = prefs.movieMode
  val showComments = prefs.showComments
  val infiniteScroll = prefs.infiniteScroll

  suspend fun setAutoNext(value: Boolean) = prefs.setAutoNext(value)
  suspend fun setAutoSkip(value: Boolean) = prefs.setAutoSkip(value)
  suspend fun setServer(value: String) = prefs.setServer(value)
  suspend fun setMovieMode(value: Boolean) = prefs.setMovieMode(value)
  suspend fun setShowComments(value: Boolean) = prefs.setShowComments(value)
  suspend fun setInfiniteScroll(value: Boolean) = prefs.setInfiniteScroll(value)

  // Search History
  val searchHistory = prefs.searchHistory
  suspend fun addSearchHistory(query: String) = prefs.addSearchHistory(query)
  suspend fun clearSearchHistory() = prefs.clearSearchHistory()

  // Notifications
  suspend fun getNotifications(): Result<NotificationData> = runCatching {
    val cookie = prefs.userCookie.first() ?: throw Exception("Not logged in")
    api.getNotifications(cookie)
  }
}
