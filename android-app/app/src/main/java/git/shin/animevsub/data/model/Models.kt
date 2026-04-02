package git.shin.animevsub.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AnimeCard(
  val animeId: String,
  val image: String,
  val name: String,
  val lastEpisodeId: String? = null,
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

/**
 * Represents a Season displayed on the UI.
 * Can be a real season from the API or a "Virtual Season" split from a very long season.
 */
data class DisplaySeason(
  val id: String,        // Unique ID for UI (can be a virtual ID like "ss1_30")
  val realId: String,    // Real ID used for API calls (e.g., "ss1")
  val name: String,      // Display name (e.g., "Season 1 (31-60)")
  val range: IntRange? = null, // Index range of chapters in the real season (if virtual)
  val isVirtual: Boolean = false
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
  val id: String,
  val name: String,
  val extra: Map<String, String> = emptyMap()
)

data class ServerInfo(
  val name: String,
  val extra: Map<String, String> = emptyMap()
)

data class PlayerData(
  val link: String,
  val type: String,
  val headers: Map<String, String>? = null,
  val isContent: Boolean = false
)

data class ChapterData(
  val chaps: List<ChapterInfo>,
  val update: Triple<Int, Int, Int>?,
  val image: String,
  val poster: String
)

data class ScheduleDay(
  val date: Long,
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

@Serializable
data class FilterOption(
  val id: String,
  val name: String
)

data class FilterGroup(
  val id: String,
  val name: String,
  val options: List<FilterOption>,
  val isMultiple: Boolean = false
)

@Serializable
data class SelectedFilter(
  val groupId: String,
  val id: String,
  val name: String,
  val include: Boolean = true,
  val exclude: Boolean = false
)
