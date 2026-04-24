package git.shin.animevsub.ui.components.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.ui.components.anime.RankingItem

@Composable
fun VerticalGridRankingList(
  items: List<AnimeCard>,
  columns: Int,
  onItemClick: (AnimeCard) -> Unit,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(16.dp)
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(columns),
    modifier = modifier.fillMaxSize(),
    contentPadding = contentPadding,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    itemsIndexed(items) { index, item ->
      RankingItem(
        rank = index + 1,
        item = item,
        onClick = { onItemClick(item) }
      )
    }
  }
}
