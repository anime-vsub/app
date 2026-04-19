package git.shin.animevsub.ui.components.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
