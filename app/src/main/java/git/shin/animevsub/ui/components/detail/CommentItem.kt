package git.shin.animevsub.ui.components.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import git.shin.animevsub.R
import git.shin.animevsub.data.model.Comment
import git.shin.animevsub.data.model.Trigger
import git.shin.animevsub.data.model.VoteType
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommentContent(
  content: String,
  modifier: Modifier = Modifier,
  style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
  color: Color = Color.Unspecified,
  maxLines: Int = Int.MAX_VALUE,
  overflow: TextOverflow = TextOverflow.Clip,
  stickerSize: Dp = 80.dp
) {
  val stickerRegex = Regex("\\[sticker:(.*?)\\]")
  val parts = remember(content) {
    val result = mutableListOf<Pair<String, Boolean>>() // text, isSticker
    var lastIndex = 0
    stickerRegex.findAll(content).forEach { match ->
      if (match.range.first > lastIndex) {
        result.add(content.substring(lastIndex, match.range.first) to false)
      }
      result.add(match.groupValues[1] to true)
      lastIndex = match.range.last + 1
    }
    if (lastIndex < content.length) {
      result.add(content.substring(lastIndex) to false)
    }
    result
  }

  if (parts.none { it.second }) {
    Text(
      text = content,
      style = style,
      color = if (color == Color.Unspecified) MaterialTheme.colorScheme.onSurface else color,
      maxLines = maxLines,
      overflow = overflow,
      modifier = modifier
    )
  } else {
    FlowRow(
      modifier = modifier,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalArrangement = Arrangement.Center
    ) {
      for ((part, isSticker) in parts) {
        if (isSticker) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(part)
              .crossfade(true)
              .build(),
            contentDescription = "Sticker",
            modifier = Modifier
              .size(stickerSize)
              .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Fit
          )
        } else {
          if (part.isNotEmpty()) {
            Text(
              text = part,
              style = style,
              color = if (color == Color.Unspecified) MaterialTheme.colorScheme.onSurface else color,
              maxLines = maxLines,
              overflow = overflow
            )
          }
        }
      }
    }
  }
}

@Composable
fun CommentItem(
  comment: Comment,
  onVote: (String, VoteType) -> Unit,
  onReply: (String, String) -> Unit,
  onEdit: (String, String) -> Unit,
  onTrigger: (Trigger) -> Unit,
  replies: List<Comment>,
  hasMoreReplies: Boolean,
  onLoadReplies: (Boolean) -> Unit,
  isReply: Boolean = false,
  isMine: Boolean = false,
  currentUserId: Int? = null,
  currentUserAvatar: String? = null
) {
  var showReplyInput by remember { mutableStateOf(false) }
  var showEditInput by remember { mutableStateOf(false) }
  var showMenu by remember { mutableStateOf(false) }

  Row(modifier = Modifier.fillMaxWidth()) {
    AsyncImage(
      model = ImageRequest.Builder(LocalContext.current)
        .data(comment.userAvatar)
        .crossfade(true)
        .build(),
      contentDescription = null,
      modifier = Modifier
        .size(if (isReply) 28.dp else 36.dp)
        .clip(CircleShape),
      contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = comment.userName,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = formatCommentTime(comment.createdAt),
          style = MaterialTheme.typography.bodySmall,
          color = Color.Gray
        )
        if (comment.isPinned || comment.isGlobalPinned) {
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.Default.PushPin,
            contentDescription = "Pinned",
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = stringResource(if (comment.isGlobalPinned) R.string.global_pinned else R.string.pinned),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.weight(1f))

        Box {
          IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "More",
              modifier = Modifier.size(16.dp),
              tint = Color.Gray
            )
          }
          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            if (isMine) {
              DropdownMenuItem(
                text = { Text(stringResource(R.string.edit)) },
                onClick = {
                  showEditInput = true
                  showMenu = false
                }
              )
            }
            comment.triggers.forEach { trigger ->
              DropdownMenuItem(
                text = { Text(trigger.name ?: trigger.id) },
                onClick = {
                  onTrigger(trigger)
                  showMenu = false
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      if (showEditInput) {
        CommentInput(
          onPost = {
            onEdit(comment.id, it)
            showEditInput = false
          },
          isPosting = false,
          initialText = comment.content,
          userAvatar = currentUserAvatar,
          onCancel = { showEditInput = false }
        )
      } else {
        CommentContent(
          content = comment.content
        )
        if (comment.editedAt != null) {
          Text(
            text = "(${stringResource(R.string.edited)})",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onVote(comment.id, VoteType.UP) }, modifier = Modifier.size(24.dp)) {
          Icon(
            imageVector = if (comment.userVote == VoteType.UP) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
            contentDescription = "Like",
            tint = if (comment.userVote == VoteType.UP) MaterialTheme.colorScheme.primary else LocalContentColor.current,
            modifier = Modifier.size(18.dp)
          )
        }
        Text(
          text = if (comment.votesUp > 0) comment.votesUp.toString() else "",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = { onVote(comment.id, VoteType.DOWN) },
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = if (comment.userVote == VoteType.DOWN) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
            contentDescription = "Dislike",
            tint = if (comment.userVote == VoteType.DOWN) MaterialTheme.colorScheme.primary else LocalContentColor.current,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
          text = stringResource(R.string.reply_label),
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.clickable { showReplyInput = !showReplyInput }
        )
      }

      if (showReplyInput) {
        Spacer(modifier = Modifier.height(8.dp))
        CommentInput(
          onPost = {
            onReply(comment.id, it)
            showReplyInput = false
          },
          isPosting = false,
          placeholder = stringResource(R.string.reply_hint, comment.userName),
          initialText = "",
          userAvatar = currentUserAvatar,
          onCancel = { showReplyInput = false }
        )
      }

      if (replies.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        replies.forEach { reply ->
          CommentItem(
            comment = reply,
            onVote = onVote,
            onReply = onReply,
            onEdit = onEdit,
            onTrigger = onTrigger,
            isMine = reply.userId == currentUserId,
            replies = emptyList<Comment>(),
            hasMoreReplies = false,
            onLoadReplies = {},
            isReply = true,
            currentUserId = currentUserId,
            currentUserAvatar = currentUserAvatar
          )
        }
      }

      if (hasMoreReplies) {
        TextButton(onClick = { onLoadReplies(replies.isNotEmpty()) }) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (replies.isEmpty()) Icons.Default.ArrowDropDown else Icons.Default.Refresh,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (replies.isEmpty()) {
                stringResource(
                  R.string.view_replies,
                  comment.repliesCount
                )
              } else {
                stringResource(R.string.view_more_replies)
              },
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}

@Composable
private fun formatCommentTime(timestamp: Long): String {
  val now = System.currentTimeMillis() / 1000
  val diff = now - timestamp

  return when {
    diff < 60 -> stringResource(R.string.just_now)
    diff < 3600 -> stringResource(R.string.minutes_ago, diff / 60)
    diff < 86400 -> stringResource(R.string.hours_ago, diff / 3600)
    diff < 2592000 -> stringResource(R.string.days_ago, diff / 86400)
    else -> {
      val date = Date(timestamp * 1000)
      val calendar = Calendar.getInstance()
      calendar.time = date
      "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
        calendar.get(
          Calendar.YEAR
        )
      }"
    }
  }
}
