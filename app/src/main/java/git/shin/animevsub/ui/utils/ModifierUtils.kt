package git.shin.animevsub.ui.utils

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import git.shin.animevsub.ui.theme.DarkCard

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
        DarkCard,
        Color(0xFF2C3D5E),
        DarkCard
      ),
      start = Offset(startOffsetX, 0f),
      end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
    )
  ).onGloballyPositioned {
    size = it.size
  }
}

fun Modifier.tvFocusScale(
  scale: Float = 1.05f,
  onFocus: () -> Unit = {}
): Modifier = this.composed {
  val configuration = LocalConfiguration.current
  val isTV = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION

  if (!isTV) return@composed this

  var isFocused by remember { mutableStateOf(false) }
  this
    .onFocusChanged {
      isFocused = it.isFocused
      if (it.isFocused) onFocus()
    }
    .graphicsLayer {
      scaleX = if (isFocused) scale else 1f
      scaleY = if (isFocused) scale else 1f
    }
    .border(
      width = if (isFocused) 2.dp else 0.dp,
      color = if (isFocused) Color.White else Color.Transparent,
      shape = RoundedCornerShape(8.dp)
    )
}
