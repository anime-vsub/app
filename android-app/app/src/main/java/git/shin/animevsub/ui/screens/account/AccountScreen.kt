package git.shin.animevsub.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.account.FollowHorizontalList
import git.shin.animevsub.ui.components.account.HistoryHorizontalList
import git.shin.animevsub.ui.components.account.PlaylistListSection
import git.shin.animevsub.ui.components.account.MenuItem
import git.shin.animevsub.ui.components.account.MenuSection
import git.shin.animevsub.ui.components.account.SettingsToggle
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.ErrorColor
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun AccountScreen(
  onNavigateToLogin: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToFollow: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToAbout: () -> Unit,
  onNavigateToPlaylists: () -> Unit,
  onNavigateToDetail: (String) -> Unit,
  onNavigateToPlayer: (String, String) -> Unit,
  viewModel: AccountViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .background(DarkBackground)
      .padding(bottom = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Profile section
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
      if (uiState.isLoggedIn && uiState.user != null) {
        val user = uiState.user!!
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp)
        ) {
          AsyncImage(
            model = user.avatar,
            contentDescription = user.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(60.dp)
              .clip(CircleShape)
              .background(DarkSurface)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = user.name,
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = user.email ?: user.username,
              color = TextSecondary,
              fontSize = 13.sp
            )
          }
        }
      } else {
        // Login prompt
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .clickable(onClick = onNavigateToLogin)
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.AccountCircle,
              contentDescription = null,
              tint = AccentMain,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = stringResource(R.string.login),
              color = AccentMain,
              fontSize = 16.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = stringResource(R.string.login_required),
              color = TextSecondary,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    if (uiState.isLoggedIn) {
      Spacer(modifier = Modifier.height(24.dp))

      HistoryHorizontalList(
        histories = uiState.histories,
        isLoading = uiState.isLoadingHistory,
        error = uiState.historyError,
        onHeaderClick = onNavigateToHistory,
        onRetry = { viewModel.refreshHistory() },
        onItemClick = { item ->
          item.chapId?.let { chapId ->
            onNavigateToPlayer(item.seasonId, chapId)
          } ?: onNavigateToDetail(item.seasonId)
        }
      )

      Spacer(modifier = Modifier.height(24.dp))

      FollowHorizontalList(
        follows = uiState.follows,
        isLoading = uiState.isLoadingFollows,
        error = uiState.followsError,
        onHeaderClick = onNavigateToFollow,
        onRetry = { viewModel.refreshFollows() },
        onItemClick = { anime -> onNavigateToDetail(anime.animeId) }
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Menu items
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      if (uiState.isLoggedIn) {
        PlaylistListSection(
          playlists = uiState.playlists,
          isLoading = uiState.isLoadingPlaylists,
          error = uiState.playlistsError,
          onRetry = { viewModel.refreshPlaylists() },
          onItemClick = { playlist ->
            onNavigateToPlaylists()
          }
        )

        Spacer(modifier = Modifier.height(16.dp))
      }

      MenuSection(title = stringResource(R.string.settings)) {
        SettingsToggle(
          label = stringResource(R.string.auto_next),
          checked = uiState.autoNext,
          onCheckedChange = { viewModel.setAutoNext(it) }
        )
        SettingsToggle(
          label = stringResource(R.string.auto_skip),
          checked = uiState.autoSkip,
          onCheckedChange = { viewModel.setAutoSkip(it) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      MenuSection(title = stringResource(R.string.gesture_controls)) {
        SettingsToggle(
          label = stringResource(R.string.volume_gesture),
          checked = uiState.volumeGesture,
          onCheckedChange = { viewModel.setVolumeGesture(it) }
        )
        SettingsToggle(
          label = stringResource(R.string.brightness_gesture),
          checked = uiState.brightnessGesture,
          onCheckedChange = { viewModel.setBrightnessGesture(it) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      MenuSection(title = stringResource(R.string.general)) {
        MenuItem(
          icon = Icons.Default.Settings,
          label = stringResource(R.string.settings),
          onClick = onNavigateToSettings
        )
        MenuItem(
          icon = Icons.Default.Info,
          label = stringResource(R.string.about),
          onClick = onNavigateToAbout
        )
      }

      // Logout button
      if (uiState.isLoggedIn) {
        Spacer(modifier = Modifier.height(24.dp))
        Button(
          onClick = { viewModel.logout() },
          colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = stringResource(R.string.logout))
        }
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }
}
