package git.shin.animevsub.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.ui.components.anime.SkeletonCard
import git.shin.animevsub.ui.components.badge.QualityBadge
import git.shin.animevsub.ui.components.common.SectionHeader
import git.shin.animevsub.ui.components.list.GridAnimeList
import git.shin.animevsub.ui.components.list.HorizontalAnimeList
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.StarColor
import git.shin.animevsub.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onNavigateToDetail: (String) -> Unit,
  onNavigateToCategory: (List<SelectedFilter>) -> Unit,
  onNavigateToRankings: (String?) -> Unit,
  onNavigateToSchedule: () -> Unit,
  viewModel: HomeViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  when {
    uiState.isLoading -> HomeLoadingSkeleton()
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
              onNavigateToDetail(anime.animeId)
            }
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
              onNavigateToCategory(listOf(SelectedFilter("danh-sach", "anime-moi", "Mới cập nhật")))
            }
          )
          HorizontalAnimeList(
            items = data.thisSeason,
            onItemClick = { anime ->
              onNavigateToDetail(anime.animeId)
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
            onItemClick = { anime ->
              onNavigateToDetail(anime.animeId)
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
              onNavigateToDetail(anime.animeId)
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
              onNavigateToDetail(anime.animeId)
            }
          )
        }

        // Last Updated (Grid)
        if (data.lastUpdate.isNotEmpty()) {
          Spacer(modifier = Modifier.height(16.dp))
          SectionHeader(
            title = stringResource(R.string.last_updated),
            onViewAll = {
              onNavigateToCategory(listOf(SelectedFilter("danh-sach", "anime-moi", "Mới cập nhật")))
            }
          )
          GridAnimeList(
            items = data.lastUpdate,
            onItemClick = { anime ->
              onNavigateToDetail(anime.animeId)
            }
          )
        }

        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CarouselSection(
  items: List<AnimeCard>,
  onItemClick: (AnimeCard) -> Unit
) {
  val pagerState = rememberPagerState(pageCount = { items.size })

  // Auto-scroll
  LaunchedEffect(pagerState) {
    while (true) {
      delay(5000)
      if (items.isNotEmpty()) {
        val nextPage = (pagerState.currentPage + 1) % items.size
        pagerState.animateScrollToPage(nextPage)
      }
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    HorizontalPager(
      state = pagerState,
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 0.dp),
      pageSpacing = 0.dp
    ) { page ->
      val item = items[page]
      val pageOffset = (
        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        ).absoluteValue

      val scale = 1f - (pageOffset * 0.02f).coerceIn(0f, 0.02f)
      val alpha = 1f - (pageOffset * 0.05f).coerceIn(0f, 0.05f)

      Box(
        modifier = Modifier
          .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
          }
          .aspectRatio(16f / 9.5f)
          .clickable { onItemClick(item) }
      ) {
        AsyncImage(
          model = item.image,
          contentDescription = item.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Improved Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.2f),
                  Color.Black.copy(alpha = 0.95f)
                )
              )
            )
        )

        // Info
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
        ) {
          if (!item.quality.isNullOrEmpty()) {
            QualityBadge(quality = item.quality, isCarousel = true)
            Spacer(modifier = Modifier.height(6.dp))
          }

          Text(
            text = item.name,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(4.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            if (item.rate > 0) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = StarColor,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = String.format("%.1f", item.rate),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = " | ",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
              )
            }

            val metaInfo = mutableListOf<String>()
            item.year?.let { metaInfo.add(it.toString()) }
            item.process?.let { metaInfo.add(it) }

            Text(
              text = metaInfo.joinToString(" | "),
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 12.sp
            )
          }

          if (item.genre.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = item.genre.joinToString(", ") { it.name },
              color = MainColor,
              fontSize = 12.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          if (!item.description.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = item.description,
              color = Color.White.copy(alpha = 0.7f),
              fontSize = 12.sp,
              maxLines = 2,
              lineHeight = 16.sp,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    // Indicators
    Row(
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(vertical = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      repeat(items.size) { index ->
        val isSelected = pagerState.currentPage == index
        Box(
          modifier = Modifier
            .height(6.dp)
            .width(if (isSelected) 20.dp else 6.dp)
            .clip(CircleShape)
            .background(
              if (isSelected) MainColor
              else Color.White.copy(alpha = 0.3f)
            )
        )
      }
    }
  }
}

@Composable
private fun QuickLinksRow(
  onCatalogClick: () -> Unit,
  onScheduleClick: () -> Unit,
  onRankingsClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.Center
  ) {
    QuickLinkItem(
      label = stringResource(R.string.catalog),
      icon = painterResource(id = R.drawable.icon_tool_alp),
      onClick = onCatalogClick,
      modifier = Modifier.weight(1f)
    )
    QuickLinkItem(
      label = stringResource(R.string.schedule),
      icon = painterResource(id = R.drawable.icon_tool_calc),
      onClick = onScheduleClick,
      modifier = Modifier.weight(1f)
    )
    QuickLinkItem(
      label = stringResource(R.string.rankings),
      icon = painterResource(id = R.drawable.icon_tool_rank),
      onClick = onRankingsClick,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun QuickLinkItem(
  label: String,
  icon: Painter,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(8.dp)
  ) {
    Image(
      painter = icon,
      contentDescription = label,
      modifier = Modifier.size(32.dp)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      color = TextPrimary,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun HomeLoadingSkeleton() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    // Carousel skeleton (Full width)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9.5f)
        .background(DarkCard)
    )

    // Quick links skeleton
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      repeat(3) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(DarkCard)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .width(60.dp)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(DarkCard)
          )
        }
      }
    }

    // This Season (Horizontal Scroll)
    SectionSkeleton()
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      userScrollEnabled = false
    ) {
      items(5) {
        SkeletonCard(modifier = Modifier.width(110.dp))
      }
    }

    // Nominated (Grid 3 columns)
    Spacer(modifier = Modifier.height(24.dp))
    SectionSkeleton()
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
      repeat(2) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          repeat(3) {
            SkeletonCard(modifier = Modifier.weight(1f))
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    // Timeline skeleton
    Spacer(modifier = Modifier.height(16.dp))
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(20.dp),
      userScrollEnabled = false
    ) {
      items(5) {
        Box(
          modifier = Modifier
            .width(80.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(DarkCard)
        )
      }
    }

    Spacer(modifier = Modifier.height(100.dp))
  }
}

@Composable
private fun SectionSkeleton() {
  Box(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .width(120.dp)
      .height(20.dp)
      .clip(RoundedCornerShape(4.dp))
      .background(DarkCard)
  )
}
