package git.shin.animevsub.data.repository

import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.DbNotificationCount
import git.shin.animevsub.data.model.DbNotificationEpisode
import git.shin.animevsub.data.model.DbNotificationItem
import git.shin.animevsub.data.model.NotificationData
import git.shin.animevsub.data.model.NotificationItem
import git.shin.animevsub.data.model.Trigger
import git.shin.animevsub.data.model.User
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDatabaseRepository @Inject constructor(
  private val supabase: SupabaseClient,
  private val storage: ApiStorage,
  private val json: Json,
  private val prefs: PreferencesManager
) {
  private val _dbNotifications = MutableStateFlow<List<DbNotificationItem>>(emptyList())
  val dbNotifications = _dbNotifications.asStateFlow()

  private val _dbNotificationCount = MutableStateFlow<DbNotificationCount?>(null)
  val dbNotificationCount = _dbNotificationCount.asStateFlow()

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing = _isSyncing.asStateFlow()

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

  suspend fun getCountNotify(): Result<DbNotificationCount?> = runCatching {
    val uid = getCurrentUid() ?: return@runCatching null
    val response = supabase.postgrest.rpc(
      "get_count_notify",
      buildJsonObject {
        put("p_user_uid", uid)
      }
    )
    val count = response.decodeSingleOrNull<DbNotificationCount>()
    _dbNotificationCount.value = count
    count
  }

  suspend fun queryNotify(
    page: Int,
    pageSize: Int = 30,
    query: String? = null,
    asc: Boolean = false
  ): Result<List<DbNotificationItem>> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Not login")
    val response = supabase.postgrest.rpc(
      "query_notify",
      buildJsonObject {
        put("p_page", page)
        put("p_page_size", pageSize)
        put("p_user_uid", uid)
        put("p_query", query)
        put("p_asc", asc)
      }
    )
    val items = response.decodeList<DbNotificationItem>().map {
      it.copy(image = it.image?.let { img -> AnimeApi.decodeURI(img) })
    }

    if (page == 1) {
      _dbNotifications.value = items
    } else {
      _dbNotifications.value = (_dbNotifications.value + items).distinctBy { it.season }
    }

    items
  }

  suspend fun deleteNotify(
    season: String,
    chapId: String? = null
  ): Result<DbNotificationCount?> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Not login")
    val response = supabase.postgrest.rpc(
      "delete_notify",
      buildJsonObject {
        put("user_uid", uid)
        put("p_season", season)
        put("p_chapid", chapId)
      }
    )

    // Optimistic delete from local list
    if (chapId == null) {
      _dbNotifications.value = _dbNotifications.value.filterNot { it.season == season }
    } else {
      _dbNotifications.value = _dbNotifications.value.map { item ->
        if (item.season == season) {
          item.copy(episodes = item.episodes.filterNot { it.chapId == chapId })
        } else {
          item
        }
      }.filter { it.episodes.isNotEmpty() }
    }

    val count = response.decodeSingleOrNull<DbNotificationCount>()
    _dbNotificationCount.value = count
    count
  }

  suspend fun addNotify(item: NotificationItem): Result<Unit> = runCatching {
    val uid = getCurrentUid() ?: throw Exception("Not login")
    val response = supabase.postgrest.rpc(
      "upsert_notify",
      buildJsonObject {
        put("p_image", item.image?.let { AnimeApi.encodeURI(it) })
        put("p_name", item.title)
        put("p_chap", item.content.replace(Regex("[^0-9.]"), ""))
        put("p_time", item.createdAt?.toString())
        put("p_season", item.animeId)
        put("p_chapid", item.chapId)
        put("user_uid", uid)
      }
    )

    // Optimistic add/update in local list
    val currentList = _dbNotifications.value.toMutableList()
    val existingIndex = currentList.indexOfFirst { it.season == item.animeId }
    val newEpisode = DbNotificationEpisode(
      name = item.content,
      chapId = item.chapId ?: "",
      time = item.createdAt ?: java.time.Instant.now(),
      createdAt = item.createdAt ?: java.time.Instant.now()
    )

    if (existingIndex != -1) {
      val existingItem = currentList[existingIndex]
      val updatedEpisodes = (listOf(newEpisode) + existingItem.episodes).distinctBy { it.chapId }
      currentList[existingIndex] = existingItem.copy(
        episodes = updatedEpisodes,
        latestChapTime = newEpisode.time
      )
    } else {
      currentList.add(
        0,
        DbNotificationItem(
          season = item.animeId ?: "",
          name = item.title,
          image = item.image,
          episodes = listOf(newEpisode),
          latestChapTime = newEpisode.time,
          createdAt = java.time.Instant.now()
        )
      )
    }
    _dbNotifications.value = currentList

    val count = response.decodeSingleOrNull<DbNotificationCount>()
    if (count != null) {
      _dbNotificationCount.value = count
    }
  }

  suspend fun startSync(
    getApiNotifications: suspend () -> Result<NotificationData>,
    onTrigger: suspend (Trigger) -> Result<Unit>
  ): Result<Unit> = runCatching {
    if (_isSyncing.value) return@runCatching
    _isSyncing.value = true
    try {
      while (true) {
        val apiData = getApiNotifications().getOrNull() ?: break
        if (apiData.items.isEmpty()) break

        for (item in apiData.items) {
          addNotify(item).onSuccess {
            item.closeTrigger?.let { onTrigger(it) }
          }
          kotlinx.coroutines.delay(200)
        }
//
//        // The API returns notifications in pages (usually 10-20 per page).
//        // If we get fewer than a full page (e.g., < 5), it means there are no more notifications to fetch.
//        if (apiData.items.size < 5) break
      }
      getCountNotify()
    } finally {
      _isSyncing.value = false
    }
  }

  fun clear() {
    _dbNotificationCount.value = null
  }
}
