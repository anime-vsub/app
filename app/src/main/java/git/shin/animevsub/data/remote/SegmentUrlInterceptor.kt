package git.shin.animevsub.data.remote

import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.ServerInfo

fun interface SegmentUrlInterceptor {
  fun intercept(server: ServerInfo, playerData: PlayerData, requestUrl: String): String
}
