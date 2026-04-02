package git.shin.animevsub.ui.screens.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.components.status.LoadingScreen
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.AccentMainLight
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.formatShortDayAndDate
import git.shin.animevsub.ui.utils.formatTime
import git.shin.animevsub.ui.utils.isToday
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String) -> Unit,
  viewModel: ScheduleViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
      when {
        uiState.isLoading -> LoadingScreen()
        uiState.error != null -> {
          ErrorScreen(error = uiState.error, onRetry = { viewModel.loadSchedule() })
        }

        else -> {
          // Day tabs
          if (uiState.days.isNotEmpty()) {
            ScrollableTabRow(
              selectedTabIndex = uiState.selectedDay,
              containerColor = DarkBackground,
              contentColor = AccentMain,
              edgePadding = 16.dp,
              divider = {},
              indicator = { tabPositions ->
                if (uiState.selectedDay < tabPositions.size) {
                  TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedDay]),
                    color = AccentMain,
                    height = 2.dp
                  )
                }
              }
            ) {
              uiState.days.forEachIndexed { index, day ->
                val isTodayDay = isToday(day.date)
                val (shortDay, dateStr) = formatShortDayAndDate(day.date)
                val isSelected = uiState.selectedDay == index

                Tab(
                  selected = isSelected,
                  onClick = { viewModel.selectDay(index) },
                  modifier = Modifier.widthIn(min = 0.dp),
                  text = {
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                      Text(
                        text = shortDay,
                        color = if (isSelected) AccentMain
                        else if (isTodayDay) AccentMainLight
                        else TextGrey,
                        fontWeight = if (isSelected || isTodayDay) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                      )
                      Text(
                        text = dateStr,
                        color = if (isSelected) AccentMain
                        else if (isTodayDay) AccentMainLight
                        else TextGrey,
                        fontSize = 10.sp
                      )
                    }
                  }
                )
              }
            }

            // Items for selected day
            val selectedDayData = uiState.days.getOrNull(uiState.selectedDay)
            if (selectedDayData != null) {
              val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
              val groupedItems = remember(selectedDayData) {
                selectedDayData.items.groupBy {
                  if (it.timeRelease != null) formatTime(it.timeRelease * 1000) else "--:--"
                }.toSortedMap()
              }

              LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                groupedItems.forEach { (time, items) ->
                  val itemHour = try {
                    time.split(":")[0].toInt()
                  } catch (e: Exception) {
                    -1
                  }
                  val isCurrentHour = itemHour == currentHour && isToday(selectedDayData.date)

                  item {
                    Text(
                      text = time,
                      color = if (isCurrentHour) MainColor else TextPrimary,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    )
                  }

                  items(items) { item ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                          onNavigateToDetail(item.animeId)
                        }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.Top
                    ) {
                      AsyncImage(
                        model = item.image,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                          .size(85.dp, 120.dp)
                          .clip(RoundedCornerShape(8.dp))
                      )
                      Spacer(modifier = Modifier.width(12.dp))
                      Column(
                        modifier = Modifier.weight(1f)
                      ) {
                        Text(
                          text = item.name,
                          color = TextPrimary,
                          fontSize = 15.sp,
                          fontWeight = FontWeight.Bold,
                          maxLines = 2,
                          overflow = TextOverflow.Ellipsis
                        )

                        val subInfo = listOfNotNull(
                          item.year?.toString(),
                          item.process?.let { stringResource(id = R.string.episode_label, it) }
                        ).joinToString(" | ")

                        if (subInfo.isNotEmpty()) {
                          Text(
                            text = subInfo,
                            color = AccentMain,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                          )
                        }

                        if (!item.description.isNullOrEmpty()) {
                          Text(
                            text = item.description,
                            color = TextGrey,
                            fontSize = 12.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp),
                            lineHeight = 16.sp
                          )
                        }
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
