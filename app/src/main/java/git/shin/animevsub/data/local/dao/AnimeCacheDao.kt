package git.shin.animevsub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import git.shin.animevsub.data.local.entity.AnimeCacheEntity

@Dao
interface AnimeCacheDao {

  @Query("SELECT * FROM anime_cache WHERE animeId = :animeId LIMIT 1")
  suspend fun get(animeId: String): AnimeCacheEntity?

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertIfAbsent(entity: AnimeCacheEntity)

  @Query("UPDATE anime_cache SET detailJson = :json, updatedAt = :updatedAt WHERE animeId = :animeId")
  suspend fun updateDetail(animeId: String, json: String, updatedAt: Long)

  @Query("UPDATE anime_cache SET chaptersJson = :json, updatedAt = :updatedAt WHERE animeId = :animeId")
  suspend fun updateChapters(animeId: String, json: String, updatedAt: Long)
}
