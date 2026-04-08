package git.shin.animevsub.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import git.shin.animevsub.R
import git.shin.animevsub.ui.navigation.BottomNavItem
import git.shin.animevsub.ui.navigation.Screen
import git.shin.animevsub.ui.screens.about.AboutScreen
import git.shin.animevsub.ui.screens.account.AccountScreen
import git.shin.animevsub.ui.screens.category.CategoryScreen
import git.shin.animevsub.ui.screens.detail.DetailScreen
import git.shin.animevsub.ui.screens.follow.FollowScreen
import git.shin.animevsub.ui.screens.history.HistoryScreen
import git.shin.animevsub.ui.screens.home.HomeScreen
import git.shin.animevsub.ui.screens.login.LoginScreen
import git.shin.animevsub.ui.screens.notification.NotificationScreen
import git.shin.animevsub.ui.screens.notification.NotificationViewModel
import git.shin.animevsub.ui.screens.playlist.PlaylistScreen
import git.shin.animevsub.ui.screens.rankings.RankingsScreen
import git.shin.animevsub.ui.screens.schedule.ScheduleScreen
import git.shin.animevsub.ui.screens.search.SearchScreen
import git.shin.animevsub.ui.screens.settings.SettingsScreen
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun AnimeVsubAppUI(
  notificationViewModel: NotificationViewModel = hiltViewModel()
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  val notificationUiState by notificationViewModel.uiState.collectAsState()
  val unreadCount = notificationUiState.data?.items?.size ?: 0

  val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Search, R.string.nav_search, Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem(
      Screen.Schedule,
      R.string.schedule,
      Icons.Default.CalendarMonth,
      Icons.Outlined.CalendarMonth
    ),
    BottomNavItem(
      Screen.Notification,
      R.string.nav_notification,
      Icons.Filled.Notifications,
      Icons.Outlined.Notifications
    ),
    BottomNavItem(Screen.Account, R.string.nav_account, Icons.Filled.Person, Icons.Outlined.Person)
  )

  // Routes where bottom bar should be hidden
  val hideBottomBar = currentDestination?.route?.let { route ->
    route.startsWith("detail") ||
      route == Screen.Rankings.route ||
      route.startsWith("category") ||
      route == Screen.Login.route ||
      route == Screen.Settings.route ||
      route == Screen.About.route ||
      route == Screen.History.route ||
      route == Screen.Follow.route ||
      route.startsWith("playlist")
  } ?: false

  Scaffold(
    containerColor = DarkBackground,
    bottomBar = {
      if (!hideBottomBar) {
        NavigationBar(
          containerColor = DarkSurface,
          contentColor = TextPrimary,
          tonalElevation = 0.dp
        ) {
          bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
              it.route == item.screen.route
            } == true

            NavigationBarItem(
              icon = {
                BadgedBox(
                  badge = {
                    if (item.screen == Screen.Notification && unreadCount > 0) {
                      Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                      ) {
                        Text(if (unreadCount > 99) "99+" else unreadCount.toString())
                      }
                    }
                  }
                ) {
                  Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = stringResource(item.labelRes)
                  )
                }
              },
              label = {
                Text(
                  text = stringResource(item.labelRes),
                  fontSize = 11.sp
                )
              },
              selected = selected,
              onClick = {
                navController.navigate(item.screen.route) {
                  popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                  }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentMain,
                selectedTextColor = AccentMain,
                unselectedIconColor = TextGrey,
                unselectedTextColor = TextGrey,
                indicatorColor = Color.Transparent
              )
            )
          }
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Home.route,
      modifier = Modifier.padding(innerPadding)
    ) {
      // Bottom nav destinations
      composable(Screen.Home.route) {
        HomeScreen(
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          },
          onNavigateToCategory = { filters ->
            val filtersJson = Json.encodeToString(filters)
            navController.navigate(Screen.Category.createRoute(filtersJson))
          },
          onNavigateToRankings = { type ->
            navController.navigate(Screen.Rankings.createRoute(type))
          },
          onNavigateToSchedule = {
            navController.navigate(Screen.Schedule.route)
          }
        )
      }

      composable(Screen.Search.route) {
        SearchScreen(
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          }
        )
      }

      composable(Screen.Schedule.route) {
        val isFromBottomNav = bottomNavItems.any { it.screen == Screen.Schedule }
        ScheduleScreen(
          onNavigateBack = if (isFromBottomNav) null else {
            { navController.popBackStack() }
          },
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          }
        )
      }

      composable(Screen.Notification.route) {
        NotificationScreen(
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          },
          onNavigateToLogin = {
            navController.navigate(Screen.Login.route)
          }
        )
      }

      composable(Screen.Account.route) {
        AccountScreen(
          onNavigateToLogin = { navController.navigate(Screen.Login.route) },
          onNavigateToHistory = { navController.navigate(Screen.History.route) },
          onNavigateToFollow = { navController.navigate(Screen.Follow.route) },
          onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
          onNavigateToAbout = { navController.navigate(Screen.About.route) },
          onNavigateToPlaylist = { playlistId ->
            navController.navigate(Screen.Playlist.createRoute(playlistId))
          },
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          },
          onNavigateToPlayer = { animeId, chapterId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId, chapterId))
          }
        )
      }

      // Detail screen (Now includes Player)
      composable(
        route = Screen.AnimeDetail.route,
        arguments = listOf(
          navArgument("animeId") { type = NavType.StringType },
          navArgument("chapterId") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
          }
        )
      ) {
        DetailScreen(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          },
          onNavigateToCategory = { filters ->
            val filtersJson = Json.encodeToString(filters)
            navController.navigate(Screen.Category.createRoute(filtersJson))
          }
        )
      }

      // Rankings screen
      composable(
        route = Screen.Rankings.route,
        arguments = listOf(
          navArgument("type") {
            type = NavType.StringType
            defaultValue = "day"
          }
        )
      ) {
        RankingsScreen(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          }
        )
      }

      // Category screen
      composable(
        route = Screen.Category.route,
        arguments = listOf(
          navArgument("filters") { type = NavType.StringType }
        )
      ) {
        CategoryScreen(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          }
        )
      }

      // Login screen
      composable(Screen.Login.route) {
        LoginScreen(
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // About screen
      composable(Screen.About.route) {
        AboutScreen(
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // History screen
      composable(Screen.History.route) {
        HistoryScreen(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          }
        )
      }

      composable(Screen.Follow.route) {
        FollowScreen(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          }
        )
      }

      composable(Screen.Settings.route) {
        SettingsScreen(
          onNavigateBack = { navController.popBackStack() }
        )
      }

      composable(
        route = Screen.Playlist.route,
        arguments = listOf(
          navArgument("playlistId") { type = NavType.StringType }
        )
      ) {
        PlaylistScreen(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToDetail = { animeId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId))
          },
          onNavigateToPlayer = { animeId, chapterId ->
            navController.navigate(Screen.AnimeDetail.createRoute(animeId, chapterId))
          }
        )
      }
    }
  }
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun PlaceholderScreen(
//  title: String,
//  icon: androidx.compose.ui.graphics.vector.ImageVector,
//  onNavigateBack: () -> Unit
//) {
//  Scaffold(
//    topBar = {
//      TopAppBar(
//        title = { Text(text = title, color = TextPrimary) },
//        navigationIcon = {
//          IconButton(onClick = onNavigateBack) {
//            Icon(
//              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//              contentDescription = stringResource(R.string.back),
//              tint = TextPrimary
//            )
//          }
//        },
//        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
//      )
//    },
//    containerColor = DarkBackground
//  ) { padding ->
//    Column(
//      modifier = Modifier
//        .fillMaxSize()
//        .padding(padding),
//      horizontalAlignment = Alignment.CenterHorizontally,
//      verticalArrangement = Arrangement.Center
//    ) {
//      Icon(
//        imageVector = icon,
//        contentDescription = null,
//        modifier = Modifier.size(64.dp),
//        tint = TextGrey
//      )
//      Spacer(modifier = Modifier.height(16.dp))
//      Text(
//        text = title,
//        fontSize = 20.sp,
//        fontWeight = FontWeight.Bold,
//        color = TextPrimary
//      )
//      Spacer(modifier = Modifier.height(8.dp))
//      Text(
//        text = stringResource(R.string.coming_soon_desc),
//        fontSize = 14.sp,
//        color = TextGrey
//      )
//    }
//  }
//}
