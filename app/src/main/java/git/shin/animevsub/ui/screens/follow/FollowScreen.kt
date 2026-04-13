package git.shin.animevsub.ui.screens.follow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.grid.GridLoadingSkeleton
import git.shin.animevsub.ui.components.grid.VerticalGridAnimeList
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String, String?) -> Unit,
  viewModel: FollowViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val gridState = rememberLazyGridState()

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.follow),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
    Box(modifier = Modifier.padding(padding)) {
      PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() }
      ) {
        when {
          uiState.isLoading -> GridLoadingSkeleton()
          uiState.error != null && uiState.items.isEmpty() -> {
            ErrorScreen(
              error = uiState.error,
              onRetry = { viewModel.retry() }
            )
          }

          else -> {
            VerticalGridAnimeList(
              items = uiState.items,
              onItemClick = { onNavigateToDetail(it.animeId, it.lastEpisode?.id) },
              state = gridState,
              isLoadingMore = uiState.isLoadingMore,
              onLoadMore = { viewModel.loadMore() }
            )
          }
        }
      }
    }
  }
}
