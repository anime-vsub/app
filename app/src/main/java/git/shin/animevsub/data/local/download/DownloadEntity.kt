package git.shin.animevsub.data.local.download

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DownloadStatus {
    QUEUED, DOWNLOADING, CONVERTING, COMPLETED, FAILED
}

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String, // chapId or unique id
    val animeId: String,
    val animeTitle: String,
    val episodeTitle: String,
    val thumbnail: String?,
    val url: String,
    val filePath: String? = null,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val progress: Int = 0,
    val totalSize: Long = 0,
    val downloadedSize: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)
