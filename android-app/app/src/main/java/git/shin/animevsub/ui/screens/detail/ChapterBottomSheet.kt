package git.shin.animevsub.ui.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterBottomSheet(
  uiState: DetailUiState,
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  onSeasonClick: (String) -> Unit,
  onChapterClick: (ChapterInfo, String) -> Unit
) {
  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val videoHeight = screenWidth * 9 / 16
  val sheetHeight = configuration.screenHeightDp.dp - videoHeight

  var searchQuery by remember { mutableStateOf("") }
  var showVerticalSeasons by remember { mutableStateOf(false) }

  val seasonListState = rememberLazyListState()

  // Scroll to current season in the horizontal list when not in vertical mode
  val currentSeasonIndex = uiState.displaySeasons.indexOfFirst {
    it.id == uiState.activeDisplaySeasonId
  }
  LaunchedEffect(currentSeasonIndex, showVerticalSeasons) {
    if (currentSeasonIndex >= 0 && !showVerticalSeasons) {
      seasonListState.animateScrollToItem(currentSeasonIndex)
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = DarkSurface,
    dragHandle = { BottomSheetDefaults.DragHandle(color = TextGrey) },
    modifier = Modifier.height(sheetHeight).fillMaxWidth()
  ) {
    Column(modifier = Modifier.fillMaxWidth() .fillMaxHeight()) {
      // Header with Title and View Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = stringResource(if (showVerticalSeasons) R.string.season_list else R.string.select_episode),
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )

        IconButton(onClick = {
          showVerticalSeasons = !showVerticalSeasons
          if (showVerticalSeasons) searchQuery = "" // Clear search when switching to season list
        }) {
          Icon(
            imageVector = if (showVerticalSeasons) Icons.Default.GridView else Icons.AutoMirrored.Filled.List,
            contentDescription = stringResource(R.string.toggle_view_mode),
            tint = if (showVerticalSeasons) Color(0xFF00D639) else TextPrimary
          )
        }
      }

      // Search Bar (Only visible when showing chapters)
      AnimatedVisibility(
        visible = !showVerticalSeasons,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 8.dp),
          placeholder = { Text(stringResource(R.string.search_episode_hint), color = TextGrey, fontSize = 14.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGrey) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = null, tint = TextGrey)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00D639),
            unfocusedBorderColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedContainerColor = DarkCard,
            cursorColor = Color(0xFF00D639)
          )
        )
      }

      if (showVerticalSeasons) {
        // View 1: Vertical Seasons List (Hide Chapters)
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(uiState.displaySeasons) { season ->
            val isCurrent = season.id == uiState.activeDisplaySeasonId
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrent) Color(0xFF00D639).copy(alpha = 0.15f) else DarkCard)
                .border(
                  width = if (isCurrent) 1.5.dp else 1.dp,
                  color = if (isCurrent) Color(0xFF00D639) else Color.Transparent,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                  onSeasonClick(season.id)
                  showVerticalSeasons = false // Switch back to chapter view on selection
                },
              contentAlignment = Alignment.CenterStart
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = season.name,
                  color = if (isCurrent) Color(0xFF00D639) else TextPrimary,
                  fontSize = 15.sp,
                  fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f)
                )
                if (isCurrent) {
                  Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = null,
                    tint = Color(0xFF00D639),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      } else {
        // View 2: Chapters View (Horizontal Seasons + Chapter Grid)

        // Horizontal Season Switcher (Only if not searching)
        if (searchQuery.isEmpty()) {
          LazyRow(
            state = seasonListState,
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
          ) {
            items(uiState.displaySeasons) { season ->
              val isCurrent = season.id == uiState.activeDisplaySeasonId

              Box(
                modifier = Modifier
                  .widthIn(min = 100.dp)
                  .height(36.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (isCurrent) Color(0xFF00D639).copy(alpha = 0.15f) else DarkCard)
                  .border(
                    width = if (isCurrent) 1.5.dp else 1.dp,
                    color = if (isCurrent) Color(0xFF00D639) else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                  )
                  .clickable {
                    onSeasonClick(season.id)
                  }, contentAlignment = Alignment.Center
              ) {
                Text(
                  text = season.name,
                  modifier = Modifier.padding(horizontal = 12.dp),
                  color = if (isCurrent) Color(0xFF00D639) else TextSecondary,
                  fontSize = 12.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Chapters Content
        val chapterData = uiState.chapterData
        if (chapterData != null && chapterData.chaps.isNotEmpty()) {
          val activeDisplaySeason =
            uiState.displaySeasons.find { it.id == uiState.activeDisplaySeasonId }

          // Logic: If searching, search across ALL chapters of the real season.
          // If not searching, only show the current 30-chapter chunk.
          val filteredChaps = if (searchQuery.isEmpty()) {
            if (activeDisplaySeason?.range != null) {
              chapterData.chaps.slice(activeDisplaySeason.range.filter { it < chapterData.chaps.size })
            } else {
              chapterData.chaps
            }
          } else {
            chapterData.chaps.filter { it.name.contains(searchQuery, ignoreCase = true) }
          }

          if (filteredChaps.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              contentAlignment = Alignment.Center
            ) {
              Text(stringResource(R.string.no_episodes_found), color = TextGrey, fontSize = 14.sp)
            }
          } else {
            LazyVerticalGrid(
              columns = GridCells.Adaptive(minSize = 65.dp),
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              contentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 16.dp),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(filteredChaps.size) { index ->
                val chap = filteredChaps[index]
                val isSelected = chap.id == uiState.currentChapter?.id

                Box(
                  modifier = Modifier
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) Color(0xFF00D639).copy(alpha = 0.15f) else DarkCard)
                    .border(
                      width = if (isSelected) 1.5.dp else 1.dp,
                      color = if (isSelected) Color(0xFF00D639) else Color.Transparent,
                      shape = RoundedCornerShape(6.dp)
                    )
                    .clickable {
                      // Pass the real season ID to play
                      onChapterClick(chap, activeDisplaySeason?.realId ?: uiState.currentSeasonId)
                      onDismissRequest()
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = chap.name,
                    color = if (isSelected) Color(0xFF00D639) else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp)
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
