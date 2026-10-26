package git.shin.animevsub.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import git.shin.animevsub.data.local.AppDatabase
import git.shin.animevsub.data.local.dao.AnimeCacheDao
import git.shin.animevsub.data.local.dao.LocalHistoryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "animevsub.db").build()

  @Provides
  fun provideLocalHistoryDao(database: AppDatabase): LocalHistoryDao = database.localHistoryDao()

  @Provides
  fun provideAnimeCacheDao(database: AppDatabase): AnimeCacheDao = database.animeCacheDao()
}
