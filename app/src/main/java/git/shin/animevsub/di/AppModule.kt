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
import git.shin.animevsub.data.remote.DynamicDns
import git.shin.animevsub.data.remote.WebViewCookieJar
import git.shin.animevsub.data.remote.api.AnimeDataSource
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
import git.shin.animevsub.utils.CloudflareManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
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
  @Named(DNS_BOOTSTRAP_CLIENT)
  fun provideDnsBootstrapClient(@ApplicationContext context: Context): OkHttpClient = OkHttpClient.Builder()
    // Every API and media request resolves through DoH, so this client sits in
    // front of the whole app. Keep its timeouts short: when the resolver is
    // blocked on the current network (common on some carriers), a 15s stall
    // here delays the first segment by 15s before DynamicDns can fall back to
    // the system resolver. The cache keeps answers for their TTL so repeat
    // lookups of the same CDN host are free.
    .connectTimeout(4, TimeUnit.SECONDS)
    .readTimeout(4, TimeUnit.SECONDS)
    .callTimeout(6, TimeUnit.SECONDS)
    .cache(Cache(File(context.cacheDir, "dns"), 2L * 1024 * 1024))
    .build()

  @Provides
  @Singleton
  fun provideDynamicDns(
    prefs: PreferencesManager,
    @Named(DNS_BOOTSTRAP_CLIENT) dnsBootstrapClient: OkHttpClient
  ): DynamicDns = DynamicDns(prefs, dnsBootstrapClient)

  @Provides
  @Singleton
  fun provideOkHttpClient(dynamicDns: DynamicDns): OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .followRedirects(false)
    .followSslRedirects(true)
    .cookieJar(WebViewCookieJar())
    .dns(dynamicDns)
    .build()

  @Provides
  @Singleton
  fun provideAnimeDataSource(
    client: OkHttpClient,
    json: Json,
    apiStorage: ApiStorage,
    cloudflareManager: CloudflareManager
  ): AnimeDataSource = AnimeApi(client, json, apiStorage, cloudflareManager)

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

  private const val DNS_BOOTSTRAP_CLIENT = "dnsBootstrapClient"
}
