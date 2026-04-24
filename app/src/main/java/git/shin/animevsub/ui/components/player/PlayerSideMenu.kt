package git.shin.animevsub.ui.components.player

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.DarkSurface

@Composable
fun PlayerSideMenu(
  visible: Boolean,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  title: String? = null,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  Box(modifier = modifier.fillMaxSize()) {
    // Backdrop
    AnimatedVisibility(
      visible = visible,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.3f))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismiss
          )
      )
    }

    // Side Menu
    AnimatedVisibility(
      visible = visible,
      enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
      exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it }),
      modifier = Modifier.align(Alignment.CenterEnd)
    ) {
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(if (findActivity(context)?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) 0.4f else 0.5f)
          .background(DarkSurface)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {}
          .padding(bottom = 16.dp)
      ) {
        if (title != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = title,
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
          }
        }
        Box(modifier = Modifier.weight(1f)) {
          content()
        }
      }
    }
  }
}
