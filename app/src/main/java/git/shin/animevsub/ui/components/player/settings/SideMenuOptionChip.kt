package git.shin.animevsub.ui.components.player.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.MainColor

@Composable
fun SideMenuOptionChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
  TextButton(
    onClick = onClick,
    colors = ButtonDefaults.textButtonColors(contentColor = if (isSelected) MainColor else Color.White),
    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
    modifier = Modifier.height(36.dp)
  ) {
    Text(
      text = text,
      fontSize = 14.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
  }
}
