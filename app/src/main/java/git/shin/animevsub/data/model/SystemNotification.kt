package git.shin.animevsub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
enum class SystemNotificationType {
  @SerialName("general")
  GENERAL,

  @SerialName("app_update")
  APP_UPDATE,

  @SerialName("maintenance")
  MAINTENANCE,

  @SerialName("promotion")
  PROMOTION,

  @SerialName("feature")
  FEATURE,

  @SerialName("bugfix")
  BUGFIX,

  @SerialName("security")
  SECURITY
}

@Serializable
data class SystemNotification(
  val id: String,
  val title: String,
  val body: String,
  val type: SystemNotificationType = SystemNotificationType.GENERAL,
  val imageUrl: String? = null,
  val deepLink: String? = null,
  val animeId: String? = null,
  val chapterId: String? = null,
  @Serializable(with = InstantSerializer::class) val createdAt: Instant = Instant.now(),
  val isRead: Boolean = false
)
