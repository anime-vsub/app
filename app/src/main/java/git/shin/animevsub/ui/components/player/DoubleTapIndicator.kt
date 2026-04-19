package git.shin.animevsub.ui.components.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun DoubleTapIndicator(
  side: String,
  text: String,
  modifier: Modifier = Modifier
) {
  val isForward = side == "right"
  val alpha = remember { Animatable(0f) }
  val scale = remember { Animatable(0.8f) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(side) {
    scope.launch {
      alpha.snapTo(0f)
      scale.snapTo(0.8f)
      alpha.animateTo(1f, tween(200))
      alpha.animateTo(0f, tween(600, delayMillis = 200))
    }
    scope.launch {
      scale.animateTo(1.2f, tween(800, easing = FastOutSlowInEasing))
    }
  }

  Box(
    modifier = modifier
      .fillMaxHeight()
      .fillMaxWidth(0.3f)
      .alpha(alpha.value)
      .background(
        brush = Brush.horizontalGradient(
          colors = if (isForward) {
            listOf(Color.Transparent, Color.White.copy(alpha = 0.25f))
          } else {
            listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
          }
        ),
        shape = if (isForward) {
          RoundedCornerShape(topStartPercent = 25, bottomStartPercent = 25)
        } else {
          RoundedCornerShape(topEndPercent = 25, bottomEndPercent = 25)
        }
      ),
    contentAlignment = if (isForward) Alignment.CenterEnd else Alignment.CenterStart
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .scale(scale.value)
        .padding(
          start = if (isForward) 0.dp else 42.dp,
          end = if (isForward) 42.dp else 0.dp
        )
    ) {
      Icon(
        imageVector = if (isForward) Icons.Default.FastForward else Icons.Default.FastRewind,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(32.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = text,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
