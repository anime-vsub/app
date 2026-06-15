package git.shin.animevsub

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import dagger.hilt.android.HiltAndroidApp
import git.shin.animevsub.data.remote.WebViewCookieJar
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class AnimeVsubApp :
  Application(),
  ImageLoaderFactory,
  Configuration.Provider {

  companion object {
    var instance: AnimeVsubApp? = null
      private set
  }

  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  override fun onCreate() {
    super.onCreate()
    instance = this
    AppIntegrityChecker.checkIntegrity(this)
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()

  override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
    .okHttpClient {
      OkHttpClient.Builder()
        .cookieJar(WebViewCookieJar())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    }
    .components {
      if (Build.VERSION.SDK_INT >= 28) {
        add(ImageDecoderDecoder.Factory())
      } else {
        add(GifDecoder.Factory())
      }
    }
    .build()
}
