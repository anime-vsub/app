package git.shin.animevsub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import git.shin.animevsub.data.local.dao.AnimeCacheDao
import git.shin.animevsub.data.local.dao.LocalHistoryDao
import git.shin.animevsub.data.local.entity.AnimeCacheEntity
import git.shin.animevsub.data.local.entity.AnimeSyncModeEntity
import git.shin.animevsub.data.local.entity.LocalProgressEntity

@Database(
  entities = [
    LocalProgressEntity::class,
    AnimeSyncModeEntity::class,
    AnimeCacheEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun localHistoryDao(): LocalHistoryDao
  abstract fun animeCacheDao(): AnimeCacheDao
}
