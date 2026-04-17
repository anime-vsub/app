package git.shin.animevsub.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextPrimary

@Composable
fun ActionButton(
  icon: ImageVector,
  label: String,
  modifier: Modifier = Modifier,
  iconTint: Color = TextPrimary,
  enabled: Boolean = true,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = modifier
      .clip(CircleShape)
      .background(DarkCard)
      .clickable(enabled = enabled) { onClick() }
      .padding(vertical = 8.dp, horizontal = 12.dp)
  ) {
    Icon(
      icon,
      null,
      tint = if (enabled) iconTint else iconTint.copy(alpha = 0.38f),
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))

    Text(
      text = label,
      color = if (enabled) TextPrimary else TextPrimary.copy(alpha = 0.38f),
      fontSize = 13.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
