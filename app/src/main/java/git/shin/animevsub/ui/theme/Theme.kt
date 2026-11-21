package git.shin.animevsub.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

// ---------------------------------------------------------------------------
// Palette state
//
// The rest of the app references these colors by name (AccentMain, DarkSurface…)
// all over the place, so instead of threading a new theme object through ~30
// screens we expose them as getters backed by a snapshot state. Compose reads
// that state while composing, so flipping it recomposes every screen that uses
// the palette — that is how the Christmas theme recolors the whole app.
// ---------------------------------------------------------------------------

private val christmasPaletteEnabled = mutableStateOf(false)

internal fun setChristmasPalette(enabled: Boolean) {
  // Guarded so a recomposition that reads the palette cannot loop forever.
  if (christmasPaletteEnabled.value != enabled) {
    christmasPaletteEnabled.value = enabled
  }
}

internal fun isChristmasPalette(): Boolean = christmasPaletteEnabled.value

/** Whether the Christmas palette is currently applied (safe to read during composition). */
fun isChristmasThemeActive(): Boolean = christmasPaletteEnabled.value

private fun palette(base: Color, christmas: Color) = if (christmasPaletteEnabled.value) christmas else base

// Theme Colors
val DarkBackground: Color get() = palette(Color(0xFF141E33), Color(0xFF130F1E))
val DarkSurface: Color get() = palette(Color(0xFF1A2540), Color(0xFF1A1428))
val DarkCard: Color get() = palette(Color(0xFF1E2D4A), Color(0xFF211936))
val AccentMain: Color get() = palette(Color(0xFF8B85FF), ChristmasRose)
val AccentMainLight: Color get() = palette(Color(0xFFAFAAFF), Color(0xFFF591C1))
val TextPrimary: Color get() = palette(Color(0xFFFFFFFF), Color(0xFFFFF5FA))
val TextSecondary: Color get() = palette(Color(0xFFB0B0B0), Color(0xFFDDCCE2))
val TextGrey: Color get() = palette(Color(0xFF9E9E9E), Color(0xFFA896B8))
val ErrorColor: Color get() = palette(Color(0xFFCF6679), Color(0xFFFF8A9E))
val GithubBlue: Color get() = palette(Color(0xFF58A6FF), Color(0xFF8FD6B4))

// Season-neutral colors (same in both palettes)
val StarColor = Color(0xFFFFD700)
val SuccessColor = Color(0xFF4CAF50)
val MainColor = Color(0xFF00D639)

// Progress color for local-only history (sync disabled while logged in)
val LocalHistoryColor = Color(0xFFFF2D95)

// Christmas accents — pastel pink + teal + warm gold, following the
// "Christmas App (3D)" reference: rose is the primary action colour, teal is
// the tree, gold is only a highlight, and the festive cards themselves are
// light lavender/blush so the 3D artwork keeps its volume.
val ChristmasRose = Color(0xFFEB67A7)
val ChristmasRoseDeep = Color(0xFFD6488C)

/** Legacy name kept for the many call sites that use it as the CTA colour. */
val ChristmasRed = ChristmasRose
val ChristmasGreen = Color(0xFF37A98F)
val ChristmasGold = Color(0xFFFFC94D)
val ChristmasDeepPlum = Color(0xFF130F1E)

// Light surfaces for festive cards placed on the dark app background.
val ChristmasBlush = Color(0xFFF8DDF5)
val ChristmasMist = Color(0xFFEFF2FF)
val ChristmasCream = Color(0xFFFFF4FA)
val ChristmasInk = Color(0xFF1C1431)
val ChristmasInkSoft = Color(0xFF6F6685)
val ChristmasNavy = Color(0xFF102783)

/**
 * Hairline used to outline cards and chips while the Christmas theme is on:
 * a soft rose stroke reads as gift-wrap ribbon instead of a generic divider.
 */
val ChristmasHairline: Color
  get() = if (christmasPaletteEnabled.value) ChristmasRose.copy(alpha = 0.28f) else Color.Transparent

/** Rose → gold → teal ribbon brush reused by headers, dividers and badges. */
fun christmasRibbonBrush(): Brush = Brush.horizontalGradient(
  listOf(ChristmasRose, ChristmasGold, ChristmasGreen, ChristmasRose)
)

/** Vertical scrim used behind text placed over festive artwork. */
fun christmasPineScrim(alpha: Float = 0.92f): Brush = Brush.verticalGradient(
  listOf(
    ChristmasDeepPlum.copy(alpha = alpha * 0.15f),
    ChristmasDeepPlum.copy(alpha = alpha * 0.75f),
    ChristmasDeepPlum.copy(alpha = alpha)
  )
)

/**
 * Background of every festive card: the pale blush-to-lavender wash of the
 * reference design. Light cards are what make the full-colour 3D artwork read
 * as a scene rather than as stickers lost on a dark surface.
 */
fun christmasCardBrush(): Brush = Brush.linearGradient(
  listOf(ChristmasCream, ChristmasMist, ChristmasBlush.copy(alpha = 0.85f))
)

private fun darkScheme(christmas: Boolean) = darkColorScheme(
  primary = if (christmas) ChristmasRose else Color(0xFF8B85FF),
  onPrimary = if (christmas) ChristmasInk else Color.White,
  primaryContainer = if (christmas) ChristmasGreen else Color(0xFFAFAAFF),
  secondary = if (christmas) ChristmasGold else Color(0xFFAFAAFF),
  onSecondary = if (christmas) Color(0xFF3B2400) else Color.White,
  background = if (christmas) Color(0xFF130F1E) else Color(0xFF141E33),
  onBackground = if (christmas) Color(0xFFFFF5FA) else Color.White,
  surface = if (christmas) Color(0xFF1A1428) else Color(0xFF1A2540),
  onSurface = if (christmas) Color(0xFFFFF5FA) else Color.White,
  surfaceVariant = if (christmas) Color(0xFF211936) else Color(0xFF1E2D4A),
  onSurfaceVariant = if (christmas) Color(0xFFDDCCE2) else Color(0xFFB0B0B0),
  error = if (christmas) Color(0xFFFF8A9E) else Color(0xFFCF6679),
  onError = Color.White,
  outline = if (christmas) Color(0xFF4A3560) else Color(0xFF3A4A6B)
)

/** December / early January, the Christmas season. */
fun isChristmasSeason(date: Calendar = Calendar.getInstance()) = when (date.get(Calendar.MONTH)) {
  Calendar.DECEMBER -> true
  // Keep the decorations up for the first days of the new year.
  Calendar.JANUARY -> date.get(Calendar.DAY_OF_MONTH) <= 7
  else -> false
}

@Composable
fun AnimeVsubTheme(
  dynamicColor: Boolean = false,
  christmas: Boolean = false,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current

  // Apply the palette before composing the content so every screen sees it.
  setChristmasPalette(christmas)

  val colorScheme = when {
    christmas -> darkScheme(true)
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      dynamicDarkColorScheme(context)
    }
    else -> darkScheme(false)
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography(),
    content = content
  )
}
