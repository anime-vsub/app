package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.model.DbNotificationEpisode
import git.shin.animevsub.data.model.DbNotificationItem
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.formatTimeAgo

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DbNotificationItemRow(
  item: DbNotificationItem,
  onClick: (String, String) -> Unit,
  onDelete: (String, String?) -> Unit
) {
  val latestEpisode = item.episodes.firstOrNull()

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(DarkBackground)
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(item.season, latestEpisode?.chapId ?: "") },
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = TextPrimary)) {
              append(item.name)
            }
          },
          color = TextGrey,
          fontSize = 15.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        if (latestEpisode != null) {
          Text(
            text = stringResource(R.string.episode_label, latestEpisode.name),
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 2.dp)
          )
        }

        Text(
          text = formatTimeAgo(item.latestChapTime ?: item.createdAt),
          color = TextGrey,
          fontSize = 13.sp
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      AsyncImage(
        model = item.image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .width(120.dp)
          .aspectRatio(16f / 9f)
          .clip(RoundedCornerShape(4.dp))
          .background(DarkSurface)
          .clickable { onClick(item.season, latestEpisode?.chapId ?: "") }
      )

      IconButton(
        onClick = { onDelete(item.season, null) },
        modifier = Modifier.padding(start = 4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Xóa tất cả",
          tint = TextGrey,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item.episodes.forEach { episode ->
        EpisodeChip(
          episode = episode,
          onClick = { onClick(item.season, episode.chapId) },
          onDelete = { onDelete(item.season, episode.chapId) }
        )
      }
    }
  }
}

@Composable
fun EpisodeChip(
  episode: DbNotificationEpisode,
  onClick: () -> Unit,
  onDelete: () -> Unit
) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .background(DarkSurface)
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = episode.name,
      color = TextPrimary,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.width(4.dp))
    Icon(
      imageVector = Icons.Default.Close,
      contentDescription = "Delete",
      tint = TextGrey,
      modifier = Modifier
        .size(12.dp)
        .clickable { onDelete() }
    )
  }
}
