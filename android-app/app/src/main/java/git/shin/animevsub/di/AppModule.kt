package git.shin.animevsub.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.remote.AnimeApi
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideSupabaseClient(): SupabaseClient {
    return createSupabaseClient(
      supabaseUrl = "https://ctwwltbkwksgnispcjmq.supabase.co",
      supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN0d3dsdGJrd2tzZ25pc3Bjam1xIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjAxNjM5ODksImV4cCI6MjAzNTczOTk4OX0.Dva9EPqy4P0KFYLAGpFqFoMBH4I_yz0VWnGny0uA-8U"
    ) {
      install(Postgrest)
      install(Auth)
    }
  }

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
  fun provideAnimeApi(client: OkHttpClient, json: Json, apiStorage: ApiStorage): AnimeApi {
    return AnimeApi(client, json, apiStorage)
  }

  @Provides
  @Singleton
  fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
    return PreferencesManager(context)
  }

  @Provides
  @Singleton
  fun provideApiStorage(@ApplicationContext context: Context): ApiStorage {
    return ApiStorage(context)
  }
}
