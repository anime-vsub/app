package git.shin.animevsub.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.local.dao.LocalHistoryDao
import git.shin.animevsub.data.local.entity.AnimeSyncModeEntity
import git.shin.animevsub.data.local.entity.LocalProgressEntity
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.data.model.LastChapResponse
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.model.WatchProgress
import git.shin.animevsub.data.remote.api.AnimeDataSource
import git.shin.animevsub.widget.ContinueWatchingWidget
import git.shin.animevsub.widget.HistoryWidget
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.MessageDigest
import java.time.Instant
import java.time.ZonedDateTime
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
  private val supabase: SupabaseClient,
  private val storage: ApiStorage,
  private val json: Json,
  private val animeDataSource: AnimeDataSource,
  private val localHistoryDao: LocalHistoryDao,
  @ApplicationContext private val context: Context
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

  private fun rethrowIfCancellation(e: Throwable) {
    if (e is CancellationException) throw e
  }

  // 0: Two-way, 1: Upload only, 2: Disabled (local only)
  suspend fun getSyncMode(animeId: String): Int = localHistoryDao.syncMode(animeId) ?: 0

  suspend fun isLocalOnly(seasonId: String): Boolean = getCurrentUid() == null || getSyncMode(seasonId) == 2

  suspend fun setSyncMode(animeId: String, mode: Int): Result<Unit> = runCatching {
    val previous = getSyncMode(animeId)
    localHistoryDao.upsertSyncMode(AnimeSyncModeEntity(animeId, mode))

    // Re-enabling sync: push the newest local progress once, then use the cloud again.
    if (previous == 2 && mode == 0 && getCurrentUid() != null) {
      localHistoryDao.latestForSeason(animeId)?.let { row ->
        setSingleProgress(
          name = row.animeName,
          poster = animeDataSource.decodeURI(row.poster),
          seasonId = row.seasonId,
          seasonName = row.seasonName,
          chapId = row.chapId,
          chapName = row.chapName,
          cur = row.cur,
          dur = row.dur
        )
      }
    }

    if (previous != mode) {
      runCatching { ContinueWatchingWidget.refresh(context) }
      runCatching { HistoryWidget.refresh(context) }
    }
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
    val uid = getCurrentUid() ?: return@runCatching localHistory(page, size)

    val localOnlyIds = localHistoryDao.localOnlyAnimeIds()
    val cloudItems = try {
      queryCloudHistory(uid, page, size).filterNot { it.seasonId in localOnlyIds }
    } catch (e: Exception) {
      rethrowIfCancellation(e)
      return@runCatching localHistory(page, size)
    }

    if (page > 1) return@runCatching cloudItems

    val localItems = if (localOnlyIds.isEmpty()) {
      emptyList()
    } else {
      localHistoryDao.latestForSeasons(localOnlyIds).map { it.toHistoryItem() }
    }
    (cloudItems + localItems).sortedByDescending { it.sortTime() }
  }

  private suspend fun queryCloudHistory(uid: String, page: Int, size: Int): List<HistoryItem> {
    val response = supabase.postgrest.rpc(
      "query_history",
      buildJsonObject {
        put("user_uid", uid)
        put("page", page)
        put("size", size)
      }
    )
    return response.decodeList<HistoryItem>()
      .map { it.copy(poster = animeDataSource.decodeURI(it.poster)) }
  }

  private suspend fun localHistory(page: Int, size: Int): List<HistoryItem> {
    val offset = ((page - 1).coerceAtLeast(0)) * size
    return localHistoryDao.latestPerSeason(size, offset).map { it.toHistoryItem() }
  }

  suspend fun getWatchProgress(seasonId: String): Result<List<WatchProgress>> {
    if (isLocalOnly(seasonId)) {
      return runCatching { localWatchProgress(seasonId) }
    }
    return try {
      val uid = getCurrentUid() ?: return runCatching { localWatchProgress(seasonId) }
      val response = supabase.postgrest.rpc(
        "get_watch_progress",
        buildJsonObject {
          put("user_uid", uid)
          put("season_id", seasonId)
        }
      )
      Result.success(response.decodeList<WatchProgress>())
    } catch (e: Exception) {
      rethrowIfCancellation(e)
      runCatching { localWatchProgress(seasonId) }
    }
  }

  private suspend fun localWatchProgress(seasonId: String): List<WatchProgress> = localHistoryDao.progressForSeason(seasonId).map {
    WatchProgress(cur = it.cur, dur = it.dur, name = it.chapName, chapId = it.chapId)
  }

  suspend fun getSingleProgress(seasonId: String, chapId: String): Result<WatchProgress?> {
    if (isLocalOnly(seasonId)) {
      return runCatching { localSingleProgress(seasonId, chapId) }
    }
    return try {
      val uid = getCurrentUid() ?: return runCatching { localSingleProgress(seasonId, chapId) }
      val response = supabase.postgrest.rpc(
        "get_single_progress",
        buildJsonObject {
          put("user_uid", uid)
          put("season_id", seasonId)
          put("p_chap_id", chapId)
        }
      )
      val cloud = try {
        response.decodeSingle<WatchProgress>()
      } catch (e: Exception) {
        rethrowIfCancellation(e)
        null
      }
      Result.success(cloud ?: localSingleProgress(seasonId, chapId))
    } catch (e: Exception) {
      rethrowIfCancellation(e)
      runCatching { localSingleProgress(seasonId, chapId) }
    }
  }

  private suspend fun localSingleProgress(seasonId: String, chapId: String): WatchProgress? = localHistoryDao.progressForChapter(seasonId, chapId)?.let {
    WatchProgress(cur = it.cur, dur = it.dur, name = it.chapName, chapId = it.chapId)
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
  ): Result<Unit> {
    // Always mirror locally: backup for cloud mode, source of truth for local-only mode.
    localHistoryDao.upsert(
      LocalProgressEntity(
        seasonId = seasonId,
        chapId = chapId,
        animeName = name,
        poster = animeDataSource.encodeURI(poster),
        seasonName = seasonName,
        chapName = chapName,
        cur = cur,
        dur = dur,
        updatedAt = System.currentTimeMillis()
      )
    )

    val cloudResult = if (isLocalOnly(seasonId)) {
      Result.success(Unit)
    } else {
      runCatching {
        val uid = getCurrentUid() ?: throw Exception("Login required")
        supabase.postgrest.rpc(
          "set_single_progress",
          buildJsonObject {
            put("user_uid", uid)
            put("p_name", name)
            put("p_poster", animeDataSource.encodeURI(poster))
            put("season_id", seasonId)
            put("p_season_name", seasonName)
            put("e_cur", cur)
            put("e_dur", dur)
            put("e_name", chapName)
            put("e_chap", chapId)
            put("gmt", getGmtOffset())
          }
        )
        Unit
      }
    }

    runCatching { ContinueWatchingWidget.refresh(context) }
    runCatching { HistoryWidget.refresh(context) }
    return cloudResult
  }

  suspend fun getLastChapOfSeason(seasonId: String): Result<String?> {
    if (isLocalOnly(seasonId)) {
      return runCatching { localHistoryDao.lastChapId(seasonId) }
    }
    return try {
      val uid = getCurrentUid() ?: return runCatching { localHistoryDao.lastChapId(seasonId) }
      val response = supabase.postgrest.rpc(
        "get_last_chap",
        buildJsonObject {
          put("user_uid", uid)
          put("season_id", seasonId)
        }
      )
      val cloud = try {
        response.decodeSingle<LastChapResponse>().chapId
      } catch (e: Exception) {
        rethrowIfCancellation(e)
        null
      }
      Result.success(cloud ?: localHistoryDao.lastChapId(seasonId))
    } catch (e: Exception) {
      rethrowIfCancellation(e)
      runCatching { localHistoryDao.lastChapId(seasonId) }
    }
  }

  private fun LocalProgressEntity.toHistoryItem(): HistoryItem = HistoryItem(
    name = animeName,
    poster = animeDataSource.decodeURI(poster),
    seasonId = seasonId,
    seasonName = seasonName,
    createdAt = Instant.ofEpochMilli(updatedAt).toString(),
    chapId = chapId,
    chapName = chapName,
    cur = cur,
    dur = dur
  )

  private fun HistoryItem.sortTime(): Long {
    val value = createdAt ?: return 0L
    return runCatching { ZonedDateTime.parse(value).toInstant().toEpochMilli() }
      .recoverCatching { Instant.parse(value).toEpochMilli() }
      .getOrDefault(0L)
  }
}
