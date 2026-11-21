package git.shin.animevsub.ui.components.christmas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.ChristmasGold
import git.shin.animevsub.ui.theme.ChristmasGreen
import git.shin.animevsub.ui.theme.ChristmasRed
import git.shin.animevsub.ui.theme.ChristmasRose
import git.shin.animevsub.ui.theme.isChristmasThemeActive

/**
 * Pine ring threaded with tiny rose and gold baubles, drawn around an icon so
 * a plain monochrome glyph reads as a Christmas ornament. Used by the bottom
 * navigation, where full-colour artwork would be illegible at icon size.
 */
@Composable
fun ChristmasIconRing(
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  size: Dp = 40.dp
) {
  androidx.compose.foundation.Canvas(modifier = modifier.size(size)) {
    val ringRadius = this.size.minDimension / 2f - 2.dp.toPx()
    val center = Offset(this.size.width / 2f, this.size.height / 2f)
    drawCircle(
      color = ChristmasGreen.copy(alpha = if (selected) 0.95f else 0.55f),
      radius = ringRadius,
      center = center,
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.6.dp.toPx())
    )
    // Baubles: four rose, four gold, alternating around the ring.
    repeat(8) { index ->
      val angle = Math.toRadians((index * 45).toDouble())
      val dotRadius = if (index % 2 == 0) 2.1.dp.toPx() else 1.6.dp.toPx()
      drawCircle(
        color = (if (index % 2 == 0) ChristmasRose else ChristmasGold)
          .copy(alpha = if (selected) 1f else 0.65f),
        radius = dotRadius,
        center = Offset(
          center.x + (ringRadius * kotlin.math.cos(angle)).toFloat(),
          center.y + (ringRadius * kotlin.math.sin(angle)).toFloat()
        )
      )
    }
  }
}

/**
 * Dim gold hairline that turns a plain card into a "wrapped gift" panel while
 * the Christmas theme is on. The modifier is a no-op otherwise, so screens can
 * apply it unconditionally.
 */
fun Modifier.christmasCardOutline(
  cornerRadius: Dp = 12.dp,
  width: Dp = 1.dp
): Modifier = if (isChristmasThemeActive()) {
  this.border(
    width = width,
    color = ChristmasGold.copy(alpha = 0.22f),
    shape = RoundedCornerShape(cornerRadius)
  )
} else {
  this
}

/**
 * Circular ornament that frames a shortcut icon. Outside the season it renders
 * just the icon, so callers keep one code path.
 */
@Composable
fun ChristmasOrnamentIcon(
  painter: Painter,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  tint: Color? = null
) {
  val active = isChristmasThemeActive()
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(
        if (active) {
          Brush.verticalGradient(
            listOf(Color(0xFF22402F), Color(0xFF16301F))
          )
        } else {
          Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
        }
      )
      .then(
        if (active) {
          Modifier
            .border(1.dp, ChristmasGold.copy(alpha = 0.45f), CircleShape)
            .padding(7.dp)
        } else {
          Modifier
        }
      ),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painter = painter,
      contentDescription = contentDescription,
      modifier = Modifier.size(if (active) size - 16.dp else size),
      colorFilter = tint?.let { ColorFilter.tint(it) }
    )
  }
}

/**
 * Thin tricolour ribbon, used as a separator under app bars and section headers.
 */
@Composable
fun ChristmasRibbon(
  modifier: Modifier = Modifier,
  thickness: Dp = 2.dp,
  alpha: Float = 0.9f
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(thickness)
      .background(
        Brush.horizontalGradient(
          listOf(
            ChristmasRed.copy(alpha = alpha),
            ChristmasGold.copy(alpha = alpha),
            ChristmasGreen.copy(alpha = alpha),
            ChristmasRed.copy(alpha = alpha)
          )
        )
      )
  )
}

/**
 * Bulb strip used to crown headers. Delegates to the animated string in
 * [ChristmasLightStrip] so static and animated call sites stay consistent.
 */
@Composable
fun ChristmasLightString(
  modifier: Modifier = Modifier,
  bulbCount: Int = 9
) {
  if (!isChristmasThemeActive()) return
  ChristmasLightStrip(modifier = modifier, bulbCount = bulbCount)
}

/** Holly sticker + label pair used to crown a sheet or dialog title. */
@Composable
fun ChristmasTitleAccent(
  text: String,
  modifier: Modifier = Modifier
) {
  if (!isChristmasThemeActive()) return
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Image(
      painter = androidx.compose.ui.res.painterResource(R.drawable.xm_christmas_ornament_star),
      contentDescription = null,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      color = ChristmasGold,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}
