package git.shin.animevsub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_cache")
data class AnimeCacheEntity(
  @PrimaryKey val animeId: String,
  val detailJson: String,
  val chaptersJson: String?,
  val updatedAt: Long
)
