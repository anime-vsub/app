package git.shin.animevsub.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.ui.components.*
import git.shin.animevsub.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCategory: (String, String) -> Unit,
    onNavigateToRankings: (String) -> Unit,
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                    // Carousel
                    if (data.carousel.isNotEmpty()) {
                        CarouselSection(
                            items = data.carousel,
                            onItemClick = { anime ->
                                val seasonId = anime.path.removePrefix("/phim/").trimEnd('/')
                                onNavigateToDetail(seasonId)
                            }
                        )
                    }

                    // Quick links
                    QuickLinksRow(
                        onCatalogClick = { onNavigateToCategory("danh-sach", "all") },
                        onScheduleClick = onNavigateToSchedule,
                        onRankingsClick = { onNavigateToRankings("day") }
                    )

                    // This Season
                    if (data.thisSeason.isNotEmpty()) {
                        HorizontalAnimeList(
                            items = data.thisSeason,
                            onItemClick = { anime ->
                                val seasonId = anime.path.removePrefix("/phim/").trimEnd('/')
                                onNavigateToDetail(seasonId)
                            }
                        )
                    }

                    // Nominated
                    if (data.nominate.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = stringResource(R.string.nominate),
                            onViewAll = { onNavigateToRankings("day") }
                        )
                        GridAnimeList(
                            items = data.nominate.take(6),
                            onItemClick = { anime ->
                                val seasonId = anime.path.removePrefix("/phim/").trimEnd('/')
                                onNavigateToDetail(seasonId)
                            }
                        )
                    }

                    // Top / Hot Update
                    if (data.hotUpdate.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = stringResource(R.string.top),
                            onViewAll = { onNavigateToRankings("day") }
                        )
                        HorizontalAnimeList(
                            items = data.hotUpdate,
                            onItemClick = { anime ->
                                val seasonId = anime.path.removePrefix("/phim/").trimEnd('/')
                                onNavigateToDetail(seasonId)
                            },
                            showTrending = true
                        )
                    }

                    // Coming Soon / Pre-release
                    if (data.preRelease.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = stringResource(R.string.coming_soon),
                            onViewAll = { onNavigateToCategory("danh-sach", "anime-sap-chieu") }
                        )
                        HorizontalAnimeList(
                            items = data.preRelease,
                            onItemClick = { anime ->
                                val seasonId = anime.path.removePrefix("/phim/").trimEnd('/')
                                onNavigateToDetail(seasonId)
                            }
                        )
                    }

                    // Last Updated
                    if (data.lastUpdate.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = stringResource(R.string.last_updated),
                            onViewAll = { onNavigateToCategory("danh-sach", "anime-moi") }
                        )
                        GridAnimeList(
                            items = data.lastUpdate.take(6),
                            onItemClick = { anime ->
                                val seasonId = anime.path.removePrefix("/phim/").trimEnd('/')
                                onNavigateToDetail(seasonId)
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
            val nextPage = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = items[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onItemClick(item) }
            ) {
                AsyncImage(
                    model = item.image,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DarkBackground.copy(alpha = 0.7f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Bottom gradient + info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    DarkBackground.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!item.quality.isNullOrEmpty()) {
                                QualityBadge(quality = item.quality)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = item.name,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.rate > 0) {
                                Text("★", color = StarColor, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", item.rate),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            if (item.year != null) {
                                Text(
                                    text = item.year.toString(),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            if (!item.process.isNullOrEmpty()) {
                                Text(
                                    text = item.process,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (item.genre.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.genre.joinToString(", ") { it.name },
                                color = AccentMain,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(items.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) AccentMain
                            else Color.White.copy(alpha = 0.5f)
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
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickLinkItem(
            label = stringResource(R.string.catalog),
            emoji = "📑",
            onClick = onCatalogClick
        )
        QuickLinkItem(
            label = stringResource(R.string.schedule),
            emoji = "📅",
            onClick = onScheduleClick
        )
        QuickLinkItem(
            label = stringResource(R.string.rankings),
            emoji = "🏆",
            onClick = onRankingsClick
        )
    }
}

@Composable
private fun QuickLinkItem(
    label: String,
    emoji: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun HomeLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 0.dp)
    ) {
        // Carousel skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkCard)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkCard)
                    )
                }
            }
        }

        // Card skeletons
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) { SkeletonCard() }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .width(100.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkCard)
        )

        Spacer(modifier = Modifier.height(12.dp))
        repeat(3) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkCard)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkCard)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkCard)
                    )
                }
            }
        }
    }
}
