package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun RankingItem(
  rank: Int,
  item: AnimeCard,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Rank number
    Text(
      text = String.format("%02d", rank),
      color = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> TextGrey
      },
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.width(40.dp)
    )

    // Thumbnail
    AsyncImage(
      model = item.image,
      contentDescription = item.name,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(55.dp, 75.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(TextGrey.copy(alpha = 0.1f))
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = item.name,
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      item.lastEpisodeId?.let {
        Text(
          text = it,
          color = TextSecondary,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (item.rate > 0) {
          Text(
            text = String.format("★ %.1f", item.rate),
            color = Color(0xFFFFB400),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(8.dp))
        }
        item.views?.let {
          Text(
            text = stringResource(R.string.views_count, it.toString()),
            color = TextGrey,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}
