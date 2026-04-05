package git.shin.animevsub.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.model.NotificationItem
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.components.status.LoadingScreen
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.formatTimeAgo

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
          uiState.isLoading && !uiState.isRefreshing -> LoadingScreen()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItemRow(
  notification: NotificationItem,
  onClick: () -> Unit,
  onClose: (git.shin.animevsub.data.model.Trigger) -> Unit
) {
  val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = {
      if (it == SwipeToDismissBoxValue.EndToStart) {
        notification.closeTrigger?.let { trigger ->
          onClose(trigger)
          true
        } ?: false
      } else false
    }
  )

  SwipeToDismissBox(
    state = dismissState,
    enableDismissFromStartToEnd = false,
    enableDismissFromEndToStart = notification.closeTrigger != null,
    backgroundContent = {
      val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
        else -> Color.Transparent
      }
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(color)
          .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Delete",
          tint = Color.White
        )
      }
    }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(DarkBackground)
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.Top
    ) {
      AsyncImage(
        model = notification.image ?: notification.avatar,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .width(120.dp)
          .aspectRatio(16f / 9f)
          .clip(RoundedCornerShape(8.dp))
          .background(DarkSurface)
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = notification.title,
          color = TextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = notification.description.ifEmpty { notification.content },
          color = if (notification.isRead) TextSecondary else TextPrimary,
          fontSize = 14.sp,
          fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Medium,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = formatTimeAgo(notification.createdAt),
          color = TextGrey,
          fontSize = 12.sp
        )
      }

      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.align(Alignment.Top)
      ) {
        if (!notification.isRead) {
          Box(
            modifier = Modifier
              .padding(top = 4.dp)
              .size(10.dp)
              .clip(CircleShape)
              .background(AccentMain)
          )
        }

        notification.closeTrigger?.let { trigger ->
          IconButton(
            onClick = { onClose(trigger) },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextGrey,
              modifier = Modifier.size(16.dp)
            )
          }
        }
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
