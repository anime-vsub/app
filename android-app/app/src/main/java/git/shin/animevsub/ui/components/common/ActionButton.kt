package git.shin.animevsub.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun ActionButton(
  icon: ImageVector,
  label: String,
  modifier: Modifier = Modifier,
  iconTint: Color = TextPrimary,
  enabled: Boolean = true,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(enabled = enabled) { onClick() }
      .padding(vertical = 8.dp, horizontal = 4.dp)
  ) {
    Icon(
      icon,
      null,
      tint = if (enabled) iconTint else iconTint.copy(alpha = 0.38f),
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = label,
      color = if (enabled) TextSecondary else TextSecondary.copy(alpha = 0.38f),
      fontSize = 11.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
