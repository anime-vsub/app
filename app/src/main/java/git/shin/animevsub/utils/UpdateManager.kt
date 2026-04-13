package git.shin.animevsub.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.BuildConfig
import git.shin.animevsub.data.model.GitHubRelease
import git.shin.animevsub.data.model.UpdateInfo
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

@Singleton
class UpdateManager @Inject constructor(
  @ApplicationContext private val context: Context,
  private val client: OkHttpClient,
  private val json: Json,
  private val cloudflareManager: CloudflareManager
) {
  private val githubRepo = "anime-vsub/app"
  private val activeCheckUrl =
    "https://raw.githubusercontent.com/anime-vsub/app/refs/heads/main/native-active"

  suspend fun checkAppActive(): Result<Boolean> = withContext(Dispatchers.IO) {
    try {
      val request = Request.Builder()
        .url(activeCheckUrl)
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful) {
        val body = response.body.string().trim()
        Result.success(body.isEmpty())
      } else {
        Result.success(false)
      }
    } catch (e: Exception) {
      // In case of network error, we might want to allow the app to run if it was previously checked
      // or block it. Usually, maintenance check should be resilient.
      Result.failure(e)
    }
  }

  suspend fun checkForUpdate(): Result<UpdateInfo> = withContext(Dispatchers.IO) {
    try {
      val request = Request.Builder()
        .url("https://api.github.com/repos/$githubRepo/releases/latest")
        .header("Accept", "application/vnd.github.v3+json")
        .build()

      val response = cloudflareManager.fetch(client, request)
      if (!response.isSuccessful) return@withContext Result.failure(Exception("Failed to fetch release: ${response.code}"))

      val body = response.body.string()
      val release = json.decodeFromString<GitHubRelease>(body)

      val latestVersion = release.tagName.removePrefix("v")

      val isNewer = isVersionNewer(latestVersion)
      val apkAsset = release.assets.find { it.name == "app-release-signed.apk" }
        ?: release.assets.find { it.name.endsWith(".apk") }

      Result.success(
        UpdateInfo(
          version = latestVersion,
          description = release.body,
          downloadUrl = apkAsset?.downloadUrl ?: "",
          isNewer = isNewer
        )
      )
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun isVersionNewer(latest: String): Boolean {
    val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }
    val currentParts = BuildConfig.VERSION_NAME.split(".").mapNotNull { it.toIntOrNull() }

    for (i in 0 until minOf(latestParts.size, currentParts.size)) {
      if (latestParts[i] > currentParts[i]) return true
      if (latestParts[i] < currentParts[i]) return false
    }
    return latestParts.size > currentParts.size
  }

  fun downloadAndInstall(url: String, fileName: String) {
    val destination = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
    if (destination.exists()) destination.delete()

    val request = DownloadManager.Request(url.toUri())
      .setTitle(context.getString(git.shin.animevsub.R.string.update_downloading_title))
      .setDescription(context.getString(git.shin.animevsub.R.string.update_downloading_description))
      .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
      .setDestinationUri(Uri.fromFile(destination))
      .setAllowedOverMetered(true)
      .setAllowedOverRoaming(true)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = downloadManager.enqueue(request)

    val onComplete = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        if (id == downloadId) {
          installApk(destination)
          try {
            context.unregisterReceiver(this)
          } catch (e: Exception) {
            print(e)
            // Already unregistered
          }
        }
      }
    }

    val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    ContextCompat.registerReceiver(
      context,
      onComplete,
      filter,
      ContextCompat.RECEIVER_EXPORTED
    )
  }

  private fun installApk(file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(uri, "application/vnd.android.package-archive")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  }
}
