package git.shin.animevsub.ui.utils

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmerEffect(): Modifier = composed {
  var size by remember { mutableStateOf(IntSize.Zero) }
  val transition = rememberInfiniteTransition(label = "shimmer")
  val startOffsetX by transition.animateFloat(
    initialValue = -2 * size.width.toFloat(),
    targetValue = 2 * size.width.toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(1000)
    ),
    label = "shimmerOffsetX"
  )

  background(
    brush = Brush.linearGradient(
      colors = listOf(
        Color(0xFF1E2D4A),
        Color(0xFF2C3D5E),
        Color(0xFF1E2D4A)
      ),
      start = Offset(startOffsetX, 0f),
      end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
    )
  ).onGloballyPositioned {
    size = it.size
  }
}
