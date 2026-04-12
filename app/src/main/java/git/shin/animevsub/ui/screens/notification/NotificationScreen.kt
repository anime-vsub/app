package git.shin.animevsub.ui.screens.notification

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.anime.NotificationListSkeleton
import git.shin.animevsub.ui.screens.notification.components.ApiNotificationTab
import git.shin.animevsub.ui.screens.notification.components.DbNotificationTab
import git.shin.animevsub.ui.screens.notification.components.LoginRequiredScreen
import git.shin.animevsub.ui.screens.notification.components.NotificationTabs
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
  onNavigateToDetail: (String) -> Unit,
  onNavigateToLogin: () -> Unit,
  viewModel: NotificationViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val pagerState = rememberPagerState(pageCount = { 2 })

  // If autoSync is enabled, set default tab to Database (index 1)
  LaunchedEffect(uiState.autoSync, uiState.isAuthReady) {
    if (uiState.isAuthReady && uiState.autoSync && pagerState.currentPage == 0) {
      pagerState.scrollToPage(1)
    }
  }

  LaunchedEffect(Unit) {
    viewModel.uiEvent.collect { event ->
      when (event) {
        is NotificationUiEvent.ShowToast -> {
          Toast.makeText(
            context,
            event.message ?: context.getString(event.messageRes ?: R.string.error_occurred),
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }
  }

  Scaffold(
    topBar = {
      Column(modifier = Modifier.background(DarkBackground)) {
        TopAppBar(
          title = {
            Text(
              text = stringResource(R.string.notifications),
              color = TextPrimary,
              fontSize = 20.sp,
              fontWeight = FontWeight.SemiBold,
            )
          },
          actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              androidx.compose.material3.Switch(
                checked = uiState.autoSync,
                onCheckedChange = { viewModel.setAutoSync(it) },
                modifier = Modifier.scale(0.7f),
                colors = androidx.compose.material3.SwitchDefaults.colors(
                  checkedThumbColor = AccentMain,
                  checkedTrackColor = AccentMain.copy(alpha = 0.5f)
                )
              )
              IconButton(onClick = { viewModel.startSync() }) {
                Icon(
                  imageVector = Icons.Default.Sync,
                  contentDescription = "Sync",
                  tint = if (uiState.isSyncing) AccentMain else TextPrimary,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        NotificationTabs(
          selectedTab = pagerState.currentPage,
          onTabSelected = { index ->
            scope.launch {
              pagerState.animateScrollToPage(index)
            }
          },
          apiCount = uiState.data?.items?.size ?: 0,
          dbCount = uiState.dbNotificationCount?.notifyCount ?: 0
        )
      }
    },
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    containerColor = DarkBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      if (uiState.isSyncing) {
        LinearProgressIndicator(
          modifier = Modifier.fillMaxWidth(),
          color = AccentMain,
          trackColor = DarkSurface
        )
      }

      if (!uiState.isAuthReady) {
        NotificationListSkeleton()
      } else if (!uiState.isLoggedIn) {
        LoginRequiredScreen(onNavigateToLogin)
      } else {
        HorizontalPager(
          state = pagerState,
          modifier = Modifier.fillMaxSize(),
          beyondViewportPageCount = 1
        ) { page ->
          when (page) {
            0 -> ApiNotificationTab(
              uiState = uiState,
              onRefresh = { viewModel.loadNotifications(true) },
              onNavigateToDetail = onNavigateToDetail,
              onTrigger = { viewModel.onTrigger(it) },
              onRetry = { viewModel.loadNotifications() }
            )

            1 -> DbNotificationTab(
              uiState = uiState,
              onRefresh = { viewModel.loadDbNotifications(true) },
              onLoadMore = { viewModel.loadDbNotifications() },
              onNavigateToDetail = onNavigateToDetail,
              onDelete = { season, chapId -> viewModel.deleteDbNotification(season, chapId) }
            )
          }
        }
      }
    }
  }
}
