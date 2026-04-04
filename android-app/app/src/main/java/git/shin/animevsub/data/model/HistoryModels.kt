package git.shin.animevsub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
  val name: String,
  val poster: String,
  @SerialName("season") val seasonId: String,
  @SerialName("season_name") val seasonName: String,
  @SerialName("created_at") val createdAt: String? = null,
  @SerialName("watch_id") val chapId: String? = null,
  @SerialName("watch_name") val chapName: String? = null,
  @SerialName("watch_cur") val cur: Double = 0.0,
  @SerialName("watch_dur") val dur: Double = 0.0
)

@Serializable
data class WatchProgress(
    val cur: Double,
    val dur: Double,
    val name: String? = null,
    @SerialName("chap_id") val chapId: String? = null
)

@Serializable
data class LastChapResponse(
    @SerialName("chap_id") val chapId: String? = null
)
