package git.shin.animevsub.ui.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.Comment
import git.shin.animevsub.data.model.FilterOption
import git.shin.animevsub.data.model.Trigger
import git.shin.animevsub.data.model.VoteType
import git.shin.animevsub.ui.theme.TextGrey

@Composable
fun BottomSheetDragHandle() {
  Box(
    modifier = Modifier
      .padding(vertical = 12.dp)
      .width(36.dp)
      .height(4.dp)
      .clip(RoundedCornerShape(2.dp))
      .background(TextGrey)
  )
}

@Composable
fun CommentSection(
  comments: List<Comment>,
  totalComments: Int,
  isLoading: Boolean,
  hasMore: Boolean,
  onLoadMore: () -> Unit,
  onVote: (String, VoteType) -> Unit,
  onReply: (String, String) -> Unit,
  onEdit: (String, String) -> Unit,
  onTrigger: (Trigger) -> Unit,
  currentUserId: Int?,
  replies: Map<String, List<Comment>>,
  repliesHasMore: Map<String, Boolean>,
  onLoadReplies: (String, Boolean) -> Unit,
  onPostComment: (String) -> Unit,
  isPosting: Boolean,
  modifier: Modifier = Modifier,
  currentUserAvatar: String? = null,
  sort: FilterOption? = null,
  sortOptions: List<FilterOption> = emptyList(),
  onSortChange: (FilterOption) -> Unit
) {
  var showSortMenu by remember { mutableStateOf(false) }

  Column(modifier = modifier.padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = stringResource(R.string.comments_title),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "$totalComments",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray
      )

      Spacer(modifier = Modifier.weight(1f))

      Box {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { showSortMenu = true }
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = sort?.name ?: sortOptions.firstOrNull()?.name ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        }

        DropdownMenu(
          expanded = showSortMenu,
          onDismissRequest = { showSortMenu = false },
          modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
          sortOptions.forEach { option ->
            DropdownMenuItem(
              text = {
                Text(
                  text = option.name,
                  fontSize = 14.sp
                )
              },
              onClick = {
                onSortChange(option)
                showSortMenu = false
              }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    CommentInput(
      onPost = onPostComment,
      isPosting = isPosting,
      userAvatar = currentUserAvatar
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (comments.isEmpty() && !isLoading) {
      Text(
        text = stringResource(R.string.no_comments),
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 16.dp)
      )
    }

    comments.forEach { comment ->
      CommentItem(
        comment = comment,
        onVote = onVote,
        onReply = onReply,
        onEdit = onEdit,
        onTrigger = onTrigger,
        isMine = comment.userId == currentUserId,
        replies = replies[comment.id] ?: emptyList(),
        hasMoreReplies = repliesHasMore[comment.id]
          ?: (comment.repliesCount > 0 && (replies[comment.id]?.isEmpty() ?: true)),
        onLoadReplies = { onLoadReplies(comment.id, it) },
        currentUserId = currentUserId,
        currentUserAvatar = currentUserAvatar
      )
      HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 0.5.dp,
        color = Color.Gray.copy(alpha = 0.3f)
      )
    }

    if (hasMore) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onLoadMore() }
          .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        if (isLoading) {
          CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
          Text(
            text = stringResource(R.string.view_more_comments),
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    } else if (isLoading && comments.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }
  }
}
