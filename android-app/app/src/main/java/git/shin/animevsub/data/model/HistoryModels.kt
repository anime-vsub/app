package git.shin.animevsub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val id: String? = null,
    val name: String,
    val poster: String,
    val season: String,
    @SerialName("season_name") val seasonName: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("chap_id") val chapId: String? = null,
    @SerialName("chap_name") val chapName: String? = null,
    val cur: Double = 0.0,
    val dur: Double = 0.0
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
