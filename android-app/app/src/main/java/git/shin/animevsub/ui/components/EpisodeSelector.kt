package git.shin.animevsub.ui.components

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import git.shin.animevsub.data.model.DisplaySeason
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.theme.DarkSurface

@Composable
fun EpisodeSelectorContent(
    displaySeasons: List<DisplaySeason>,
    activeDisplaySeasonId: String,
    episodes: List<ChapterInfo>,
    currentEpisodeId: String?,
    onSeasonClick: (String) -> Unit,
    onChapterClick: (ChapterInfo, String) -> Unit,
    isSideMenu: Boolean = false,
    onClose: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var showVerticalSeasons by remember { mutableStateOf(false) }
    val seasonListState = rememberLazyListState()

    // Scroll to current season in the horizontal list when not in vertical mode
    val currentSeasonIndex = displaySeasons.indexOfFirst { it.id == activeDisplaySeasonId }
    LaunchedEffect(currentSeasonIndex, showVerticalSeasons) {
        if (currentSeasonIndex >= 0 && !showVerticalSeasons) {
            seasonListState.animateScrollToItem(currentSeasonIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header with Title and View Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(if (showVerticalSeasons) R.string.season_list else R.string.select_episode),
                color = TextPrimary,
                fontSize = if (isSideMenu) 16.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                showVerticalSeasons = !showVerticalSeasons
                if (showVerticalSeasons) searchQuery = "" // Clear search when switching to season list
            }) {
                Icon(
                    imageVector = if (showVerticalSeasons) Icons.Default.GridView else Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    tint = if (showVerticalSeasons) MainColor else TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (onClose != null) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
                    .padding(horizontal = 16.dp, vertical = 4.dp),
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
                    focusedBorderColor = MainColor,
                    unfocusedBorderColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedContainerColor = DarkCard,
                    cursorColor = MainColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        if (showVerticalSeasons) {
            // View 1: Vertical Seasons List (Hide Chapters)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displaySeasons) { season ->
                    val isCurrent = season.id == activeDisplaySeasonId
                    SeasonItem(
                        season = season,
                        isSelected = isCurrent,
                        onClick = {
                            onSeasonClick(season.id)
                            showVerticalSeasons = false // Switch back to chapter view on selection
                        },
                        isFullWidth = true
                    )
                }
            }
        } else {
            // View 2: Chapters View (Horizontal Seasons + Chapter Grid)

            // Horizontal Season Switcher (Only if not searching)
            if (searchQuery.isEmpty() && displaySeasons.isNotEmpty()) {
                LazyRow(
                    state = seasonListState,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(displaySeasons) { season ->
                        SeasonItem(
                            season = season,
                            isSelected = season.id == activeDisplaySeasonId,
                            onClick = { onSeasonClick(season.id) }
                        )
                    }
                }
            }

            // Chapters Content
            val activeSeason = displaySeasons.find { it.id == activeDisplaySeasonId }

            // Logic: If searching, search across ALL chapters of the real season.
            // If not searching, only show the current 30-chapter chunk.
            val filteredChaps = if (searchQuery.isEmpty()) {
                if (activeSeason?.range != null) {
                    episodes.slice(activeSeason.range.filter { it < episodes.size })
                } else {
                    episodes
                }
            } else {
                episodes.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

            if (filteredChaps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_episodes_found), color = TextGrey, fontSize = 14.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 65.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredChaps) { chap ->
                        EpisodeItem(
                            chap = chap,
                            isSelected = chap.id == currentEpisodeId,
                            onClick = {
                                // Pass the real season ID to play
                                onChapterClick(chap, activeSeason?.realId ?: activeDisplaySeasonId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServerSelectorContent(
    servers: List<ServerInfo>,
    currentServer: ServerInfo?,
    onServerClick: (ServerInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(servers) { server ->
            val isSelected = server == currentServer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MainColor.copy(alpha = 0.15f) else DarkCard)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) MainColor else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onServerClick(server) },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = server.name,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = if (isSelected) MainColor else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun SeasonItem(
    season: DisplaySeason,
    isSelected: Boolean,
    onClick: () -> Unit,
    isFullWidth: Boolean = false
) {
    Box(
        modifier = Modifier
            .then(if (isFullWidth) Modifier.fillMaxWidth().height(52.dp) else Modifier.widthIn(min = 90.dp).height(34.dp))
            .clip(RoundedCornerShape(if (isFullWidth) 8.dp else 4.dp))
            .background(if (isSelected) MainColor.copy(alpha = 0.15f) else DarkCard)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MainColor else Color.Transparent,
                shape = RoundedCornerShape(if (isFullWidth) 8.dp else 4.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = if (isFullWidth) Alignment.CenterStart else Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = season.name,
                color = if (isSelected) MainColor else if (isFullWidth) TextPrimary else TextSecondary,
                fontSize = if (isFullWidth) 15.sp else 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (isFullWidth) Modifier.weight(1f) else Modifier
            )
            if (isFullWidth && isSelected) {
                Icon(Icons.Default.GridView, null, tint = MainColor, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun EpisodeItem(
    chap: ChapterInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) MainColor.copy(alpha = 0.15f) else DarkCard)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MainColor else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = chap.name,
            color = if (isSelected) MainColor else TextPrimary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
