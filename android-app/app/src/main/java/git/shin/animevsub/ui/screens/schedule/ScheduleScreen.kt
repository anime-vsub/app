package git.shin.animevsub.ui.screens.schedule

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.*
import git.shin.animevsub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.schedule),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
            when {
                uiState.isLoading -> LoadingScreen()
                uiState.error != null -> {
                    ErrorScreen(error = uiState.error, onRetry = { viewModel.retry() })
                }
                else -> {
                    // Day tabs
                    if (uiState.days.isNotEmpty()) {
                        ScrollableTabRow(
                            selectedTabIndex = uiState.selectedDay,
                            containerColor = DarkBackground,
                            contentColor = AccentMain,
                            edgePadding = 8.dp,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedDay]),
                                    color = AccentMain
                                )
                            }
                        ) {
                            uiState.days.forEachIndexed { index, day ->
                                Tab(
                                    selected = uiState.selectedDay == index,
                                    onClick = { viewModel.selectDay(index) },
                                    text = {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = day.dayName,
                                                color = if (uiState.selectedDay == index) AccentMain
                                                else if (day.isToday) AccentMainLight
                                                else TextGrey,
                                                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        // Items for selected day
                        val selectedDayData = uiState.days.getOrNull(uiState.selectedDay)
                        if (selectedDayData != null) {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(selectedDayData.items) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkCard)
                                            .clickable {
                                                val seasonId = item.path
                                                    .removePrefix("/phim/")
                                                    .trimEnd('/')
                                                onNavigateToDetail(seasonId)
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = item.image,
                                            contentDescription = item.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(55.dp, 75.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.name,
                                                color = TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            if (!item.chap.isNullOrEmpty()) {
                                                Text(
                                                    text = item.chap,
                                                    color = AccentMain,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            if (!item.timeRelease.isNullOrEmpty()) {
                                                Text(
                                                    text = item.timeRelease,
                                                    color = TextGrey,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                if (selectedDayData.items.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = stringResource(R.string.no_results),
                                                color = TextSecondary,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
