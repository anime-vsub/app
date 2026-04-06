package git.shin.animevsub.ui.components.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.formatDuration

@Composable
fun HistoryItemRow(
  item: HistoryItem,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .width(140.dp)
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(4.dp))
    ) {
      AsyncImage(
        model = item.poster,
        contentDescription = item.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      if (item.dur > 0) {
        LinearProgressIndicator(
          progress = { (item.cur / item.dur).toFloat() },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .align(androidx.compose.ui.Alignment.BottomCenter),
          color = MainColor,
          trackColor = Color.White.copy(alpha = 0.3f),
        )
      }
    }

    Column(
      modifier = Modifier.weight(1f)
    ) {
      Text(
        text = item.name,
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = stringResource(R.string.history_subtitle, item.seasonName, item.chapName ?: ""),
        color = TextGrey,
        fontSize = 13.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "${formatDuration((item.cur * 1000).toLong())} / ${formatDuration((item.dur * 1000).toLong())}",
        color = TextGrey,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
fun HistoryItemRowSkeleton() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .width(140.dp)
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    )

    Column(
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.8f)
          .height(18.dp)
          .background(DarkCard, RoundedCornerShape(2.dp))
      )

      Spacer(modifier = Modifier.height(8.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth(0.4f)
          .height(14.dp)
          .background(DarkCard, RoundedCornerShape(2.dp))
      )

      Spacer(modifier = Modifier.height(8.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth(0.3f)
          .height(12.dp)
          .background(DarkCard, RoundedCornerShape(2.dp))
      )
    }
  }
}
