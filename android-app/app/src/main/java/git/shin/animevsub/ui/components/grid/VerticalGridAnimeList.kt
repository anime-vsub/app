package git.shin.animevsub.ui.components.grid

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.ui.components.anime.AnimeCardItem
import git.shin.animevsub.ui.theme.AccentMain

@Composable
fun VerticalGridAnimeList(
    items: List<AnimeCard>,
    onItemClick: (AnimeCard) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    LaunchedEffect(state.canScrollForward) {
        if (!state.canScrollForward && !isLoadingMore && items.isNotEmpty()) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            AnimeCardItem(
                anime = item,
                onClick = { onItemClick(item) },
                showRating = true
            )
        }

        if (isLoadingMore) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentMain,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        item(span = { GridItemSpan(3) }) {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
