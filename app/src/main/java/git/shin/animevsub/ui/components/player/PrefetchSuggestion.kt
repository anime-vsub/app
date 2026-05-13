package git.shin.animevsub.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R

@Composable
fun PrefetchSuggestion(
  onEnable: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color.Black.copy(alpha = 0.8f))
      .padding(start = 8.dp, end = 2.dp, top = 6.dp, bottom = 6.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(Color(0xFFFFC107).copy(alpha = 0.2f))
          .padding(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Lightbulb,
          contentDescription = null,
          tint = Color(0xFFFFC107),
          modifier = Modifier.size(16.dp)
        )
      }
      Text(
        text = stringResource(R.string.prefetch_suggestion_text),
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.clickable(onClick = onEnable)
      )
      Box(
        modifier = Modifier
          .width(1.dp)
          .height(20.dp)
          .background(Color.White.copy(alpha = 0.2f))
      )
      IconButton(
        onClick = onDismiss,
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Dismiss",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
