package git.shin.animevsub.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import git.shin.animevsub.ui.screens.home.HomeScreen
import git.shin.animevsub.ui.screens.login.LoginScreen
import git.shin.animevsub.ui.screens.news.NewsScreen
import git.shin.animevsub.ui.screens.notification.NotificationScreen
import git.shin.animevsub.ui.screens.rankings.RankingsScreen
import git.shin.animevsub.ui.screens.schedule.ScheduleScreen
import git.shin.animevsub.ui.screens.search.SearchScreen
import git.shin.animevsub.ui.theme.*

@Composable
fun AnimeVsubAppUI() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Screen.Search, R.string.nav_search, Icons.Filled.Search, Icons.Outlined.Search),
        BottomNavItem(Screen.News, R.string.nav_news, Icons.Filled.Article, Icons.Outlined.Article),
        BottomNavItem(Screen.Notification, R.string.nav_notification, Icons.Filled.Notifications, Icons.Outlined.Notifications),
        BottomNavItem(Screen.Account, R.string.nav_account, Icons.Filled.Person, Icons.Outlined.Person)
    )

    // Routes where bottom bar should be hidden
    val hideBottomBar = currentDestination?.route?.let { route ->
        route.startsWith("detail") ||
        route == Screen.Rankings.route ||
        route == Screen.Schedule.route ||
        route.startsWith("category") ||
        route == Screen.Login.route ||
        route == Screen.Settings.route ||
        route == Screen.About.route ||
        route == Screen.History.route ||
        route == Screen.Follow.route
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
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = stringResource(item.labelRes)
                                )
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
                    onNavigateToCategory = { type, value ->
                        navController.navigate(Screen.Category.createRoute(type, value))
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

            composable(Screen.News.route) {
                NewsScreen()
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
                    onNavigateToPlaylists = { navController.navigate(Screen.Playlists.route) }
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
                    onNavigateToCategory = { type, value ->
                        navController.navigate(Screen.Category.createRoute(type, value))
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

            // Schedule screen
            composable(Screen.Schedule.route) {
                ScheduleScreen(
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
                    navArgument("typeNormal") { type = NavType.StringType },
                    navArgument("value") { type = NavType.StringType }
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

            // Placeholder screens
            composable(Screen.History.route) {
                PlaceholderScreen(
                    title = stringResource(R.string.history),
                    emoji = "📋",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Follow.route) {
                PlaceholderScreen(
                    title = stringResource(R.string.follow),
                    emoji = "❤️",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                PlaceholderScreen(
                    title = stringResource(R.string.settings),
                    emoji = "⚙️",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Playlists.route) {
                PlaceholderScreen(
                    title = stringResource(R.string.playlists),
                    emoji = "📂",
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(
    title: String,
    emoji: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coming_soon_desc),
                fontSize = 14.sp,
                color = TextGrey
            )
        }
    }
}
