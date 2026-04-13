package git.shin.animevsub.data.model

import java.time.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InstantSerializer : KSerializer<Instant> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(value.toString())
  override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}

@Serializable
data class NotificationData(
  val items: List<NotificationItem>,
  val max: Int
)

@Serializable
data class DbNotificationEpisode(
  val id: Int? = null,
  val name: String,
  @Serializable(with = InstantSerializer::class) val time: Instant? = null,
  @SerialName("chap_id") val chapId: String,
  @SerialName("created_at") @Serializable(with = InstantSerializer::class) val createdAt: Instant? = null
)

@Serializable
data class DbNotificationItem(
  val season: String,
  val name: String,
  val image: String? = null,
  val episodes: List<DbNotificationEpisode> = emptyList(),
  @SerialName("latest_chap_time") @Serializable(with = InstantSerializer::class) val latestChapTime: Instant? = null,
  @SerialName("created_at") @Serializable(with = InstantSerializer::class) val createdAt: Instant? = null
)

@Serializable
data class DbNotificationCount(
  @SerialName("notify_count") val notifyCount: Int,
  @SerialName("notify_chap_count") val notifyChapCount: Int
)
