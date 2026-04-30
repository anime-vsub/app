package git.shin.animevsub.ui.screens.notification.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import git.shin.animevsub.data.model.Trigger
import git.shin.animevsub.ui.components.anime.NotificationItemRow
import git.shin.animevsub.ui.components.anime.NotificationListSkeleton
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.screens.notification.NotificationUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiNotificationTab(
  uiState: NotificationUiState,
  onRefresh: () -> Unit,
  onNavigateToDetail: (String, String?) -> Unit,
  onTrigger: (Trigger) -> Unit,
  onRetry: () -> Unit
) {
  PullToRefreshBox(
    isRefreshing = uiState.isRefreshing,
    onRefresh = onRefresh,
    modifier = Modifier.fillMaxSize()
  ) {
    val items = uiState.data?.items ?: emptyList()

    if (uiState.isLoading && !uiState.isRefreshing && items.isEmpty()) {
      NotificationListSkeleton()
    } else if (uiState.error != null && items.isEmpty()) {
      ErrorScreen(error = uiState.error, onRetry = onRetry)
    } else if (items.isEmpty()) {
      EmptyNotifications()
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
      ) {
        items(items, key = { "api_${it.id}" }) { notification ->
          NotificationItemRow(
            notification = notification,
            onClick = {
              notification.animeId?.let { id -> onNavigateToDetail(id, null) }
            },
            onClose = { trigger ->
              onTrigger(trigger)
            }
          )
        }
      }
    }
  }
}
