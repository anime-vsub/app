package git.shin.animevsub.ui.components.detail

data class AiChatMessage(
  val id: String = System.currentTimeMillis().toString(),
  val content: String,
  val isFromUser: Boolean,
  val isLoading: Boolean = false
)
