package git.shin.animevsub.di

import android.content.Context
import androidx.room.Room
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
import git.shin.animevsub.data.local.AppDatabase
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.local.download.DownloadDao
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
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
  fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
  ) {
    install(Postgrest)
    install(Auth)
  }

  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    encodeDefaults = true
    isLenient = true
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .followRedirects(true)
    .followSslRedirects(true)
    .cookieJar(git.shin.animevsub.data.remote.WebViewCookieJar())
    .build()

  @Provides
  @Singleton
  fun provideAnimeApi(
    client: OkHttpClient,
    json: Json,
    apiStorage: ApiStorage,
    cloudflareManager: CloudflareManager
  ): AnimeApi = AnimeApi(client, json, apiStorage, cloudflareManager)

  @Provides
  @Singleton
  fun provideCloudflareManager(@ApplicationContext context: Context): CloudflareManager = CloudflareManager(context)

  @Provides
  @Singleton
  fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager = PreferencesManager(context)

  @Provides
  @Singleton
  fun provideApiStorage(@ApplicationContext context: Context): ApiStorage = ApiStorage(context)

  @Provides
  @Singleton
  fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

  @Provides
  @Singleton
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      "anime_vsub.db"
    ).build()
  }

  @Provides
  @Singleton
  fun provideDownloadDao(database: AppDatabase): DownloadDao = database.downloadDao()
}
