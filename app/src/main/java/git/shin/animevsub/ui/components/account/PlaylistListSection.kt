package git.shin.animevsub.ui.components.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.Playlist
import git.shin.animevsub.ui.components.common.ErrorRetrySection
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.tvFocusScale

@Composable
fun PlaylistListSection(
  playlists: List<Playlist>,
  isLoading: Boolean,
  error: String?,
  onRetry: () -> Unit,
  onItemClick: (Playlist) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = stringResource(R.string.playlists),
      color = TextGrey,
      fontSize = 13.sp,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    if (isLoading) {
      Column {
        repeat(3) {
          PlaylistSkeletonItem()
        }
      }
    } else if (error != null) {
      ErrorRetrySection(onRetry = onRetry)
    } else if (playlists.isEmpty()) {
      Text(
        text = stringResource(R.string.no_playlists),
        color = TextGrey,
        fontSize = 13.sp,
        modifier = Modifier.padding(vertical = 8.dp)
      )
    } else {
      playlists.forEach { playlist ->
        PlaylistItemRow(
          playlist = playlist,
          onClick = { onItemClick(playlist) }
        )
      }
    }
  }
}

@Composable
fun PlaylistItemRow(
  playlist: Playlist,
  onClick: () -> Unit
) {
  val displayName = playlist.name.ifBlank { "<" + stringResource(R.string.unknown) + ">" }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .tvFocusScale()
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
      contentDescription = null,
      tint = AccentMain,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = displayName,
        color = TextPrimary,
        fontSize = 15.sp
      )
      Text(
        text = "${playlist.movieCount} anime",
        color = TextSecondary,
        fontSize = 12.sp
      )
    }
    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForward,
      contentDescription = null,
      tint = TextGrey,
      modifier = Modifier.size(16.dp)
    )
  }
}

@Composable
fun PlaylistSkeletonItem() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(22.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .width(120.dp)
          .height(16.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(DarkCard)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Box(
        modifier = Modifier
          .width(60.dp)
          .height(12.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(DarkCard)
      )
    }
    Box(
      modifier = Modifier
        .size(16.dp)
        .clip(CircleShape)
        .background(DarkCard)
    )
  }
}
