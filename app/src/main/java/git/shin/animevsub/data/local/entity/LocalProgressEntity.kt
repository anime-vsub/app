package git.shin.animevsub.data.local.entity

import androidx.room.Entity

@Entity(tableName = "local_progress", primaryKeys = ["seasonId", "chapId"])
data class LocalProgressEntity(
  val seasonId: String,
  val chapId: String,
  val animeName: String,
  val poster: String,
  val seasonName: String,
  val chapName: String,
  val cur: Double,
  val dur: Double,
  val updatedAt: Long
)
