package git.shin.animevsub.ui.utils

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

data class ScreenUtils(
  val isLandscape: Boolean,
  val isTV: Boolean
)

@Composable
fun rememberScreenState(): ScreenUtils {
  val configuration = LocalConfiguration.current
  return remember(configuration) {
    ScreenUtils(
      isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
      isTV = (configuration.uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION
    )
  }
}
