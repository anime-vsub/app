package git.shin.animevsub.ui.components.christmas

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.ChristmasBlush
import git.shin.animevsub.ui.theme.ChristmasGold
import git.shin.animevsub.ui.theme.ChristmasGreen
import git.shin.animevsub.ui.theme.ChristmasRed
import git.shin.animevsub.ui.theme.ChristmasRose
import git.shin.animevsub.ui.theme.christmasCardBrush

/** Bauble, star and teardrop ornaments, the standard hanging trio. */
val christmasOrnamentSet: List<Int> = listOf(
  R.drawable.xm_christmas_ornament_bauble,
  R.drawable.xm_christmas_ornament_star,
  R.drawable.xm_christmas_ornament_teardrop,
  R.drawable.xm_candy_cane_crossed
)

/**
 * A single ornament hanging from a fine thread, swaying like the decorations in
 * the skin this theme is modelled on.
 *
 * Each ornament runs its own infinite transition with a phase offset, so a row
 * of them never sways in lockstep.
 */
@Composable
private fun SwayingOrnament(
  @DrawableRes imageRes: Int,
  size: Dp,
  threadHeight: Dp,
  periodMillis: Int,
  swayDegrees: Float,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "xmas-sway")
  val angle by transition.animateFloat(
    initialValue = -swayDegrees,
    targetValue = swayDegrees,
    animationSpec = infiniteRepeatable(
      animation = tween(periodMillis, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "xmas-sway-angle"
  )

  Column(
    modifier = modifier.graphicsLayer {
      rotationZ = angle
      transformOrigin = TransformOrigin(0.5f, 0f)
    },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .width(1.dp)
        .height(threadHeight)
        .background(ChristmasGold.copy(alpha = 0.35f))
    )
    Image(
      painter = painterResource(imageRes),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier.size(size)
    )
  }
}

/**
 * Hanging ornament row used to crown a screen header: strings drop from the top
 * edge and the baubles drift gently.
 */
@Composable
fun ChristmasOrnamentGarland(
  modifier: Modifier = Modifier,
  ornaments: List<Int> = christmasOrnamentSet,
  ornamentSize: Dp = 44.dp,
  threadHeight: Dp = 14.dp
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(18.dp),
    verticalAlignment = Alignment.Top
  ) {
    ornaments.forEachIndexed { index, res ->
      SwayingOrnament(
        imageRes = res,
        size = if (index % 2 == 0) ornamentSize else ornamentSize * 0.82f,
        threadHeight = if (index % 2 == 0) threadHeight else threadHeight + 6.dp,
        periodMillis = 2600 + index * 240,
        swayDegrees = if (index % 2 == 0) 4.5f else 3.2f
      )
    }
  }
}

/**
 * Header band for list screens.
 *
 * Same language as the greeting card: a light blush → lavender surface, a pale
 * pink disc holding a full-colour 3D cluster, a bulb swag across the top and a
 * tricolour strip along the bottom edge. Everything is centred on the disc, so
 * the composition stays balanced at any width the screen gives it.
 */
@Composable
fun ChristmasDecorBand(
  modifier: Modifier = Modifier,
  height: Dp = 148.dp
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
      .background(christmasCardBrush())
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      // Centred blush disc with a white rim: the stage for the 3D scene.
      val radius = size.height * 0.46f
      val center = Offset(size.width * 0.5f, size.height * 0.53f)
      drawCircle(color = ChristmasBlush, radius = radius, center = center)
      drawCircle(color = ChristmasBlush.copy(alpha = 0.55f), radius = radius * 0.84f, center = center)
      drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = radius,
        center = center,
        style = Stroke(width = 2f)
      )

      // Bulb swag: a wire dipping symmetrically from both top corners, which is
      // what fills the width instead of leaving empty gutters.
      val path = Path().apply {
        moveTo(0f, size.height * 0.04f)
        quadraticBezierTo(size.width * 0.5f, size.height * 0.44f, size.width, size.height * 0.04f)
      }
      drawPath(
        path = path,
        color = ChristmasRose.copy(alpha = 0.30f),
        style = Stroke(width = 2f)
      )
      val bulbs = 11
      repeat(bulbs) { index ->
        val t = (index + 1f) / (bulbs + 1f)
        val inv = 1f - t
        val y = inv * inv * size.height * 0.04f +
          2f * inv * t * size.height * 0.44f +
          t * t * size.height * 0.04f
        val color = when {
          index % 3 == 0 -> ChristmasGold
          index % 3 == 1 -> ChristmasGreen
          else -> ChristmasRose
        }
        drawCircle(
          color = color.copy(alpha = 0.9f),
          radius = 3.6f,
          center = Offset(size.width * t, y + 5f)
        )
      }
    }

    // Cluster anchored on the bottom centre so tree, gifts, snowman and wreath
    // sit symmetrically inside the disc.
    Image(
      painter = painterResource(R.drawable.xm_christmas_tree_decorated),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 10.dp)
        .size(height * 0.66f)
    )

    Image(
      painter = painterResource(R.drawable.xm_christmas_present_stack),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(x = height * -0.34f, y = (-6).dp)
        .size(height * 0.38f)
    )

    Image(
      painter = painterResource(R.drawable.xm_snowman_classic),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(x = height * 0.38f, y = (-8).dp)
        .size(height * 0.34f)
    )

    Image(
      painter = painterResource(R.drawable.xm_christmas_wreath_classic),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(x = height * 0.30f, y = -height * 0.52f)
        .size(height * 0.30f)
    )

    // Tricolour strip along the bottom edge, as in the reference cards.
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .height(3.dp)
        .background(
          Brush.horizontalGradient(
            listOf(ChristmasRose, ChristmasGold, ChristmasGreen, ChristmasRose)
          )
        )
    )
  }
}

/**
 * Round badge holding a festive illustration, used where a small emblem is
 * enough (section headers, empty states).
 */
@Composable
fun ChristmasIllustration(
  @DrawableRes imageRes: Int,
  modifier: Modifier = Modifier,
  size: Dp = 72.dp
) {
  Image(
    painter = painterResource(imageRes),
    contentDescription = null,
    contentScale = ContentScale.Fit,
    modifier = modifier.size(size)
  )
}

/**
 * Animated bulb row: bulbs alternate red and gold and pulse with a staggered
 * phase, mimicking a live light string rather than a static divider.
 */
@Composable
fun ChristmasLightStringAnimated(
  modifier: Modifier = Modifier,
  bulbCount: Int = 12
) {
  val transition = rememberInfiniteTransition(label = "xmas-bulbs")
  val phase by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "xmas-bulb-phase"
  )

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    repeat(bulbCount) { index ->
      val isWarm = index % 2 == 0
      val wave = ((index.toFloat() / bulbCount) + phase) % 1f
      val glow = 0.45f + 0.55f * (1f - kotlin.math.abs(wave - 0.5f) * 2f)
      Box(
        modifier = Modifier
          .size(7.dp)
          .clip(RoundedCornerShape(50))
          .background(
            (if (isWarm) ChristmasRed else ChristmasGold)
              .copy(alpha = 0.35f + glow * 0.65f)
          )
      )
    }
  }
}

/** Thin wire + bulb strip: light string with a drawn cable behind it. */
@Composable
fun ChristmasLightStrip(
  modifier: Modifier = Modifier,
  bulbCount: Int = 14
) {
  Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(ChristmasGreen.copy(alpha = 0.35f))
    )
    Spacer(modifier = Modifier.height(3.dp))
    ChristmasLightStringAnimated(bulbCount = bulbCount)
    Spacer(modifier = Modifier.height(2.dp))
  }
}

private fun Modifier.alpha(value: Float): Modifier = this.then(
  Modifier.graphicsLayer { alpha = value }
)

/** Empty festive still-life used by empty states and dialogs. */
@Composable
fun ChristmasStillLife(
  modifier: Modifier = Modifier,
  height: Dp = 120.dp
) {
  Box(modifier = modifier.fillMaxSize().height(height)) {
    Image(
      painter = painterResource(R.drawable.xm_christmas_tree_decorated),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .align(Alignment.Center)
        .fillMaxSize()
    )
  }
}
