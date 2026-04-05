package git.shin.animevsub.ui.components.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.formatDuration

@Composable
fun HistoryCard(
  item: HistoryItem,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .width(200.dp)
      .clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(4.dp))
    ) {
      AsyncImage(
        model = item.poster,
        contentDescription = item.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      // Bottom progress and time
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
              startY = 0f
            )
          )
      ) {
        Column {
          Spacer(modifier = Modifier.height(40.dp))
          if (item.dur > 0) {
            LinearProgressIndicator(
              progress = { (item.cur / item.dur).toFloat() },
              modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
              color = MainColor,
              trackColor = Color.White.copy(alpha = 0.3f),
            )
          }
        }
      }

      Text(
        text = "${formatDuration((item.cur * 1000).toLong())} / ${formatDuration((item.dur * 1000).toLong())}",
        color = Color.White,
        fontSize = 12.sp,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 8.dp, bottom = 8.dp)
      )
    }

    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = item.name,
      color = TextPrimary,
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )
    Text(
      text = stringResource(R.string.history_subtitle, item.seasonName, item.chapName ?: ""),
      color = TextGrey,
      fontSize = 12.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
