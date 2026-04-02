package git.shin.animevsub.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.ui.components.anime.AnimeCardItem

/**
 * A simple grid-like list for use inside a vertical scrollable column (like HomeScreen).
 * This is NOT a LazyVerticalGrid.
 */
@Composable
fun GridAnimeList(
  items: List<AnimeCard>,
  onItemClick: (AnimeCard) -> Unit,
  modifier: Modifier = Modifier,
  columns: Int = 3
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    val chunkedItems = items.chunked(columns)
    chunkedItems.forEachIndexed { rowIndex, rowItems ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        rowItems.forEach { item ->
          AnimeCardItem(
            anime = item,
            onClick = { onItemClick(item) },
            modifier = Modifier.weight(1f)
          )
        }
        // Fill remaining space if row is not full
        if (rowItems.size < columns) {
          repeat(columns - rowItems.size) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
      if (rowIndex < chunkedItems.size - 1) {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
