package git.shin.animevsub.data.local.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFprobe
import com.arthenica.mobileffmpeg.Statistics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import git.shin.animevsub.MainActivity
import git.shin.animevsub.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadDao: DownloadDao
) : CoroutineWorker(context, workerParams) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "download_channel"
    private val notificationId = UUID.randomUUID().hashCode()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val id = inputData.getString("id") ?: return@withContext Result.failure()
        val url = inputData.getString("url") ?: return@withContext Result.failure()
        val animeId = inputData.getString("animeId") ?: return@withContext Result.failure()
        val animeTitle = inputData.getString("animeTitle") ?: "Anime"
        val episodeTitle = inputData.getString("episodeTitle") ?: "Episode"

        Log.d("DownloadWorker", "Starting download: $id - $animeTitle - $episodeTitle - $url")

        val download = downloadDao.getDownloadById(id) ?: return@withContext Result.failure()

        // Register log callback to see FFmpeg output
        Config.enableLogCallback { log ->
            Log.d("FFmpegLog", "[${log.level}] ${log.text}")
        }

        // Đăng ký xử lý hủy tác vụ khi Coroutine bị stop
        coroutineContext[Job]?.invokeOnCompletion {
            if (it is CancellationException) {
                FFmpeg.cancel()
            }
        }

        createNotificationChannel()
        setForeground(createForegroundInfo(id, animeId, animeTitle, episodeTitle, 0))

        downloadDao.updateDownload(download.copy(status = DownloadStatus.DOWNLOADING, progress = 0))

        val outputDir = File(context.getExternalFilesDir(null), "downloads")
        if (!outputDir.exists()) outputDir.mkdirs()

        val outputFile = File(outputDir, "${id}.mp4")
        val tempFile = File(outputDir, "${id}_temp.mp4")

        if (tempFile.exists()) tempFile.delete()

        // Get duration using FFprobe
        var durationMs = 0L
        try {
            val mediaInformation = FFprobe.getMediaInformation(url)
            if (mediaInformation != null) {
                // Chuyển đổi sang Double trước khi tính toán để tránh lỗi BigDecimal
                durationMs = (mediaInformation.duration.toDouble() * 1000).toLong()
                Log.d("DownloadWorker", "Media duration: $durationMs ms")
            } else {
                Log.w("DownloadWorker", "Could not get media information for $url")
            }
        } catch (e: Exception) {
            Log.e("DownloadWorker", "FFprobe error", e)
        }

        // FFmpeg command to download and remux
        val command = "-i \"$url\" -c copy -y \"${tempFile.absolutePath}\""
        Log.d("DownloadWorker", "Executing FFmpeg command: $command")

        // Set statistics callback for progress
        Config.resetStatistics()
        Config.enableStatisticsCallback { statistics: Statistics ->
            if (durationMs > 0) {
                val progress = (statistics.time.toDouble() / durationMs * 100).toInt().coerceIn(0, 100)
                Log.d("DownloadWorker", "Progress: $progress% (time: ${statistics.time}, duration: $durationMs)")
                downloadDao.updateProgressSync(id, DownloadStatus.DOWNLOADING, progress)
                updateNotification(id, animeId, animeTitle, episodeTitle, progress, false)
            } else {
                Log.d("DownloadWorker", "Downloading... time: ${statistics.time} (Duration unknown)")
                // If duration is unknown, we can't show % but we know it's working
                downloadDao.updateProgressSync(id, DownloadStatus.DOWNLOADING, 0)
            }
        }

        val rc = FFmpeg.execute(command)
        Log.d("DownloadWorker", "FFmpeg finished with return code: $rc")
        
        // Clear callbacks to avoid leaks
        Config.enableStatisticsCallback(null)
        Config.enableLogCallback(null)

        return@withContext when (rc) {
            Config.RETURN_CODE_SUCCESS -> {
                Log.d("DownloadWorker", "Download successful, renaming temp file")
                if (tempFile.exists() && tempFile.renameTo(outputFile)) {
                    downloadDao.updateDownloadSync(
                        download.copy(
                            status = DownloadStatus.COMPLETED,
                            filePath = outputFile.absolutePath,
                            progress = 100
                        )
                    )
                    updateNotification(id, animeId, animeTitle, episodeTitle, 100, true)
                    Result.success()
                } else {
                    Log.e("DownloadWorker", "Failed to rename temp file or temp file doesn't exist")
                    Result.failure()
                }
            }
            Config.RETURN_CODE_CANCEL -> {
                Log.d("DownloadWorker", "Download cancelled")
                downloadDao.updateDownloadSync(download.copy(status = DownloadStatus.FAILED, errorMessage = "Cancelled"))
                if (tempFile.exists()) tempFile.delete()
                Result.failure()
            }
            else -> {
                Log.e("DownloadWorker", "Download failed with rc: $rc")
                downloadDao.updateDownloadSync(
                    download.copy(
                        status = DownloadStatus.FAILED,
                        errorMessage = "FFmpeg failed with rc: $rc"
                    )
                )
                updateNotification(id, animeId, animeTitle, episodeTitle, 0, false, "Failed")
                Result.failure()
            }
        }
    }

    private fun DownloadDao.updateDownloadSync(entity: DownloadEntity) {
        runBlocking { updateDownload(entity) }
    }

    private fun DownloadDao.updateProgressSync(id: String, status: DownloadStatus, progress: Int) {
        runBlocking { updateProgress(id, status, progress) }
    }

    private fun createForegroundInfo(
        id: String,
        animeId: String,
        animeTitle: String,
        episodeTitle: String,
        progress: Int
    ): ForegroundInfo {
        val cancelIntent = WorkManager.getInstance(context).createCancelPendingIntent(this.id)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.download_downloading) + " $animeTitle")
            .setContentText(episodeTitle)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setProgress(100, progress, false)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.download_cancel), cancelIntent)
            .build()
        return ForegroundInfo(notificationId, notification)
    }

    private fun updateNotification(
        id: String,
        animeId: String,
        animeTitle: String,
        episodeTitle: String,
        progress: Int,
        completed: Boolean,
        error: String? = null
    ) {
        val title = when {
            completed -> context.getString(R.string.download_completed)
            error != null -> context.getString(R.string.download_failed)
            else -> context.getString(R.string.download_downloading) + " $animeTitle"
        }
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(error ?: episodeTitle)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, progress, !completed && error == null)
            .setOngoing(!completed && error == null)

        if (completed) {
            val deepLinkIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("animevsub://detail/$animeId?chapterId=$id"),
                context,
                MainActivity::class.java
            )
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                deepLinkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
            builder.setAutoCancel(true)
            builder.addAction(android.R.drawable.ic_media_play, context.getString(R.string.download_view), pendingIntent)
        } else if (error == null) {
            val cancelIntent = WorkManager.getInstance(context).createCancelPendingIntent(this.id)
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.download_cancel), cancelIntent)
        }

        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}
