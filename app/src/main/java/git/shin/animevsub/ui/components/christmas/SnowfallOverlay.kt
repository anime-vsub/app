package git.shin.animevsub.ui.components.christmas

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

private data class Snowflake(
  val x: Float,
  val size: Float,
  val speed: Float,
  val swayAmplitude: Float,
  val swayPhase: Float,
  val alpha: Float
)

private fun buildSnowflakes(count: Int, seed: Int): List<Snowflake> {
  val random = Random(seed)
  return List(count) {
    Snowflake(
      x = random.nextFloat(),
      size = 1.2f + random.nextFloat() * 2.6f,
      speed = 0.35f + random.nextFloat() * 0.75f,
      swayAmplitude = 0.01f + random.nextFloat() * 0.05f,
      swayPhase = random.nextFloat() * 6.283f,
      alpha = 0.14f + random.nextFloat() * 0.30f
    )
  }
}

/**
 * Gentle falling snow drawn on top of the UI. Cheap: a handful of circles per
 * frame, no allocations inside the draw phase. Pointer input passes through
 * because the canvas never requests it.
 */
@Composable
fun SnowfallOverlay(
  modifier: Modifier = Modifier,
  count: Int = 26,
  color: Color = Color.White
) {
  val flakes = remember(count) { buildSnowflakes(count, seed = 1225) }
  val transition = rememberInfiniteTransition(label = "snowfall")

  val progress by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 9000, easing = LinearEasing)
    ),
    label = "snow-progress"
  )

  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return@Canvas

    flakes.forEach { flake ->
      // Each flake loops over a slightly different span so they never
      // visibly "restart" together.
      val t = (progress + flake.x) % 1f
      val y = (t * 1.25f - 0.12f) * h
      val x = (flake.x + sin(t * 6.283f + flake.swayPhase) * flake.swayAmplitude) * w
      val radius = flake.size

      // Plain dots only: halos and cross-hairs read as rendering artefacts or
      // dust specks over text and video, so flakes stay small and sharp.
      drawCircle(
        color = color.copy(alpha = flake.alpha),
        radius = radius,
        center = Offset(x, y)
      )
    }
  }
}
