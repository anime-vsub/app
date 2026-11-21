package git.shin.animevsub.ui.components.christmas

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.isChristmasThemeActive
import kotlin.math.sin

/**
 * Horizontal fir garland: the ring of the 3D wreath asset unwrapped into a
 * wrap-around strip. One tile spans a phone screen; on wide layouts further
 * tiles join seamlessly because the source image loops 0-360 degrees. A single
 * bitmap, so there is no per-frame canvas work.
 */
@Composable
fun ChristmasBoughStrip(
  modifier: Modifier = Modifier,
  height: Dp = 34.dp,
  alpha: Float = 1f
) {
  BoxWithConstraints(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .alpha(alpha)
      .clipToBounds()
  ) {
    val tile = height * 27.5f
    val tiles = (maxWidth / tile).toInt().coerceAtLeast(1)
    // Slide the wrap so the wreath's bow is not centred on every screen.
    Row(
      modifier = Modifier
        .offset(x = -tile * 0.24f)
        .requiredWidth(tile * (tiles + 1))
    ) {
      repeat(tiles + 1) {
        Image(
          painter = painterResource(R.drawable.xm_pine_garland),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .width(tile)
            .height(height)
        )
      }
    }
  }
}

/** A short piece of bough, rotated so it climbs a corner like a wreath arc. */
@Composable
fun ChristmasBoughArc(
  modifier: Modifier = Modifier,
  angle: Float = 0f,
  mirror: Boolean = false,
  width: Dp = 54.dp,
  height: Dp = 24.dp,
  alpha: Float = 1f
) {
  Image(
    painter = painterResource(R.drawable.xm_pine_bough_tile),
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = modifier
      .size(width, height)
      .alpha(alpha)
      .graphicsLayer {
        rotationZ = angle
        scaleX = if (mirror) -1f else 1f
      }
  )
}

/**
 * Wreath-style frame for a poster card: a bough along the bottom edge that
 * turns up both sides, the way a wreath hugs what it surrounds.
 */
@Composable
fun ChristmasCardWreath(modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    ChristmasBoughArc(
      angle = -26f,
      width = 54.dp,
      height = 21.dp,
      alpha = 0.9f,
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset(x = (-18).dp, y = (-5).dp)
    )
    ChristmasBoughArc(
      angle = 26f,
      mirror = true,
      width = 54.dp,
      height = 21.dp,
      alpha = 0.9f,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .offset(x = 18.dp, y = (-5).dp)
    )
    // Bow-free slice of the garland, so a grid of cards does not repeat a big
    // ribbon knot under every poster.
    Image(
      painter = painterResource(R.drawable.xm_pine_trim),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .height(16.dp)
        .alpha(0.92f)
    )
  }
}

/**
 * Decoration that drapes across the very top of the app on every screen: a fir
 * garland under the status bar, a strand of twinkling lights threaded through
 * it, and baubles dangling on swaying threads.
 */
@Composable
fun ChristmasTopGarland(modifier: Modifier = Modifier) {
  if (!isChristmasThemeActive()) return

  val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  val anim = rememberInfiniteTransition(label = "xmas-garland")
  val swing by anim.animateFloat(
    initialValue = -1f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2900),
      repeatMode = RepeatMode.Reverse
    ),
    label = "swing"
  )
  val twinkle by anim.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000),
      repeatMode = RepeatMode.Reverse
    ),
    label = "twinkle"
  )

  // The bough sits just under the status bar so its needle fringe is legible,
  // and the decorations dangle below that.
  val boughOffset = statusBar - 16.dp
  val boughHeight = 34.dp
  val boughBottom = boughOffset + boughHeight
  val bandHeight = statusBar + 62.dp
  // Threads start inside the bough's fringe so they read as tied to it.
  val drop = boughBottom - 6.dp

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(bandHeight)
  ) {
    // Faint scrim keeps the needles readable over bright posters.
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(bandHeight)
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF0B0712).copy(alpha = 0.50f),
              Color(0xFF0B0712).copy(alpha = 0.16f),
              Color.Transparent
            )
          )
        )
    )
    ChristmasBoughStrip(
      height = boughHeight,
      alpha = 0.98f,
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = boughOffset)
    )

    val hangX = listOf(0.09f, 0.28f, 0.5f, 0.74f, 0.92f)
    val hangRes = listOf(
      R.drawable.xm_christmas_ornament_bauble,
      R.drawable.xm_candy_cane_classic,
      R.drawable.xm_christmas_wreath_classic,
      R.drawable.xm_snowflake_ornate,
      R.drawable.xm_christmas_ornament_star
    )
    val hangSize = listOf(17.dp, 15.dp, 26.dp, 15.dp, 17.dp)
    val threadLen = listOf(13.dp, 20.dp, 6.dp, 17.dp, 11.dp)

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val width = maxWidth
      for (i in hangX.indices) {
        val size = hangSize[i]
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .offset(x = width * hangX[i] - size / 2, y = drop)
            .graphicsLayer {
              rotationZ = swing * (2.1f + i * 0.5f)
              transformOrigin = TransformOrigin(0.5f, 0f)
            }
        ) {
          Box(
            modifier = Modifier
              .width(1.4.dp)
              .height(threadLen[i])
              .background(
                Brush.verticalGradient(
                  listOf(
                    Color(0xFFFFF0C4).copy(alpha = 0.95f),
                    Color(0xFFFFE6A8).copy(alpha = 0.45f)
                  )
                )
              )
          )
          Box(contentAlignment = Alignment.Center) {
            Box(
              modifier = Modifier
                .size(size * 1.9f)
                .background(
                  Brush.radialGradient(
                    listOf(
                      Color(0xFFFFD98A).copy(alpha = 0.14f * twinkle),
                      Color.Transparent
                    )
                  )
                )
            )
            Image(
              painter = painterResource(hangRes[i]),
              contentDescription = null,
              contentScale = ContentScale.Fit,
              modifier = Modifier.size(size)
            )
          }
        }
      }

      // Little lights threaded along the bough itself.
      val lights = listOf(0.16f, 0.33f, 0.47f, 0.63f, 0.8f)
      val lightColors = listOf(
        Color(0xFFFFD166),
        Color(0xFFFF8FB8),
        Color(0xFF7FE3C4),
        Color(0xFFFFD166),
        Color(0xFFFF8FB8)
      )
      for (i in lights.indices) {
        val pulse = 0.5f + 0.5f * sin(i * 1.9f + twinkle * 3.1f)
        Box(
          modifier = Modifier
            .offset(x = width * lights[i], y = boughBottom - 4.dp)
            .size(8.dp)
            .background(
              Brush.radialGradient(
                listOf(
                  Color.White.copy(alpha = 0.9f * pulse),
                  lightColors[i].copy(alpha = 0.95f * pulse),
                  lightColors[i].copy(alpha = 0.30f * pulse),
                  Color.Transparent
                )
              )
            )
        )
      }
    }
  }
}
