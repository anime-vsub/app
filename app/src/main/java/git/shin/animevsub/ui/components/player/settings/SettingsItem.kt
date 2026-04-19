package git.shin.animevsub.ui.components.player.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsItem(icon: ImageVector, title: String, value: String? = null, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
    Spacer(modifier = Modifier.width(16.dp))
    Text(text = title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
    if (value != null) {
      Text(text = value, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        Icons.Default.ChevronRight,
        null,
        tint = Color.White.copy(alpha = 0.3f),
        modifier = Modifier.size(18.dp)
      )
    }
  }
}
