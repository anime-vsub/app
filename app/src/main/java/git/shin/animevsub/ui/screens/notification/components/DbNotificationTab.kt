package git.shin.animevsub.ui.screens.notification.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import git.shin.animevsub.ui.components.anime.DbNotificationItemRow
import git.shin.animevsub.ui.components.anime.NotificationListSkeleton
import git.shin.animevsub.ui.components.anime.NotificationSkeleton
import git.shin.animevsub.ui.screens.notification.NotificationUiState
import git.shin.animevsub.ui.theme.DarkSurface
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DbNotificationTab(
  uiState: NotificationUiState,
  onRefresh: () -> Unit,
  onLoadMore: () -> Unit,
  onNavigateToDetail: (String, String?) -> Unit,
  onDelete: (String, String?) -> Unit
) {
  val listState = rememberLazyListState()

  LaunchedEffect(listState) {
    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
      .filter { it != null && it >= uiState.dbNotifications.size - 5 && uiState.hasMoreDb && !uiState.isLoading }
      .distinctUntilChanged()
      .collect {
        onLoadMore()
      }
  }

  PullToRefreshBox(
    isRefreshing = uiState.isRefreshing,
    onRefresh = onRefresh,
    modifier = Modifier.fillMaxSize()
  ) {
    val items = uiState.dbNotifications

    if (items.isEmpty() && uiState.isLoading && !uiState.isRefreshing) {
      NotificationListSkeleton()
    } else if (items.isEmpty()) {
      EmptyNotifications()
    } else {
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
      ) {
        items(items, key = { "db_${it.season}" }) { item ->
          Column(modifier = Modifier.animateItem()) {
            DbNotificationItemRow(
              item = item,
              onClick = { season, chapId ->
                onNavigateToDetail(season, chapId)
              },
              onDelete = { season, chapId ->
                onDelete(season, chapId)
              }
            )
            HorizontalDivider(
              modifier = Modifier.padding(horizontal = 16.dp),
              thickness = 0.5.dp,
              color = DarkSurface
            )
          }
        }

        if (uiState.hasMoreDb) {
          item {
            NotificationSkeleton()
          }
        }
      }
    }
  }
}
