package git.shin.animevsub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_sync_mode")
data class AnimeSyncModeEntity(
  @PrimaryKey val animeId: String,
  val mode: Int
)
