package git.shin.animevsub.data.remote.api_example

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
import git.shin.animevsub.data.remote.api.AnimeDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GogoAnimeDataSource @Inject constructor() : AnimeDataSource {

  override val hostCurl: String = "gogoanime.llc"
  override val baseUrl: String = "https://gogoanime.llc"
  override val loginUrl: String = "$baseUrl/login.html"

  override fun getUser(): Flow<User?> = kotlinx.coroutines.flow.flowOf(null)

  override suspend fun refreshUser(): User = throw NotImplementedError("GogoAnime does not support authentication")

  override suspend fun logout() {}

  override suspend fun getHomePage(): HomeData = throw NotImplementedError("Not implemented yet")

  override suspend fun getSchedule(): List<ScheduleDay> = throw NotImplementedError("Not implemented yet")

  override suspend fun getRankings(type: String): List<AnimeCard> = throw NotImplementedError("Not implemented yet")

  override suspend fun getRankingTypes(): List<FilterOption> = throw NotImplementedError("Not implemented yet")

  override suspend fun preSearch(keyword: String): List<SearchSuggestion> = throw NotImplementedError("Not implemented yet")

  override suspend fun search(keyword: String, page: Int): CategoryPage = throw NotImplementedError("Not implemented yet")

  override suspend fun getCategory(filters: List<SelectedFilter>, page: Int): CategoryPage = throw NotImplementedError("Not implemented yet")

  override suspend fun getFilters(filters: List<SelectedFilter>): List<FilterGroup> = throw NotImplementedError("Not implemented yet")

  override suspend fun getAnimeDetail(animeId: String): AnimeDetail = throw NotImplementedError("Not implemented yet")

  override suspend fun getChapters(animeId: String): ChapterData = throw NotImplementedError("Not implemented yet")

  override suspend fun getServers(chapter: ChapterInfo): List<ServerInfo> = throw NotImplementedError("Not implemented yet")

  override suspend fun getPlayerLink(server: ServerInfo): PlayerData = throw NotImplementedError("Not implemented yet")

  override suspend fun getEpisodeSkip(animeId: String, detail: AnimeDetail, chapter: ChapterInfo): InOutroEpisode? = null

  override suspend fun getFollows(page: Int): CategoryPage = throw NotImplementedError("Not implemented yet")

  override suspend fun checkFollow(animeId: String): Boolean = throw NotImplementedError("Not implemented yet")

  override suspend fun toggleFollow(animeId: String, follow: Boolean) = throw NotImplementedError("Not implemented yet")

  override suspend fun getNotifications(): NotificationData = throw NotImplementedError("Not implemented yet")

  override suspend fun onTrigger(trigger: Trigger) {}

  override suspend fun getComments(filmId: String, anime: AnimeDetail, sort: FilterOption?, offset: Int): CommentResponse = throw NotImplementedError("Not implemented yet")

  override suspend fun getReplies(commentId: String, sort: FilterOption?, offset: Int): ReplyResponse = throw NotImplementedError("Not implemented yet")

  override suspend fun postComment(filmId: String, content: String, isSpoiler: Boolean, episodeId: String?, parentId: String, threadKey: String?): PostCommentResponse = throw NotImplementedError("Not implemented yet")

  override suspend fun voteComment(commentId: String, voteType: VoteType): VoteResponse = throw NotImplementedError("Not implemented yet")

  override suspend fun editComment(commentId: String, content: String, isSpoiler: Boolean): EditCommentResponse = throw NotImplementedError("Not implemented yet")

  override suspend fun getCommentSortOptions(): List<FilterOption> = throw NotImplementedError("Not implemented yet")

  override fun encodeURI(url: String): String = url

  override fun decodeURI(url: String): String = url
}
