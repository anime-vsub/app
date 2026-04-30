package git.shin.animevsub.ui.screens.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.anime.DbNotificationItemRow
import git.shin.animevsub.ui.components.anime.NotificationListSkeleton
import git.shin.animevsub.ui.components.anime.NotificationSkeleton
import git.shin.animevsub.ui.screens.notification.NotificationUiState
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DbNotificationTab(
  uiState: NotificationUiState,
  onRefresh: () -> Unit,
  onLoadMore: () -> Unit,
  onNavigateToDetail: (String, String?) -> Unit,
  onDelete: (String, String?) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onToggleOrder: () -> Unit
) {
  val listState = rememberLazyListState()
  val currentUiState by rememberUpdatedState(uiState)
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)

  LaunchedEffect(listState) {
    snapshotFlow {
      val lastIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
      val size = currentUiState.dbNotifications.size
      val hasMore = currentUiState.hasMoreDb
      val loading = currentUiState.isLoadingDb

      lastIndex != null && lastIndex >= size - 5 && hasMore && !loading
    }
      .distinctUntilChanged()
      .filter { it }
      .collect {
        currentOnLoadMore()
      }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Search Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(DarkSurface)
    ) {
      TextField(
        value = uiState.searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
          Text(
            text = stringResource(R.string.search_hint),
            color = TextSecondary,
            fontSize = 14.sp
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
          )
        },
        trailingIcon = {
          if (uiState.searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChanged("") }) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { /* Handled by query change */ }),
        singleLine = true,
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          disabledContainerColor = Color.Transparent,
          cursorColor = AccentMain,
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent
        )
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (uiState.isAscending) stringResource(R.string.sort_oldest) else stringResource(R.string.sort_newest),
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.weight(1f)
      )
      IconButton(
        onClick = onToggleOrder,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Sort,
          contentDescription = "Toggle Order",
          tint = if (uiState.isAscending) AccentMain else TextPrimary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    PullToRefreshBox(
      isRefreshing = uiState.isRefreshing,
      onRefresh = onRefresh,
      modifier = Modifier.weight(1f)
    ) {
      val items = uiState.dbNotifications

      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
      ) {
        if (items.isEmpty() && uiState.isLoadingDb && !uiState.isRefreshing) {
          item {
            NotificationListSkeleton()
          }
        } else if (items.isEmpty() && !uiState.isLoadingDb) {
          item {
            EmptyNotifications()
          }
        } else {
          items(items, key = { it.season }) { item ->
            Column {
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

          if (uiState.hasMoreDb && uiState.isLoadingDb) {
            item {
              NotificationSkeleton()
            }
          }
        }
      }
    }
  }
}
