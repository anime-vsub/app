package git.shin.animevsub.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import git.shin.animevsub.MainActivity
import git.shin.animevsub.R
import git.shin.animevsub.data.remote.api.AnimeDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationHelper(private val context: Context) {
  companion object {
    private const val CHANNEL_ID = "anime_updates"
    private const val CHANNEL_NAME = "Anime Updates"
    private const val SYSTEM_CHANNEL_ID = "system_notifications"
    private const val SYSTEM_CHANNEL_NAME = "System Notifications"
  }

  init {
    createNotificationChannel()
    createSystemNotificationChannel()
  }

  private fun createNotificationChannel() {
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

  private fun createSystemNotificationChannel() {
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(SYSTEM_CHANNEL_ID, SYSTEM_CHANNEL_NAME, importance).apply {
      description = "Important system notifications and app updates"
      enableLights(true)
      enableVibration(true)
      setSound(
        android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION),
        android.media.AudioAttributes.Builder()
          .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
          .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .build()
      )
    }
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

  fun showNotification(
    title: String,
    message: String,
    animeId: String? = null,
    chapterId: String? = null,
    imageUrl: String? = null,
    deepLink: String? = null,
    notificationType: String? = null
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ActivityCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    val channelId = if (notificationType == "app_update" || notificationType == "maintenance" || notificationType == "security") {
      SYSTEM_CHANNEL_ID
    } else {
      CHANNEL_ID
    }

    val intent = Intent(context, MainActivity::class.java).apply {
      action = "OPEN_FROM_NOTIFICATION"
      if (animeId != null) {
        putExtra("animeId", animeId)
        putExtra("chapterId", chapterId)
      }
      if (deepLink != null) {
        putExtra("deep_link", deepLink)
      }
      if (notificationType != null) {
        putExtra("notification_type", notificationType)
      }
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context, System.currentTimeMillis().toInt(), intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationId = System.currentTimeMillis().toInt()

    val builder = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(R.drawable.ic_notification)
      .setContentTitle(title)
      .setContentText(message)
      .setPriority(if (channelId == SYSTEM_CHANNEL_ID) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    if (!imageUrl.isNullOrEmpty()) {
      val scope = CoroutineScope(Dispatchers.IO)
      scope.launch {
        val bitmap = fetchBitmap(imageUrl)
        withContext(Dispatchers.Main) {
          if (bitmap != null) {
            builder.setLargeIcon(bitmap)
            builder.setStyle(
              NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null as Bitmap?)
            )
          }
          val notificationManager = NotificationManagerCompat.from(context)
          notificationManager.notify(notificationId, builder.build())
        }
      }
    } else {
      val notificationManager = NotificationManagerCompat.from(context)
      notificationManager.notify(notificationId, builder.build())
    }
  }

  private suspend fun fetchBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
    val loader = Coil.imageLoader(context)
    val request = ImageRequest.Builder(context)
      .data(url)
      .apply {
        AnimeDataSource.getHeaders(url).forEach { (key, value) ->
          addHeader(key, value)
        }
      }
      .allowHardware(false)
      .build()
    val result = loader.execute(request)
    if (result is SuccessResult) {
      (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
    } else {
      null
    }
  }
}
