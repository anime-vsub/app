package git.shin.animevsub.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import git.shin.animevsub.data.local.entity.AnimeSyncModeEntity
import git.shin.animevsub.data.local.entity.LocalProgressEntity

@Dao
interface LocalHistoryDao {

  @Query("SELECT * FROM local_progress WHERE seasonId = :seasonId")
  suspend fun progressForSeason(seasonId: String): List<LocalProgressEntity>

  @Query("SELECT * FROM local_progress WHERE seasonId = :seasonId AND chapId = :chapId LIMIT 1")
  suspend fun progressForChapter(seasonId: String, chapId: String): LocalProgressEntity?

  @Query("SELECT chapId FROM local_progress WHERE seasonId = :seasonId ORDER BY updatedAt DESC LIMIT 1")
  suspend fun lastChapId(seasonId: String): String?

  @Upsert
  suspend fun upsert(entity: LocalProgressEntity)

  @Query(
    """
    SELECT p.* FROM local_progress p
    WHERE NOT EXISTS (
      SELECT 1 FROM local_progress x
      WHERE x.seasonId = p.seasonId AND x.updatedAt > p.updatedAt
    )
    ORDER BY p.updatedAt DESC
    LIMIT :limit OFFSET :offset
    """
  )
  suspend fun latestPerSeason(limit: Int, offset: Int): List<LocalProgressEntity>

  @Query(
    """
    SELECT p.* FROM local_progress p
    WHERE p.seasonId IN (:seasonIds)
      AND NOT EXISTS (
        SELECT 1 FROM local_progress x
        WHERE x.seasonId = p.seasonId AND x.updatedAt > p.updatedAt
      )
    ORDER BY p.updatedAt DESC
    """
  )
  suspend fun latestForSeasons(seasonIds: List<String>): List<LocalProgressEntity>

  @Query("SELECT * FROM local_progress WHERE seasonId = :seasonId ORDER BY updatedAt DESC LIMIT 1")
  suspend fun latestForSeason(seasonId: String): LocalProgressEntity?

  @Query("DELETE FROM local_progress WHERE seasonId = :seasonId")
  suspend fun deleteSeason(seasonId: String)

  @Query("SELECT mode FROM anime_sync_mode WHERE animeId = :animeId LIMIT 1")
  suspend fun syncMode(animeId: String): Int?

  @Upsert
  suspend fun upsertSyncMode(entity: AnimeSyncModeEntity)

  @Query("SELECT animeId FROM anime_sync_mode WHERE mode = 2")
  suspend fun localOnlyAnimeIds(): List<String>
}
