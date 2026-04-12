package git.shin.animevsub.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class VoteType {
  UP, DOWN, NONE
}

@Serializable
data class Comment(
  val id: String,
  val userId: Int,
  val userName: String,
  val userAvatar: String,
  val content: String,
  val isSpoiler: Boolean = false,
  val isPending: Boolean = false,
  val isPinned: Boolean = false,
  val isGlobalPinned: Boolean = false,
  val createdAt: Long,
  val editedAt: Long? = null,
  val votesUp: Int = 0,
  val votesDown: Int = 0,
  val repliesCount: Int = 0,
  val userVote: VoteType = VoteType.NONE,
  val badges: List<CommentBadge> = emptyList(),
  val threadKey: String? = null,
  val parentId: String? = null,
  val isHidden: Boolean = false,
  val hideReason: String? = null,
  val triggers: List<Trigger> = emptyList()
)

@Serializable
data class CommentBadge(
  val name: String,
  val icon: String,
  val color: String
)

@Serializable
data class CommentResponse(
  val success: Boolean,
  val comments: List<Comment> = emptyList(),
  val total: Int = 0,
  val offset: Int = 0,
  val hasMore: Boolean = false,
  val error: String? = null
)

@Serializable
data class ReplyResponse(
  val success: Boolean,
  val replies: List<Comment> = emptyList(),
  val total: Int = 0,
  val offset: Int = 0,
  val hasMore: Boolean = false,
  val error: String? = null
)

@Serializable
data class PostCommentResponse(
  val success: Boolean,
  val comment: Comment? = null,
  val total: Int? = null,
  val pending: Boolean = false,
  val error: String? = null
)

@Serializable
data class VoteResponse(
  val success: Boolean,
  val votesUp: Int = 0,
  val votesDown: Int = 0,
  val error: String? = null
)

@Serializable
data class EditCommentResponse(
  val success: Boolean,
  val content: String? = null,
  val isSpoiler: Boolean = false,
  val editedAt: Long? = null,
  val pending: Boolean = false,
  val error: String? = null
)

@Serializable
data class ActionResponse(
  val success: Boolean,
  val message: String? = null,
  val error: String? = null,
  val total: Int? = null
)
