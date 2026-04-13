package git.shin.animevsub.ui.components.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import kotlinx.coroutines.launch

@Composable
fun PlayerControlSmallButton(
  icon: ImageVector,
  modifier: Modifier = Modifier,
  text: String? = null,
  onClick: () -> Unit
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(4.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = Color.White,
      modifier = Modifier.size(20.dp)
    )
    if (text != null) {
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = text,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun SkipNotification(
  text: String,
  secondsRemaining: Int,
  onSkip: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color.Black.copy(alpha = 0.7f))
      .padding(start = 8.dp, end = 2.dp, top = 4.dp, bottom = 4.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Column(
        modifier = Modifier.clickable(onClick = onSkip)
      ) {
        Text(
          text = text,
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          lineHeight = 12.sp
        )
        Text(
          text = pluralStringResource(
            R.plurals.seconds_remaining,
            secondsRemaining,
            secondsRemaining
          ),
          color = Color.White.copy(alpha = 0.5f),
          fontSize = 10.sp
        )
      }
      Box(
        modifier = Modifier
          .width(1.dp)
          .height(24.dp)
          .background(Color.White.copy(alpha = 0.2f))
      )
      IconButton(
        onClick = onClose,
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
fun GestureIndicator(
  icon: ImageVector,
  text: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
      .padding(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = Color.White,
      modifier = Modifier.size(28.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = text,
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
fun SeekIndicator(
  isForward: Boolean,
  modifier: Modifier = Modifier
) {
  val alpha = remember { Animatable(0f) }
  val scale = remember { Animatable(0.8f) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(isForward) {
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
      .fillMaxWidth(0.4f)
      .alpha(alpha.value)
      .background(
        brush = Brush.horizontalGradient(
          colors = if (isForward) {
            listOf(Color.Transparent, Color.White.copy(alpha = 0.2f))
          } else {
            listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
          }
        ),
        shape = if (isForward) {
          RoundedCornerShape(topStartPercent = 100, bottomStartPercent = 100)
        } else {
          RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
        }
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.scale(scale.value)
    ) {
      Icon(
        imageVector = if (isForward) Icons.Default.FastForward else Icons.Default.FastRewind,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(32.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = if (isForward) "+10s" else "-10s",
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
