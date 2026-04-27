package git.shin.animevsub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import git.shin.animevsub.data.local.download.DownloadDao
import git.shin.animevsub.data.local.download.DownloadEntity

@Database(entities = [DownloadEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
}
