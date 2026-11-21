package git.shin.animevsub.ui.components.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Tracks whether the video player is currently in immersive full screen.
 *
 * The full screen flag lives deep inside the detail screen, but the app shell
 * needs it too: overlays drawn above the nav host (seasonal snowfall, for one)
 * must disappear while the player takes over the whole display. Exposing the
 * flag through this tiny holder avoids threading callbacks through navigation.
 */
object PlayerFullscreenState {
  var isFullScreen by mutableStateOf(false)
    private set

  fun update(fullScreen: Boolean) {
    if (isFullScreen != fullScreen) {
      isFullScreen = fullScreen
    }
  }
}
