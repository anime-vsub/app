package git.shin.animevsub.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import git.shin.animevsub.data.local.SystemNotificationStore
import git.shin.animevsub.data.model.SystemNotification
import git.shin.animevsub.data.model.SystemNotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class AnimeVsubFirebaseMessagingService : FirebaseMessagingService() {

  @Inject
  lateinit var notificationStore: SystemNotificationStore

  private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  companion object {
    private const val TAG = "FCMService"

    fun subscribeToTopics() {
      FirebaseMessaging.getInstance().subscribeToTopic("all_users")
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Log.d(TAG, "Subscribed to all_users topic")
          } else {
            Log.e(TAG, "Failed to subscribe to all_users topic", task.exception)
          }
        }
      FirebaseMessaging.getInstance().subscribeToTopic("app_updates")
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Log.d(TAG, "Subscribed to app_updates topic")
          } else {
            Log.e(TAG, "Failed to subscribe to app_updates topic", task.exception)
          }
        }
    }
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)

    val data = remoteMessage.data
    if (data.isEmpty()) return

    val title = data["title"] ?: getString(git.shin.animevsub.R.string.app_name)
    val body = data["body"] ?: ""
    val typeString = data["type"] ?: "general"
    val imageUrl = data["image"]
    val deepLink = data["deep_link"]
    val animeId = data["anime_id"]
    val chapterId = data["chapter_id"]

    val notificationType = runCatching {
      SystemNotificationType.valueOf(typeString.uppercase())
    }.getOrDefault(SystemNotificationType.GENERAL)

    val notificationId = data["id"] ?: UUID.randomUUID().toString()

    val systemNotification = SystemNotification(
      id = notificationId,
      title = title,
      body = body,
      type = notificationType,
      imageUrl = imageUrl,
      deepLink = deepLink,
      animeId = animeId,
      chapterId = chapterId,
      createdAt = Instant.now(),
      isRead = false
    )

    serviceScope.launch {
      notificationStore.save(systemNotification)
    }

    val helper = NotificationHelper(this@AnimeVsubFirebaseMessagingService)
    helper.showNotification(
      title = title,
      message = body,
      animeId = animeId,
      chapterId = chapterId,
      imageUrl = imageUrl,
      deepLink = deepLink,
      notificationType = typeString
    )
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)

    subscribeToTopics()
  }
}
