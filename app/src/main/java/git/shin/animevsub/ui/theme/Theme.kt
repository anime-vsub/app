package git.shin.animevsub.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Theme Colors
val DarkBackground = Color(0xFF141E33)
val DarkSurface = Color(0xFF1A2540)
val DarkCard = Color(0xFF1E2D4A)
val AccentMain = Color(0xFF6C63FF)
val AccentMainLight = Color(0xFF8B85FF)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B0)
val TextGrey = Color(0xFF9E9E9E)
val StarColor = Color(0xFFFFD700)
val ErrorColor = Color(0xFFCF6679)
val SuccessColor = Color(0xFF4CAF50)
val GithubBlue = Color(0xFF58A6FF)

// Popular Colors
val MainColor = Color(0xFF00D639)

private val DarkColorScheme = darkColorScheme(
  primary = AccentMain,
  onPrimary = Color.White,
  primaryContainer = AccentMainLight,
  secondary = AccentMainLight,
  onSecondary = Color.White,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkCard,
  onSurfaceVariant = TextSecondary,
  error = ErrorColor,
  onError = Color.White,
  outline = Color(0xFF3A4A6B)
)

@Composable
fun AnimeVsubTheme(
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      dynamicDarkColorScheme(context)
    }
    else -> DarkColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography(),
    content = content
  )
}
