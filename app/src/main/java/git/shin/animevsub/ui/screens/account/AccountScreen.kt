package git.shin.animevsub.ui.screens.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.account.FollowHorizontalList
import git.shin.animevsub.ui.components.account.HistoryHorizontalList
import git.shin.animevsub.ui.components.account.PlaylistListSection
import git.shin.animevsub.ui.components.dialogs.UpdateDialog
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.ErrorColor
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
  onNavigateToLogin: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToFollow: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToAbout: () -> Unit,
  onNavigateToPlaylist: (String) -> Unit,
  onNavigateToDetail: (String, String?) -> Unit,
  viewModel: AccountViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current

  LaunchedEffect(Unit) {
    viewModel.uiEvent.collect { event ->
      when (event) {
        is git.shin.animevsub.data.repository.AnimeRepository.AuthEvent.PromptForAction -> {
          // MainActivity handles this by showing a dialog, so we can optionally show a toast or do nothing here
          Toast.makeText(context, R.string.session_expired, Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  var showUpdateDialog by remember { mutableStateOf<git.shin.animevsub.data.model.UpdateInfo?>(null) }

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.nav_account),
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
          )
        },
        actions = {
          IconButton(
            onClick = {
              viewModel.checkForUpdate(
                onUpdateAvailable = { showUpdateDialog = it },
                onNoUpdate = {
                  Toast.makeText(context, R.string.no_update, Toast.LENGTH_SHORT).show()
                },
                onError = {
                  Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT).show()
                }
              )
            },
            enabled = !uiState.isCheckingUpdate
          ) {
            if (uiState.isCheckingUpdate) {
              CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = AccentMain,
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.Default.Update,
                contentDescription = stringResource(R.string.check_update),
                tint = TextPrimary
              )
            }
          }
          IconButton(onClick = onNavigateToSettings) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = stringResource(R.string.settings),
              tint = TextPrimary
            )
          }
          IconButton(onClick = onNavigateToAbout) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = stringResource(R.string.about),
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
      onRefresh = { viewModel.refresh() },
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(bottom = 16.dp)
      ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Profile section
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
          if (!uiState.isAuthReady) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCard),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator(color = AccentMain)
            }
          } else if (uiState.isLoggedIn && uiState.user != null) {
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
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = user.email ?: user.username,
                  color = TextSecondary,
                  fontSize = 13.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              IconButton(
                onClick = { viewModel.performLogout() },
                modifier = Modifier
                  .clip(CircleShape)
                  .background(ErrorColor.copy(alpha = 0.1f))
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Logout,
                  contentDescription = stringResource(R.string.logout),
                  tint = ErrorColor,
                  modifier = Modifier.size(20.dp)
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
              onNavigateToDetail(item.seasonId, item.chapId)
            }
          )

          Spacer(modifier = Modifier.height(24.dp))

          FollowHorizontalList(
            follows = uiState.follows,
            isLoading = uiState.isLoadingFollows,
            error = uiState.followsError,
            onHeaderClick = onNavigateToFollow,
            onRetry = { viewModel.refreshFollows() },
            onItemClick = { anime -> onNavigateToDetail(anime.animeId, null) }
          )

          Spacer(modifier = Modifier.height(24.dp))

          // Menu items
          Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            PlaylistListSection(
              playlists = uiState.playlists,
              isLoading = uiState.isLoadingPlaylists,
              error = uiState.playlistsError,
              onRetry = { viewModel.refreshPlaylists() },
              onItemClick = { playlist ->
                onNavigateToPlaylist(playlist.id.toString())
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    showUpdateDialog?.let { info ->
      UpdateDialog(
        info = info,
        onDismiss = { showUpdateDialog = null },
        onConfirm = {
          viewModel.downloadUpdate(info)
          showUpdateDialog = null
        }
      )
    }
  }
}
