package git.shin.animevsub.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.history.HistoryItemRow
import git.shin.animevsub.ui.components.history.HistoryItemRowSkeleton
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String) -> Unit,
  viewModel: HistoryViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val listState = rememberLazyListState()

  val shouldLoadMore = remember {
    derivedStateOf {
      val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
      lastVisibleItemIndex >= listState.layoutInfo.totalItemsCount - 5
    }
  }

  LaunchedEffect(shouldLoadMore.value) {
    if (shouldLoadMore.value && !uiState.isLoadingMore && uiState.hasMore) {
      viewModel.loadMore()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.history),
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
      when {
        uiState.isLoading -> {
          LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(10) { HistoryItemRowSkeleton() }
          }
        }

        uiState.error != null && uiState.groupedItems.isEmpty() -> {
          ErrorScreen(
            error = uiState.error,
            onRetry = { viewModel.retry() }
          )
        }

        uiState.groupedItems.isEmpty() -> {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
          ) {
            Text(text = stringResource(R.string.no_history), color = TextGrey)
          }
        }

        else -> {
          LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
          ) {
            uiState.groupedItems.forEach { (dateStr, items) ->
              item {
                HistoryDateHeader(dateStr)
              }
              items(items) { item ->
                HistoryItemRow(
                  item = item,
                  onClick = { onNavigateToDetail(item.seasonId) }
                )
              }
            }

            if (uiState.isLoadingMore) {
              item {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                  contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                  CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun HistoryDateHeader(dateStr: String) {
  val date = LocalDate.parse(dateStr)
  val today = LocalDate.now()
  val yesterday = today.minusDays(1)

  val headerText = when (date) {
    today -> stringResource(R.string.today)
    yesterday -> stringResource(R.string.yesterday)
    else -> stringResource(R.string.history_date_format, date.dayOfMonth, date.monthValue)
  }

  Text(
    text = headerText,
    color = TextPrimary,
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  )
}
