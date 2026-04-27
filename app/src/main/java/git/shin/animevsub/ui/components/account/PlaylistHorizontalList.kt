package git.shin.animevsub.ui.components.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.Playlist
import git.shin.animevsub.ui.components.common.ErrorRetrySection
import git.shin.animevsub.ui.components.common.SectionHeader
import git.shin.animevsub.ui.components.playlist.PlaylistPoster
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.tvFocusScale

@Composable
fun PlaylistHorizontalList(
  playlists: List<Playlist>,
  isLoading: Boolean,
  error: String?,
  onHeaderClick: () -> Unit,
  onAddClick: (() -> Unit)? = null,
  onRetry: () -> Unit,
  onItemClick: (Playlist) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    SectionHeader(
      title = stringResource(R.string.playlists),
      onViewAll = onHeaderClick,
      onAddClick = onAddClick,
      compactArrow = true
    )

    if (isLoading) {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(5) {
          PlaylistHorizontalItemSkeleton()
        }
      }
    } else if (error != null) {
      ErrorRetrySection(onRetry = onRetry)
    } else if (playlists.isEmpty()) {
      Text(
        text = stringResource(R.string.no_playlists),
        color = TextGrey,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    } else {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(playlists) { playlist ->
          PlaylistHorizontalCard(
            playlist = playlist,
            onClick = { onItemClick(playlist) }
          )
        }
      }
    }
  }
}

@Composable
fun PlaylistHorizontalCard(
  playlist: Playlist,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .width(180.dp)
      .tvFocusScale()
      .clickable(onClick = onClick)
  ) {
    PlaylistPoster(
      posterUrl = playlist.poster,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = playlist.name,
      color = TextPrimary,
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      text = pluralStringResource(
        R.plurals.video_count,
        playlist.movieCount,
        playlist.movieCount
      ),
      color = TextSecondary,
      fontSize = 12.sp
    )
  }
}

@Composable
fun PlaylistHorizontalItemSkeleton() {
  Column(modifier = Modifier.width(180.dp)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(101.dp) // approx 16:9 for 180dp width
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .height(14.dp)
        .background(DarkCard, RoundedCornerShape(2.dp))
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.5f)
        .height(12.dp)
        .background(DarkCard, RoundedCornerShape(2.dp))
    )
  }
}
