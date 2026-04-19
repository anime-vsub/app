package git.shin.animevsub.ui.components.player.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun SideMenuSection(title: String, content: @Composable () -> Unit) {
  Column(modifier = Modifier.padding(vertical = 8.dp)) {
    Text(
      text = title,
      color = TextSecondary,
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    content()
  }
}
