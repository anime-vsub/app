package git.shin.animevsub.ui.screens.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.data.model.Playlist
import git.shin.animevsub.ui.components.common.ErrorRetrySection
import git.shin.animevsub.ui.components.playlist.CreatePlaylistDialog
import git.shin.animevsub.ui.components.playlist.PlaylistPoster
import git.shin.animevsub.ui.screens.account.AccountViewModel
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.tvFocusScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
  onBack: () -> Unit,
  onNavigateToPlaylist: (String) -> Unit,
  viewModel: AccountViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  var showCreateDialog by remember { mutableStateOf(false) }

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.playlists),
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = null,
              tint = TextPrimary
            )
          }
        },
        actions = {
          IconButton(onClick = { showCreateDialog = true }) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = stringResource(R.string.create_playlist),
              tint = TextPrimary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
      )
    },
    containerColor = DarkBackground
  ) { padding ->
    PullToRefreshBox(
      isRefreshing = uiState.isRefreshing,
      onRefresh = { viewModel.refreshPlaylists() },
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      if (uiState.isLoadingPlaylists && uiState.playlists.isEmpty()) {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          items(10) {
            PlaylistVerticalRowSkeleton()
          }
        }
      } else if (uiState.playlistsError != null) {
        ErrorRetrySection(onRetry = { viewModel.refreshPlaylists() })
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          items(uiState.playlists) { playlist ->
            PlaylistVerticalRow(
              playlist = playlist,
              onClick = { onNavigateToPlaylist(playlist.id.toString()) }
            )
          }
        }
      }
    }
  }

  if (showCreateDialog) {
    CreatePlaylistDialog(
      onDismiss = { showCreateDialog = false },
      onCreate = { name ->
        viewModel.createPlaylist(name)
        showCreateDialog = false
      }
    )
  }
}

@Composable
fun PlaylistVerticalRowSkeleton() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .width(140.dp)
        .height(79.dp) // 16:9
        .clip(RoundedCornerShape(8.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.7f)
          .height(18.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(DarkCard)
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.4f)
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(DarkCard)
      )
    }
  }
}

@Composable
fun PlaylistVerticalRow(
  playlist: Playlist,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .tvFocusScale()
      .clickable(onClick = onClick),
    verticalAlignment = Alignment.CenterVertically
  ) {
    PlaylistPoster(
      posterUrl = playlist.poster,
      modifier = Modifier.width(140.dp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = playlist.name,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = pluralStringResource(
          R.plurals.video_count,
          playlist.movieCount,
          playlist.movieCount
        ),
        color = TextSecondary,
        fontSize = 14.sp
      )
    }
  }
}
