package git.shin.animevsub.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
  data object Home : Screen("home")
  data object Search : Screen("search")
  data object Notification : Screen("notification")
  data object Account : Screen("account")
  data object AnimeDetail : Screen("detail/{animeId}?chapterId={chapterId}") {
    fun createRoute(animeId: String, chapterId: String? = null) =
      if (chapterId != null) "detail/$animeId?chapterId=$chapterId" else "detail/$animeId"
  }

  data object Rankings : Screen("rankings?type={type}") {
    fun createRoute(type: String?) = "rankings?type=${type ?: ""}"
  }

  data object Schedule : Screen("schedule")
  data object Category : Screen("category/{filters}") {
    fun createRoute(filters: String) = "category/$filters"
  }

  data object History : Screen("history")
  data object Follow : Screen("follow")
  data object Settings : Screen("settings")
  data object About : Screen("about")
  data object Login : Screen("login")
  data object Playlist : Screen("playlist/{playlistId}") {
    fun createRoute(playlistId: String) = "playlist/$playlistId"
  }
}

data class BottomNavItem(
  val screen: Screen,
  val labelRes: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
)
