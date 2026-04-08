package git.shin.animevsub.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import git.shin.animevsub.BuildConfig
import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.remote.AnimeApi
import git.shin.animevsub.utils.CloudflareManager
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
      supabaseUrl = BuildConfig.SUPABASE_URL,
      supabaseKey = BuildConfig.SUPABASE_KEY
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
  fun provideAnimeApi(
    client: OkHttpClient,
    json: Json,
    apiStorage: ApiStorage,
    cloudflareManager: CloudflareManager
  ): AnimeApi {
    return AnimeApi(client, json, apiStorage, cloudflareManager)
  }

  @Provides
  @Singleton
  fun provideCloudflareManager(apiStorage: ApiStorage): CloudflareManager {
    return CloudflareManager(apiStorage)
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

  @Provides
  @Singleton
  fun provideFirebaseAnalytics(): FirebaseAnalytics {
    return Firebase.analytics
  }
}
