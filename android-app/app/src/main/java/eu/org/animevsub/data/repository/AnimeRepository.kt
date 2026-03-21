package eu.org.animevsub.data.repository

import eu.org.animevsub.data.local.PreferencesManager
import eu.org.animevsub.data.model.*
import eu.org.animevsub.data.remote.AnimeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    suspend fun getAnimeDetail(seasonId: String): Result<AnimeDetail> = runCatching {
        val cookie = prefs.cookie.first()
        api.getAnimeDetail(seasonId, cookie)
    }

    // Chapters
    suspend fun getChapters(seasonId: String): Result<ChapterData> = runCatching {
        val cookie = prefs.cookie.first()
        api.getChapters(seasonId, cookie)
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
    suspend fun getCategoryPage(typeNormal: String, value: String, page: Int = 1): Result<CategoryPage> = runCatching {
        api.getCategoryPage(typeNormal, value, page)
    }

    // Player
    suspend fun getPlayerLink(id: String, play: String, hash: String): Result<String> = runCatching {
        api.getPlayerLink(id, play, hash)
    }

    // Auth
    val isLoggedIn: Flow<Boolean> = prefs.isLoggedIn
    val user: Flow<User?> = prefs.user
    val cookie: Flow<String?> = prefs.cookie

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        val (user, cookie) = api.login(email, password)
        prefs.saveAuth(user, cookie)
        user
    }

    suspend fun logout() {
        prefs.clearAuth()
    }

    // Notifications
    suspend fun getNotifications(): Result<NotificationData> = runCatching {
        val cookie = prefs.cookie.first() ?: throw Exception("Not logged in")
        api.getNotifications(cookie)
    }

    // Settings
    val autoNext: Flow<Boolean> = prefs.autoNext
    val autoSkip: Flow<Boolean> = prefs.autoSkip
    val server: Flow<String> = prefs.server
    val movieMode: Flow<Boolean> = prefs.movieMode
    val showComments: Flow<Boolean> = prefs.showComments
    val infiniteScroll: Flow<Boolean> = prefs.infiniteScroll

    suspend fun setAutoNext(value: Boolean) = prefs.setAutoNext(value)
    suspend fun setAutoSkip(value: Boolean) = prefs.setAutoSkip(value)
    suspend fun setServer(value: String) = prefs.setServer(value)
    suspend fun setMovieMode(value: Boolean) = prefs.setMovieMode(value)
    suspend fun setShowComments(value: Boolean) = prefs.setShowComments(value)
    suspend fun setInfiniteScroll(value: Boolean) = prefs.setInfiniteScroll(value)

    // Search history
    val searchHistory: Flow<List<String>> = prefs.searchHistory
    suspend fun addSearchHistory(keyword: String) = prefs.addSearchHistory(keyword)
    suspend fun clearSearchHistory() = prefs.clearSearchHistory()
}
