package git.shin.animevsub.data.repository

import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.model.Playlist
import git.shin.animevsub.data.model.PlaylistHasMovieResponse
import git.shin.animevsub.data.model.PlaylistItem
import git.shin.animevsub.data.model.PlaylistPosterResponse
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
  private val supabase: SupabaseClient,
  private val storage: ApiStorage,
  private val json: Json
) {
  private suspend fun getCurrentUid(): String? {
    val userJson = storage.getString("user_data").firstOrNull() ?: return null
    return try {
      val user = json.decodeFromString<User>(userJson)
      sha256((user.email ?: "") + user.name)
    } catch (e: Exception) {
      null
    }
  }

  private fun sha256(input: String): String = MessageDigest.getInstance("SHA-256")
    .digest(input.toByteArray())
    .joinToString("") { "%02x".format(it) }

  suspend fun getPlaylists(): Result<List<Playlist>> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "get_list_playlist",
      buildJsonObject {
        put("user_uid", uid)
      }
    )
    response.decodeList<Playlist>()
  }

  suspend fun createPlaylist(name: String, isPublic: Boolean): Result<Playlist> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "create_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_name", name)
        put("is_public", isPublic)
      }
    )
    response.decodeSingle<Playlist>()
  }

  suspend fun deletePlaylist(id: Int): Result<Unit> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    supabase.postgrest.rpc(
      "delete_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_id", id)
      }
    )
  }

  suspend fun renamePlaylist(oldName: String, newName: String): Result<Unit> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    supabase.postgrest.rpc(
      "rename_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("old_name", oldName)
        put("new_name", newName)
      }
    )
  }

  suspend fun setDescriptionPlaylist(id: Int, description: String): Result<Unit> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    supabase.postgrest.rpc(
      "set_description_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_id", id)
        put("playlist_description", description)
      }
    )
  }

  suspend fun setPublicPlaylist(id: Int, isPublic: Boolean): Result<Unit> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    supabase.postgrest.rpc(
      "set_public_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_id", id)
        put("is_public", isPublic)
      }
    )
  }

  suspend fun addAnimeToPlaylist(
    id: Int,
    seasonId: String,
    seasonName: String,
    name: String,
    poster: String,
    chapId: String?,
    chapName: String?
  ): Result<Playlist?> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "add_movie_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_id", id)
        put("p_chap", chapId)
        put("p_name", name)
        put("p_name_chap", chapName)
        put("p_name_season", seasonName)
        put("p_poster", AnimeApi.encodeURI(poster))
        put("p_season", seasonId)
      }
    )
    try {
      response.decodeSingle<Playlist>()
    } catch (e: Exception) {
      null
    }
  }

  suspend fun deleteAnimeFromPlaylist(id: Int, seasonId: String): Result<Playlist?> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "delete_movie_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_id", id)
        put("p_season", seasonId)
      }
    )
    try {
      response.decodeSingle<Playlist>()
    } catch (e: Exception) {
      null
    }
  }

  suspend fun hasAnimeOfPlaylists(ids: List<Int>, seasonId: String): Result<List<Boolean>> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "has_movie_playlists",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_ids", buildJsonArray { ids.forEach { add(it) } })
        put("season_id", seasonId)
      }
    )
    val data = response.decodeList<PlaylistHasMovieResponse>()
    val map = data.associate { it.playlistId to it.hasMovie }
    ids.map { map[it] ?: false }
  }

  suspend fun getAnimesFromPlaylist(
    id: Int,
    page: Int,
    sorter: String = "desc"
  ): Result<List<PlaylistItem>> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "get_movies_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_id", id)
        put("sorter", sorter)
        put("page", page)
        put("page_size", 30)
      }
    )
    response.decodeList<PlaylistItem>().map {
      it.copy(poster = AnimeApi.decodeURI(it.poster))
    }
  }

  suspend fun getPosterPlaylist(id: Int): Result<String?> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "get_poster_playlist",
      buildJsonObject {
        put("user_uid", uid)
        put("playlist_id", id)
      }
    )
    try {
      val data = response.decodeSingle<PlaylistPosterResponse>()
      data.poster?.let { AnimeApi.decodeURI(it) }
    } catch (e: Exception) {
      null
    }
  }
}
