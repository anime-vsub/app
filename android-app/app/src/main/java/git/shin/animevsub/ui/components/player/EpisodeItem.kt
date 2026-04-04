package git.shin.animevsub.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.data.model.WatchProgress
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextPrimary

@Composable
fun EpisodeItem(
  chap: ChapterInfo,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isLarge: Boolean = false,
  progress: WatchProgress? = null
) {
  Box(
    modifier = modifier
      .height(if (isLarge) 42.dp else 36.dp)
      .widthIn(min = if (isLarge) 0.dp else 45.dp)
      .clip(RoundedCornerShape(if (isLarge) 6.dp else 4.dp))
      // .background(if (isSelected) MainColor.copy(alpha = 0.1f) else DarkCard)
      .border(
        width = 1.dp,
        color = if (isSelected) MainColor else Color(0xFF3A3A3A),
        shape = RoundedCornerShape(if (isLarge) 6.dp else 4.dp)
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = chap.name,
      modifier = Modifier.padding(horizontal = if (isLarge) 4.dp else 12.dp),
      color = if (isSelected) MainColor else TextPrimary,
      fontSize = 13.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )

    if (progress != null && progress.dur > 0) {
      val percent = (progress.cur.toFloat() / progress.dur.toFloat()).coerceIn(0f, 1f)
      Box(
        modifier = Modifier
          .matchParentSize()
          .padding(start = 2.dp, end = 2.dp, bottom = 1.dp),
        contentAlignment = Alignment.BottomStart
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(Color.White.copy(alpha = 0.1f))
        )
        Box(
          modifier = Modifier
            .fillMaxWidth(percent)
            .height(2.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(if (isSelected) Color(0xFF3B82F6) else MainColor)
        )
      }
    }
  }
}
