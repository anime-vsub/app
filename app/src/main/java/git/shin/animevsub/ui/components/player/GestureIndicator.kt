package git.shin.animevsub.ui.components.player

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
