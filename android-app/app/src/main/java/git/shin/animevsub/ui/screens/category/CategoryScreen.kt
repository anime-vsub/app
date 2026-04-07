package git.shin.animevsub.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.data.model.FilterGroup
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.ui.components.grid.GridLoadingSkeleton
import git.shin.animevsub.ui.components.grid.VerticalGridAnimeList
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String) -> Unit,
  viewModel: CategoryViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val gridState = rememberLazyGridState()
  var selectedGroupForSheet by remember { mutableStateOf<FilterGroup?>(null) }

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      Column(modifier = Modifier.background(DarkBackground)) {
        TopAppBar(
          title = {
            Text(
              text = uiState.title,
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
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
          actions = {
            if (uiState.filterGroups.isNotEmpty()) {
              IconButton(onClick = {
                if (uiState.filterGroups.isNotEmpty()) {
                  selectedGroupForSheet = uiState.filterGroups[0]
                }
              }) {
                Icon(
                  imageVector = Icons.Default.FilterList,
                  contentDescription = stringResource(R.string.filter),
                  tint = if (uiState.selectedFilters.isNotEmpty()) AccentMain else TextPrimary
                )
              }
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        if (uiState.filterGroups.isNotEmpty()) {
          FilterActionRow(
            groups = uiState.filterGroups,
            selectedFilters = uiState.selectedFilters,
            onGroupClick = { group -> selectedGroupForSheet = group }
          )
        }
      }
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
              onItemClick = { onNavigateToDetail(it.animeId) },
              state = gridState,
              isLoadingMore = uiState.isLoadingMore,
              onLoadMore = { viewModel.loadMore() }
            )
          }
        }
      }
    }
  }

  selectedGroupForSheet?.let { group ->
    FilterBottomSheet(
      group = group,
      selectedFilters = uiState.selectedFilters,
      onDismiss = { selectedGroupForSheet = null },
      onUpdateFilter = { viewModel.updateFilter(it) }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterActionRow(
  groups: List<FilterGroup>,
  selectedFilters: List<SelectedFilter>,
  onGroupClick: (FilterGroup) -> Unit
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 8.dp),
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(groups) { group ->
      val activeFiltersInGroup = selectedFilters.filter { it.groupId == group.id }
      val isSelected = activeFiltersInGroup.isNotEmpty()

      val labelText = if (activeFiltersInGroup.isEmpty()) {
        group.name
      } else {
        if (activeFiltersInGroup.size == 1) activeFiltersInGroup[0].name
        else "${group.name} (${activeFiltersInGroup.size})"
      }

      FilterChip(
        selected = isSelected,
        onClick = { onGroupClick(group) },
        label = {
          Text(
            text = labelText,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        },
        trailingIcon = {
          Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          containerColor = DarkCard,
          labelColor = TextSecondary,
          selectedContainerColor = AccentMain.copy(alpha = 0.2f),
          selectedLabelColor = AccentMain,
          selectedTrailingIconColor = AccentMain
        ),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = isSelected,
          borderColor = Color.Transparent,
          selectedBorderColor = AccentMain,
          borderWidth = 1.dp,
          selectedBorderWidth = 1.dp
        ),
        shape = RoundedCornerShape(8.dp)
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
  group: FilterGroup,
  selectedFilters: List<SelectedFilter>,
  onDismiss: () -> Unit,
  onUpdateFilter: (SelectedFilter) -> Unit
) {
  val sheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkBackground,
    dragHandle = { BottomSheetDefaults.DragHandle(color = TextGrey) }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.6f)
        .padding(bottom = 32.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Text(
        text = stringResource(R.string.filter_group_format, group.name),
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
      )

      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          group.options.forEach { option ->
            val filterInList =
              selectedFilters.find { it.id == option.id && it.groupId == group.id }
            val isSelected = filterInList?.include == true
            val isExcluded = filterInList?.exclude == true

            FilterChip(
              selected = isSelected || isExcluded,
              onClick = {
                val newFilter = when {
                  !isSelected && !isExcluded -> SelectedFilter(
                    group.id,
                    option.id,
                    option.name,
                    include = true,
                    exclude = false
                  )

                  isSelected -> SelectedFilter(
                    group.id,
                    option.id,
                    option.name,
                    include = false,
                    exclude = true
                  )

                  else -> SelectedFilter(
                    group.id,
                    option.id,
                    option.name,
                    include = false,
                    exclude = false
                  )
                }
                onUpdateFilter(newFilter)
              },
              label = {
                Text(
                  text = (if (isExcluded) "!" else "") + option.name,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                containerColor = DarkCard,
                labelColor = TextSecondary,
                selectedContainerColor = if (isExcluded) Color.Red.copy(alpha = 0.2f) else AccentMain.copy(
                  alpha = 0.2f
                ),
                selectedLabelColor = if (isExcluded) Color.Red else AccentMain
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected || isExcluded,
                borderColor = Color.Transparent,
                selectedBorderColor = if (isExcluded) Color.Red else AccentMain,
                borderWidth = 1.dp,
                selectedBorderWidth = 1.dp
              ),
              shape = RoundedCornerShape(16.dp)
            )
          }
        }
      }
    }
  }
}
