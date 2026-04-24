package git.shin.animevsub.utils

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

object ResponsiveUtils {
  @Composable
  fun calculateGridColumns(windowSizeClass: WindowSizeClass): Int {
//    val configuration = LocalConfiguration.current
    val widthClass = windowSizeClass.widthSizeClass

    return when {
      widthClass == WindowWidthSizeClass.Compact -> 3
      // && configuration.screenWidthDp >= 600
      widthClass == WindowWidthSizeClass.Medium -> 4
      // && configuration.screenWidthDp >= 1200
      widthClass == WindowWidthSizeClass.Expanded -> 6
      else -> 8
    }
  }
}
