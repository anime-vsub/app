package git.shin.animevsub.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.remote.AnimeApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideJson(): Json {
    return Json {
      ignoreUnknownKeys = true
      coerceInputValues = true
      encodeDefaults = true
      isLenient = true
    }
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .followRedirects(true)
      .followSslRedirects(true)
      .build()
  }

  @Provides
  @Singleton
  fun provideAnimeApi(client: OkHttpClient, json: Json): AnimeApi {
    return AnimeApi(client, json)
  }

  @Provides
  @Singleton
  fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
    return PreferencesManager(context)
  }
}
