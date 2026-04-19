package git.shin.animevsub.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R

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
