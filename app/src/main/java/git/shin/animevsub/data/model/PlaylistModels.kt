package git.shin.animevsub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
  val id: Int,
  val name: String,
  val description: String? = null,
  @SerialName("public") val isPublic: Boolean = false,
  @SerialName("created_at") val createdAt: String? = null,
  @SerialName("movies_count") val movieCount: Int = 0,
  @SerialName("poster_url") val poster: String? = null
)

@Serializable
data class PlaylistItem(
  val id: Int? = null,
  val name: String,
  val poster: String,
  @SerialName("season") val seasonId: String,
  @SerialName("name_season") val seasonName: String,
  @SerialName("chap") val chapId: String? = null,
  @SerialName("name_chap") val chapName: String? = null,
  @SerialName("add_at") val addAt: String? = null
)

@Serializable
data class PlaylistHasMovieResponse(
  @SerialName("playlist_id") val playlistId: Int,
  @SerialName("has_movie") val hasMovie: Boolean
)

@Serializable
data class PlaylistPosterResponse(
  val poster: String? = null
)
