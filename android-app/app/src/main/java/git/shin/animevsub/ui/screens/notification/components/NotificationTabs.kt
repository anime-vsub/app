package git.shin.animevsub.ui.screens.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey

@Composable
fun NotificationTabs(
  selectedTab: Int,
  onTabSelected: (Int) -> Unit,
  apiCount: Int,
  dbCount: Int
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .fillMaxWidth()
      .height(40.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(DarkSurface),
    verticalAlignment = Alignment.CenterVertically
  ) {
    NotificationTabItem(
      title = "API",
      count = apiCount,
      isSelected = selectedTab == 0,
      onClick = { onTabSelected(0) },
      modifier = Modifier.weight(1f)
    )
    NotificationTabItem(
      title = "Database",
      count = dbCount,
      isSelected = selectedTab == 1,
      onClick = { onTabSelected(1) },
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun NotificationTabItem(
  title: String,
  count: Int,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(4.dp)
      .clip(RoundedCornerShape(7.dp))
      .background(if (isSelected) AccentMain else Color.Transparent)
      .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = title,
        color = if (isSelected) Color.White else TextGrey,
        fontSize = 13.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
      if (count > 0) {
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = count.toString(),
          color = Color.White,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
