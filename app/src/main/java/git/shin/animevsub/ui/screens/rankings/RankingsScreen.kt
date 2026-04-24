package git.shin.animevsub.ui.screens.rankings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.anime.RankingItem
import git.shin.animevsub.ui.components.anime.RankingSkeleton
import git.shin.animevsub.ui.components.grid.VerticalGridRankingList
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RankingsScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String, String?) -> Unit,
  windowSize: WindowSizeClass,
  viewModel: RankingsViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val rankingTypes = uiState.rankingTypes

  val selectedIndex = rankingTypes.indexOfFirst { it.id == uiState.selectedType }.coerceAtLeast(0)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.rankings),
            color = TextPrimary
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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      if (rankingTypes.isNotEmpty()) {
        val pagerState = rememberPagerState(initialPage = selectedIndex) { rankingTypes.size }
        val scope = rememberCoroutineScope()

        // Sync pager with ViewModel when swiping
        LaunchedEffect(pagerState.currentPage) {
          val typeId = rankingTypes[pagerState.currentPage].id
          if (typeId != uiState.selectedType) {
            viewModel.loadRankings(typeId)
          }
        }

        // Sync pager when selectedIndex changes
        LaunchedEffect(selectedIndex) {
          if (selectedIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(selectedIndex)
          }
        }

        ScrollableTabRow(
          selectedTabIndex = pagerState.currentPage,
          containerColor = DarkBackground,
          contentColor = AccentMain,
          edgePadding = 16.dp,
          indicator = { tabPositions ->
            if (pagerState.currentPage < tabPositions.size) {
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                color = AccentMain
              )
            }
          }
        ) {
          rankingTypes.forEachIndexed { index, type ->
            Tab(
              selected = pagerState.currentPage == index,
              onClick = {
                scope.launch {
                  pagerState.animateScrollToPage(index)
                }
              },
              text = {
                Text(
                  text = type.name,
                  color = if (pagerState.currentPage == index) AccentMain else TextGrey
                )
              }
            )
          }
        }

        HorizontalPager(
          state = pagerState,
          modifier = Modifier.weight(1f)
        ) {
          RankingsListContent(
            uiState = uiState,
            windowSize = windowSize,
            onNavigateToDetail = onNavigateToDetail,
            onRetry = { viewModel.retry() }
          )
        }
      } else {
        // Initial loading of ranking types
        val columns = when (windowSize.widthSizeClass) {
          WindowWidthSizeClass.Compact -> 1
          WindowWidthSizeClass.Medium -> 2
          else -> 3
        }
        when {
          uiState.isLoading -> RankingLoadingList(columns = columns)
          uiState.error != null || uiState.errorRes != null -> {
            ErrorScreen(
              error = uiState.error ?: uiState.errorRes?.let { stringResource(it) },
              onRetry = { viewModel.retry() }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun RankingsListContent(
  uiState: RankingsUiState,
  windowSize: WindowSizeClass,
  onNavigateToDetail: (String, String?) -> Unit,
  onRetry: () -> Unit
) {
  val columns = when (windowSize.widthSizeClass) {
    WindowWidthSizeClass.Compact -> 1
    WindowWidthSizeClass.Medium -> 2
    else -> 3
  }

  when {
    uiState.isLoading && uiState.items.isEmpty() -> RankingLoadingList(columns = columns)
    (uiState.error != null || uiState.errorRes != null) && uiState.items.isEmpty() -> {
      ErrorScreen(
        error = uiState.error ?: uiState.errorRes?.let { stringResource(it) },
        onRetry = onRetry
      )
    }

    else -> {
      if (columns > 1) {
        Box(modifier = Modifier.fillMaxSize()) {
          VerticalGridRankingList(
            items = uiState.items,
            columns = columns,
            onItemClick = { onNavigateToDetail(it.animeId, it.lastEpisode?.id) }
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          itemsIndexed(uiState.items) { index, item ->
            RankingItem(
              rank = index + 1,
              item = item,
              onClick = {
                onNavigateToDetail(item.animeId, item.lastEpisode?.id)
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun RankingLoadingList(columns: Int) {
  if (columns > 1) {
    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
      columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      userScrollEnabled = false
    ) {
      items(12) {
        RankingSkeleton()
      }
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      userScrollEnabled = false
    ) {
      items(10) {
        RankingSkeleton()
      }
    }
  }
}
