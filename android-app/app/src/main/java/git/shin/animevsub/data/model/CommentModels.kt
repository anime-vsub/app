package git.shin.animevsub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
  val id: String,
  @SerialName("user_id") val userId: Int,
  @SerialName("user_name") val userName: String,
  @SerialName("user_avatar") val userAvatar: String,
  val content: String,
  @SerialName("is_spoiler") val isSpoiler: Int = 0,
  @SerialName("is_pending") val isPending: Int = 0,
  @SerialName("is_pinned") val isPinned: Int = 0,
  @SerialName("is_global_pinned") val isGlobalPinned: Int = 0,
  @SerialName("created_at") val createdAt: Long,
  @SerialName("edited_at") val editedAt: Long? = null,
  @SerialName("votes_up") val votesUp: Int = 0,
  @SerialName("votes_down") val votesDown: Int = 0,
  @SerialName("replies_count") val repliesCount: Int = 0,
  @SerialName("user_vote") val userVote: Int = 0, // 1: up, -1: down, 0: none
  val badges: List<CommentBadge> = emptyList(),
  @SerialName("thread_key") val threadKey: String? = null,
  @SerialName("parent_id") val parentId: String? = null,
  @SerialName("is_hidden") val isHidden: Int = 0,
  @SerialName("hide_reason") val hideReason: String? = null
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
  @SerialName("has_more") val hasMore: Boolean = false,
  val error: String? = null
)

@Serializable
data class ReplyResponse(
  val success: Boolean,
  val replies: List<Comment> = emptyList(),
  val total: Int = 0,
  val offset: Int = 0,
  @SerialName("has_more") val hasMore: Boolean = false,
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
  @SerialName("votes_up") val votesUp: Int = 0,
  @SerialName("votes_down") val votesDown: Int = 0,
  val error: String? = null
)

@Serializable
data class EditCommentResponse(
  val success: Boolean,
  val content: String? = null,
  @SerialName("is_spoiler") val isSpoiler: Int = 0,
  @SerialName("edited_at") val editedAt: Long? = null,
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
