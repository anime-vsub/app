package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import git.shin.animevsub.data.model.NotificationItem
import git.shin.animevsub.data.model.Trigger
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.formatTimeAgo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItemRow(
  notification: NotificationItem,
  onClick: () -> Unit,
  onClose: (Trigger) -> Unit
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
          color = TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
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
//        Box(
//          modifier = Modifier
//            .padding(top = 4.dp)
//            .size(10.dp)
//            .clip(CircleShape)
//            .background(AccentMain)
//        )

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
