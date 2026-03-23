package git.shin.animevsub.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AnimeCard(
  val animeId: String,
  val image: String,
  val name: String,
  val chap: String? = null,
  val rate: Float = 0f,
  val views: Int? = null,
  val quality: String? = null,
  val process: String? = null,
  val year: Int? = null,
  val description: String? = null,
  val studio: String? = null,
  val genre: List<NamePath> = emptyList(),
  val timeRelease: Long? = null
)

@Serializable
data class NamePath(
  val name: String,
  val id: String,
)

data class HomeData(
  val thisSeason: List<AnimeCard>,
  val carousel: List<AnimeCard>,
  val lastUpdate: List<AnimeCard>,
  val preRelease: List<AnimeCard>,
  val nominate: List<AnimeCard>,
  val hotUpdate: List<AnimeCard>
)

data class AnimeDetail(
  val name: String,
  val othername: String?,
  val image: String?,
  val poster: String?,
  val pathToView: String?,
  val description: String,
  val rate: Int,
  val countRate: Int,
  val duration: String?,
  val yearOf: Int?,
  val views: Int,
  val season: List<NamePath>,
  val genre: List<NamePath>,
  val quality: String?,
  val authors: List<NamePath>,
  val countries: List<NamePath>,
  val follows: Int,
  val language: String?,
  val studio: String?,
  val seasonOf: NamePath?,
  val trailer: String?,
  val related: List<AnimeCard>
)

data class ChapterInfo(
  val id: String, val play: String, val hash: String, val name: String
)

data class ChapterData(
  val chaps: List<ChapterInfo>,
  val update: Triple<Int, Int, Int>?,
  val image: String,
  val poster: String
)

data class RankingItem(
  val animeId: String,
  val image: String,
  val name: String,
  val othername: String? = null,
  val process: String? = null,
  val rate: Float = 0f,
  val views: String = ""
)

data class ScheduleDay(
  val dayName: String,
  val date: String? = null,
  val month: String? = null,
  val isToday: Boolean = false,
  val items: List<AnimeCard>
)

data class SearchSuggestion(
  val animeId: String, val image: String, val name: String, val status: String
)

data class CategoryPage(
  val items: List<AnimeCard>,
  val totalPages: Int,
  val currentPage: Int,
  val name: String = "",
  val title: String = ""
)

@Serializable
data class User(
  val avatar: String? = null,
  val email: String,
  val name: String,
  val sex: String,
  val username: String
)

data class NotificationItem(
  val id: String,
  val image: String? = null,
  val avatar: String? = null,
  val title: String = "",
  val content: String = "",
  val description: String = "",
  val link: String = "",
  val animeId: String? = null,
  val time: String = "",
  val isRead: Boolean = false
)

data class NotificationData(
  val items: List<NotificationItem>, val max: Int
)

data class NewsItem(
  val title: String,
  val link: String,
  val description: String?,
  val pubDate: String?,
  val image: String?
)
