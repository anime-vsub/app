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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.Comment
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
  onVote: (String, Int) -> Unit,
  onReply: (String, String) -> Unit,
  onDelete: (String, String) -> Unit,
  onEdit: (String, String) -> Unit,
  onReport: (String) -> Unit,
  currentUserId: Int?,
  replies: Map<String, List<Comment>>,
  repliesHasMore: Map<String, Boolean>,
  onLoadReplies: (String, Boolean) -> Unit,
  onPostComment: (String) -> Unit,
  isPosting: Boolean,
  currentUserAvatar: String? = null,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.padding(16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
        onDelete = onDelete,
        onEdit = onEdit,
        onReport = onReport,
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
          .padding(24.dp), contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }
  }
}
