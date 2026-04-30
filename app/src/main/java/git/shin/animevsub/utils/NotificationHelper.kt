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
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationHelper(private val context: Context) {
  companion object {
    private const val CHANNEL_ID = "anime_updates"
    private const val CHANNEL_NAME = "Anime Updates"
  }

  init {
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

  fun showNotification(
    title: String,
    message: String,
    animeId: String? = null,
    chapterId: String? = null,
    imageUrl: String? = null
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

    val intent = Intent(context, MainActivity::class.java).apply {
      if (animeId != null) {
        action = "PLAY_ANIME"
        putExtra("animeId", animeId)
        putExtra("chapterId", chapterId)
      }
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context, System.currentTimeMillis().toInt(), intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationId = System.currentTimeMillis().toInt()

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification)
      .setContentTitle(title)
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
        AnimeApi.getHeaders(url).forEach { (key, value) ->
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
