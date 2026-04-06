package git.shin.animevsub.ui.screens.notification

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.anime.NotificationItemRow
import git.shin.animevsub.ui.components.anime.NotificationListSkeleton
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NotificationScreen(
  onNavigateToDetail: (String) -> Unit,
  onNavigateToLogin: () -> Unit,
  viewModel: NotificationViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current

  LaunchedEffect(Unit) {
    viewModel.uiEvent.collect { event ->
      when (event) {
        is NotificationUiEvent.ShowToast -> {
          Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    // Title
    Text(
      text = stringResource(R.string.notifications),
      color = TextPrimary,
      fontSize = 20.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(16.dp)
    )

    if (!uiState.isLoggedIn) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Outlined.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextGrey
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = stringResource(R.string.login_required),
            color = TextSecondary,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onNavigateToLogin,
            colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(stringResource(R.string.login))
          }
        }
      }
    } else {
      val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() }
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .pullRefresh(pullRefreshState)
      ) {
        when {
          uiState.isLoading && !uiState.isRefreshing -> NotificationListSkeleton()
          uiState.error != null -> {
            ErrorScreen(error = uiState.error, onRetry = { viewModel.retry() })
          }

          uiState.data != null -> {
            val items = uiState.data!!.items
            if (items.isEmpty()) {
              EmptyNotifications()
            } else {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
              ) {
                items(items, key = { it.id }) { notification ->
                  NotificationItemRow(
                    notification = notification,
                    onClick = {
                      notification.animeId?.let { id -> onNavigateToDetail(id) }
                    },
                    onClose = { trigger ->
                      viewModel.onTrigger(trigger)
                    }
                  )
                }
              }
            }
          }
        }

        PullRefreshIndicator(
          refreshing = uiState.isRefreshing,
          state = pullRefreshState,
          modifier = Modifier.align(Alignment.TopCenter),
          backgroundColor = DarkSurface,
          contentColor = AccentMain
        )
      }
    }
  }
}

@Composable
private fun EmptyNotifications() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        imageVector = Icons.Outlined.NotificationsNone,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = TextGrey
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = stringResource(R.string.no_notifications),
        color = TextSecondary,
        fontSize = 14.sp
      )
    }
  }
}
