package git.shin.animevsub.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.data.local.download.DownloadDao
import git.shin.animevsub.data.local.download.DownloadEntity
import git.shin.animevsub.data.local.download.DownloadStatus
import git.shin.animevsub.data.local.download.DownloadWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadDao: DownloadDao
) {
    val allDownloads: Flow<List<DownloadEntity>> = downloadDao.getAllDownloads()

    suspend fun startDownload(
        id: String,
        animeId: String,
        animeTitle: String,
        episodeTitle: String,
        thumbnail: String?,
        url: String
    ) {
        val download = DownloadEntity(
            id = id,
            animeId = animeId,
            animeTitle = animeTitle,
            episodeTitle = episodeTitle,
            thumbnail = thumbnail,
            url = url,
            status = DownloadStatus.QUEUED
        )
        downloadDao.insertDownload(download)

        val workData = Data.Builder()
            .putString("id", id)
            .putString("animeId", animeId)
            .putString("url", url)
            .putString("animeTitle", animeTitle)
            .putString("episodeTitle", episodeTitle)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(workData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    suspend fun deleteDownload(id: String) {
        val download = downloadDao.getDownloadById(id)
        download?.filePath?.let {
            val file = java.io.File(it)
            if (file.exists()) file.delete()
        }
        downloadDao.deleteDownload(id)
    }

    suspend fun getDownloadById(id: String): DownloadEntity? {
        return downloadDao.getDownloadById(id)
    }
}
