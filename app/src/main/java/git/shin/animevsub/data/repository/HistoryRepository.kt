package git.shin.animevsub.data.repository

import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.data.model.LastChapResponse
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.model.WatchProgress
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.MessageDigest
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
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

  private fun getGmtOffset(): Int {
    val tz = TimeZone.getDefault()
    return -(tz.rawOffset / (1000 * 60 * 60))
  }

  suspend fun upsertUser(user: User): Result<Unit> = runCatching {
    val uid = sha256((user.email ?: "") + user.name)
    supabase.postgrest.rpc(
      "upsert_user",
      buildJsonObject {
        put("p_uuid", uid)
        put("p_email", user.email)
        put("p_name", user.name)
      }
    )
  }

  suspend fun getHistory(page: Int, size: Int = 30): Result<List<HistoryItem>> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "query_history",
      buildJsonObject {
        put("user_uid", uid)
        put("page", page)
        put("size", size)
      }
    )
    response.decodeList<HistoryItem>().map { it.copy(poster = AnimeApi.decodeURI(it.poster)) }
  }

  suspend fun getWatchProgress(seasonId: String): Result<List<WatchProgress>> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "get_watch_progress",
      buildJsonObject {
        put("user_uid", uid)
        put("season_id", seasonId)
      }
    )
    response.decodeList<WatchProgress>()
  }

  suspend fun getSingleProgress(seasonId: String, chapId: String): Result<WatchProgress?> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "get_single_progress",
      buildJsonObject {
        put("user_uid", uid)
        put("season_id", seasonId)
        put("p_chap_id", chapId)
      }
    )
    try {
      response.decodeSingle<WatchProgress>()
    } catch (e: Exception) {
      print(e)
      null
    }
  }

  suspend fun setSingleProgress(
    name: String,
    poster: String,
    seasonId: String,
    seasonName: String,
    chapId: String,
    chapName: String,
    cur: Double,
    dur: Double
  ): Result<Unit> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    supabase.postgrest.rpc(
      "set_single_progress",
      buildJsonObject {
        put("user_uid", uid)
        put("p_name", name)
        put("p_poster", AnimeApi.encodeURI(poster))
        put("season_id", seasonId)
        put("p_season_name", seasonName)
        put("e_cur", cur)
        put("e_dur", dur)
        put("e_name", chapName)
        put("e_chap", chapId)
        put("gmt", getGmtOffset())
      }
    )
  }

  suspend fun getLastChapOfSeason(seasonId: String): Result<String?> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Login required")
    val response = supabase.postgrest.rpc(
      "get_last_chap",
      buildJsonObject {
        put("user_uid", uid)
        put("season_id", seasonId)
      }
    )
    try {
      response.decodeSingle<LastChapResponse>().chapId
    } catch (e: Exception) {
      null
    }
  }
}
