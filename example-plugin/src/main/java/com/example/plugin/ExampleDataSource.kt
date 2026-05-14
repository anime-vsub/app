package com.example.plugin

import git.shin.animevsub.data.model.*
import git.shin.animevsub.data.remote.api.AnimeDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class ExampleDataSource : AnimeDataSource {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val baseUrl = "https://api.example-anime-api.com"
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"

    override val hostCurl: String = "api.example-anime-api.com"
    override val baseUrl: String = baseUrl
    override val loginUrl: String = "$baseUrl/login"

    override fun getUser(): Flow<User?> = flow { emit(null) }
    override suspend fun refreshUser(): User = User()
    override suspend fun logout() {}

    override suspend fun getHomePage(): HomeData = HomeData(
        featured = emptyList(),
        latestUpdated = emptyList(),
        latestEpisodes = emptyList(),
        trending = emptyList(),
        topUpcoming = emptyList(),
        top = emptyList(),
        newest = emptyList(),
        topAiring = emptyList(),
        topMovie = emptyList(),
        genres = emptyList(),
        scheduleDays = emptyList(),
        isEmpty = true
    )

    override suspend fun getSchedule(): List<ScheduleDay> = emptyList()
    override suspend fun getRankings(type: String): List<AnimeCard> = emptyList()
    override suspend fun getRankingTypes(): List<FilterOption> = emptyList()

    override suspend fun preSearch(keyword: String): List<SearchSuggestion> = emptyList()
    override suspend fun search(keyword: String, page: Int): CategoryPage = CategoryPage(animeList = emptyList(), hasNextPage = false, totalPages = 0, totalResults = 0)
    override suspend fun getCategory(filters: List<SelectedFilter>, page: Int): CategoryPage = CategoryPage(animeList = emptyList(), hasNextPage = false, totalPages = 0, totalResults = 0)
    override suspend fun getFilters(filters: List<SelectedFilter>): List<FilterGroup> = emptyList()

    override suspend fun getAnimeDetail(animeId: String): AnimeDetail = AnimeDetail()
    override suspend fun getChapters(animeId: String): ChapterData = ChapterData(chapters = emptyList(), totalPages = 0, currentPage = 0)

    override suspend fun getServers(chapter: ChapterInfo): List<ServerInfo> = emptyList()
    override suspend fun getPlayerLink(server: ServerInfo): PlayerData = PlayerData(videoUrl = "")

    override suspend fun getEpisodeSkip(animeId: String, detail: AnimeDetail, chapter: ChapterInfo): InOutroEpisode? = null

    override suspend fun getFollows(page: Int): CategoryPage = CategoryPage(animeList = emptyList(), hasNextPage = false, totalPages = 0, totalResults = 0)
    override suspend fun checkFollow(animeId: String): Boolean = false
    override suspend fun toggleFollow(animeId: String, follow: Boolean) {}

    override suspend fun getNotifications(): NotificationData = NotificationData(items = emptyList())
    override suspend fun onTrigger(trigger: Trigger) {}

    override suspend fun getComments(filmId: String, anime: AnimeDetail, sort: FilterOption?, offset: Int): CommentResponse = CommentResponse(comments = emptyList(), hasMore = false, total = 0)
    override suspend fun getReplies(commentId: String, sort: FilterOption?, offset: Int): ReplyResponse = ReplyResponse(replies = emptyList(), hasMore = false)
    override suspend fun postComment(filmId: String, content: String, isSpoiler: Boolean, episodeId: String?, parentId: String, threadKey: String?): PostCommentResponse = PostCommentResponse(id = "", success = true)
    override suspend fun voteComment(commentId: String, voteType: VoteType): VoteResponse = VoteResponse(success = true, newScore = 0)
    override suspend fun editComment(commentId: String, content: String, isSpoiler: Boolean): EditCommentResponse = EditCommentResponse(success = true)
    override suspend fun getCommentSortOptions(): List<FilterOption> = emptyList()

    override fun encodeURI(url: String): String = URLEncoder.encode(url, "UTF-8")
    override fun decodeURI(url: String): String = java.net.URLDecoder.decode(url, "UTF-8")
}