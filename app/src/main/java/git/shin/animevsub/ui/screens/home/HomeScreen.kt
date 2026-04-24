package git.shin.animevsub.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.ui.components.common.SectionHeader
import git.shin.animevsub.ui.components.home.CarouselSection
import git.shin.animevsub.ui.components.home.HomeLoadingSkeleton
import git.shin.animevsub.ui.components.home.QuickLinksRow
import git.shin.animevsub.ui.components.list.GridAnimeList
import git.shin.animevsub.ui.components.list.HorizontalAnimeList
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.utils.ResponsiveUtils

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onNavigateToDetail: (String, String?) -> Unit,
  onNavigateToCategory: (List<SelectedFilter>) -> Unit,
  onNavigateToRankings: (String?) -> Unit,
  onNavigateToSchedule: () -> Unit,
  windowSize: WindowSizeClass,
  viewModel: HomeViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  val columns = ResponsiveUtils.calculateGridColumns(windowSize)

  PullToRefreshBox(
    isRefreshing = uiState.isRefreshing,
    onRefresh = { viewModel.refresh() }
  ) {
    when {
      uiState.isLoading -> HomeLoadingSkeleton(windowSize = windowSize)
      uiState.error != null && uiState.data == null -> {
        ErrorScreen(
          error = uiState.error,
          onRetry = { viewModel.loadHomePage() }
        )
      }

      uiState.data != null -> {
        val data = uiState.data!!
        val hotUpdateListState = rememberLazyListState()

        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
          // Carousel (No padding, no corner radius)
          if (data.carousel.isNotEmpty()) {
            CarouselSection(
              items = data.carousel,
              onItemClick = { anime ->
                onNavigateToDetail(anime.animeId, anime.lastEpisode?.id)
              },
              onNavigateToCategory = onNavigateToCategory,
              windowSize = windowSize
            )
          }

          // Quick links
          QuickLinksRow(
            onCatalogClick = {
              onNavigateToCategory(listOf(SelectedFilter("danh-sach", "all", "Tất cả")))
            },
            onScheduleClick = onNavigateToSchedule,
            onRankingsClick = { onNavigateToRankings(null) }
          )

          // This Season
          if (data.thisSeason.isNotEmpty()) {
            SectionHeader(
              title = stringResource(R.string.this_season),
              onViewAll = {
                onNavigateToCategory(
                  listOf(
                    SelectedFilter(
                      "danh-sach",
                      "anime-moi",
                      "Mới cập nhật"
                    )
                  )
                )
              }
            )
            HorizontalAnimeList(
              items = data.thisSeason,
              onItemClick = { anime ->
                onNavigateToDetail(anime.animeId, anime.lastEpisode?.id)
              }
            )
          }

          // Nominated (Grid)
          if (data.nominate.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
              title = stringResource(R.string.nominate),
              onViewAll = { onNavigateToRankings(null) }
            )
            GridAnimeList(
              items = data.nominate,
              columns = columns,
              onItemClick = { anime ->
                onNavigateToDetail(anime.animeId, anime.lastEpisode?.id)
              }
            )
          }

          // Top / Hot Update
          if (data.hotUpdate.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
              title = stringResource(R.string.top),
              onViewAll = { onNavigateToRankings(null) }
            )
            HorizontalAnimeList(
              items = data.hotUpdate,
              state = hotUpdateListState,
              showRating = true,
              onItemClick = { anime ->
                onNavigateToDetail(anime.animeId, anime.lastEpisode?.id)
              },
              showTrending = true
            )
          }

          // Coming Soon / Pre-release
          if (data.preRelease.isNotEmpty()) {
            val preReleaseListState = rememberLazyListState()

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
              title = stringResource(R.string.coming_soon),
              onViewAll = {
                onNavigateToCategory(
                  listOf(
                    SelectedFilter(
                      "danh-sach",
                      "anime-sap-chieu",
                      "Sắp chiếu"
                    )
                  )
                )
              }
            )

            HorizontalAnimeList(
              items = data.preRelease,
              state = preReleaseListState,
              showTimeline = true,
              onItemClick = { anime ->
                onNavigateToDetail(anime.animeId, anime.lastEpisode?.id)
              }
            )
          }

          // Last Updated (Grid)
          if (data.lastUpdate.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
              title = stringResource(R.string.last_updated),
              onViewAll = {
                onNavigateToCategory(
                  listOf(
                    SelectedFilter(
                      "danh-sach",
                      "anime-moi",
                      "Mới cập nhật"
                    )
                  )
                )
              }
            )
            GridAnimeList(
              items = data.lastUpdate,
              columns = columns,
              onItemClick = { anime ->
                onNavigateToDetail(anime.animeId, anime.lastEpisode?.id)
              }
            )
          }

          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }
}
