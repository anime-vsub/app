package eu.org.animevsub.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
