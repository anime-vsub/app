package git.shin.animevsub.data.remote.api

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
import git.shin.animevsub.utils.CloudflareManager
import kotlinx.coroutines.flow.Flow

interface AnimeDataSource {
  val hostCurl: String
  val baseUrl: String
  val loginUrl: String

  fun getUser(): Flow<User?>
  suspend fun refreshUser(): User
  suspend fun logout()

  suspend fun getHomePage(): HomeData
  suspend fun getSchedule(): List<ScheduleDay>
  suspend fun getRankings(type: String): List<AnimeCard>
  suspend fun getRankingTypes(): List<FilterOption>

  suspend fun preSearch(keyword: String): List<SearchSuggestion>
  suspend fun search(keyword: String, page: Int): CategoryPage
  suspend fun getCategory(filters: List<SelectedFilter>, page: Int): CategoryPage
  suspend fun getFilters(filters: List<SelectedFilter>): List<FilterGroup>

  suspend fun getAnimeDetail(animeId: String): AnimeDetail
  suspend fun getChapters(animeId: String): ChapterData

  suspend fun getServers(chapter: ChapterInfo): List<ServerInfo>
  suspend fun getPlayerLink(server: ServerInfo): PlayerData

  suspend fun getEpisodeSkip(animeId: String, detail: AnimeDetail, chapter: ChapterInfo): InOutroEpisode?

  suspend fun getFollows(page: Int): CategoryPage
  suspend fun checkFollow(animeId: String): Boolean
  suspend fun toggleFollow(animeId: String, follow: Boolean)

  suspend fun getNotifications(): NotificationData
  suspend fun onTrigger(trigger: Trigger)

  suspend fun getComments(filmId: String, anime: AnimeDetail, sort: FilterOption?, offset: Int): CommentResponse
  suspend fun getReplies(commentId: String, sort: FilterOption?, offset: Int): ReplyResponse
  suspend fun postComment(filmId: String, content: String, isSpoiler: Boolean, episodeId: String?, parentId: String, threadKey: String?): PostCommentResponse
  suspend fun voteComment(commentId: String, voteType: VoteType): VoteResponse
  suspend fun editComment(commentId: String, content: String, isSpoiler: Boolean): EditCommentResponse

  suspend fun getCommentSortOptions(): List<FilterOption>

  fun encodeURI(url: String): String
  fun decodeURI(url: String): String

  companion object {
    val userAgent: String
      get() = CloudflareManager.getCurrentUserAgent()

    fun getHeaders(url: String): Map<String, String> = mapOf(
      "User-Agent" to userAgent,
      "Referer" to url.substringBefore("/", "").ifEmpty { "" }
    )

    fun extractBackgroundImage(style: String): String = Regex("background-image\\s*:\\s*url\\(['\"]?([^'\")]+)['\"]?\\)").find(style)?.groupValues?.getOrNull(1) ?: ""
  }
}
