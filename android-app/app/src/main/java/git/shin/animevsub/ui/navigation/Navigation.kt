package git.shin.animevsub.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object News : Screen("news")
    data object Notification : Screen("notification")
    data object Account : Screen("account")
    data object AnimeDetail : Screen("detail/{seasonId}") {
        fun createRoute(seasonId: String) = "detail/$seasonId"
    }
    data object Player : Screen("player/{seasonId}/{chapId}/{play}/{hash}") {
        fun createRoute(seasonId: String, chapId: String, play: String, hash: String) =
            "player/$seasonId/$chapId/$play/$hash"
    }
    data object Rankings : Screen("rankings?type={type}") {
        fun createRoute(type: String = "day") = "rankings?type=$type"
    }
    data object Schedule : Screen("schedule")
    data object Category : Screen("category/{typeNormal}/{value}") {
        fun createRoute(typeNormal: String, value: String) = "category/$typeNormal/$value"
    }
    data object History : Screen("history")
    data object Follow : Screen("follow")
    data object Settings : Screen("settings")
    data object PlayerSettings : Screen("settings/player")
    data object LanguageSettings : Screen("settings/language")
    data object About : Screen("about")
    data object Login : Screen("login")
    data object Playlists : Screen("playlists")
    data object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
