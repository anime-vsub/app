package git.shin.animevsub.ui.screens.notification.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.SystemNotification
import git.shin.animevsub.data.model.SystemNotificationType
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.formatRelativeTime

@Composable
fun SystemNotificationTab(
  notifications: List<SystemNotification>,
  isLoading: Boolean,
  onMarkRead: (String) -> Unit,
  onDelete: (String) -> Unit,
  onClearAll: () -> Unit,
  onNotificationClick: (SystemNotification) -> Unit
) {
  val ctx = androidx.compose.ui.platform.LocalContext.current
  var isAscending by remember { mutableStateOf(false) }

  val sortedNotifications = notifications.sortedWith(
    compareByDescending<SystemNotification> { it.createdAt }.let {
      if (isAscending) it.reversed() else it
    }
  )

  if (isLoading) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(color = AccentMain)
    }
  } else if (notifications.isEmpty()) {
    EmptySystemNotifications()
  } else {
    Column(modifier = Modifier.fillMaxSize()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = ctx.resources.getQuantityString(R.plurals.notification_count, notifications.size, notifications.size),
          color = TextGrey,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )

        Row {
          IconButton(onClick = { isAscending = !isAscending }) {
            Icon(
              imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
              contentDescription = if (isAscending) "Mới nhất" else "Cũ nhất",
              tint = TextGrey
            )
          }

          IconButton(onClick = onClearAll) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = stringResource(R.string.clear_all_notifications),
              tint = TextGrey
            )
          }
        }
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(sortedNotifications, key = { it.id }) { notification ->
          SystemNotificationItem(
            notification = notification,
            context = ctx,
            onMarkRead = onMarkRead,
            onDelete = onDelete,
            onClick = {
              if (!notification.isRead) onMarkRead(notification.id)
              onNotificationClick(notification)
            }
          )
        }
      }
    }
  }
}

@Composable
private fun SystemNotificationItem(
  notification: SystemNotification,
  context: android.content.Context,
  onMarkRead: (String) -> Unit,
  onDelete: (String) -> Unit,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(if (notification.isRead) DarkCard else DarkSurface)
      .clickable(onClick = onClick)
      .padding(12.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(getTypeColor(notification.type)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = getTypeIcon(notification.type),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = notification.title,
          color = TextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )

        if (!notification.isRead) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(AccentMain)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = notification.body,
        color = TextGrey,
        fontSize = 13.sp,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      if (!notification.imageUrl.isNullOrEmpty()) {
        AsyncImage(
          model = notification.imageUrl,
          contentDescription = null,
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp)),
          contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = formatRelativeTime(context, notification.createdAt),
          color = TextGrey.copy(alpha = 0.7f),
          fontSize = 11.sp
        )

        IconButton(
          onClick = { onDelete(notification.id) },
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete_notification),
            tint = TextGrey.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun getTypeIcon(type: SystemNotificationType): ImageVector = when (type) {
  SystemNotificationType.APP_UPDATE -> Icons.Default.SystemUpdate
  SystemNotificationType.MAINTENANCE -> Icons.Default.Build
  SystemNotificationType.PROMOTION -> Icons.Default.CardGiftcard
  SystemNotificationType.FEATURE -> Icons.Default.AutoAwesome
  SystemNotificationType.BUGFIX -> Icons.Default.BugReport
  SystemNotificationType.SECURITY -> Icons.Default.Security
  SystemNotificationType.GENERAL -> Icons.Default.Campaign
}

@Composable
private fun getTypeColor(type: SystemNotificationType): Color = when (type) {
  SystemNotificationType.APP_UPDATE -> Color(0xFF4CAF50)
  SystemNotificationType.MAINTENANCE -> Color(0xFFFF9800)
  SystemNotificationType.PROMOTION -> Color(0xFFE91E63)
  SystemNotificationType.FEATURE -> Color(0xFF8B85FF)
  SystemNotificationType.BUGFIX -> Color(0xFFFFC107)
  SystemNotificationType.SECURITY -> Color(0xFFF44336)
  SystemNotificationType.GENERAL -> AccentMain
}

@Composable
private fun EmptySystemNotifications() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = Icons.Default.Campaign,
      contentDescription = null,
      tint = TextGrey.copy(alpha = 0.3f),
      modifier = Modifier.size(64.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = stringResource(R.string.no_system_notifications),
      color = TextGrey,
      fontSize = 14.sp
    )
  }
}
