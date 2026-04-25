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
import javax.inject.Inject

@HiltAndroidApp
class AnimeVsubApp :
  Application(),
  ImageLoaderFactory,
  Configuration.Provider {

  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  override fun onCreate() {
    super.onCreate()
    AppIntegrityChecker.checkIntegrity(this)
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()

  override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
    .components {
      if (Build.VERSION.SDK_INT >= 28) {
        add(ImageDecoderDecoder.Factory())
      } else {
        add(GifDecoder.Factory())
      }
    }
    .build()
}
