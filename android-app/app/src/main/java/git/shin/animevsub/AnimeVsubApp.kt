package git.shin.animevsub

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AnimeVsubApp : Application(), ImageLoaderFactory {
  override fun newImageLoader(): ImageLoader {
    return ImageLoader.Builder(this)
      .components {
        if (Build.VERSION.SDK_INT >= 28) {
          add(ImageDecoderDecoder.Factory())
        } else {
          add(GifDecoder.Factory())
        }
      }
      .build()
  }
}
