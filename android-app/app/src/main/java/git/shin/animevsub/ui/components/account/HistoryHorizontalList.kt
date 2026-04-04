package git.shin.animevsub.ui.components.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.ui.components.common.ErrorRetrySection
import git.shin.animevsub.ui.components.common.SectionHeader
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey

@Composable
fun HistoryHorizontalList(
  histories: List<HistoryItem>,
  isLoading: Boolean,
  error: String?,
  onHeaderClick: () -> Unit,
  onRetry: () -> Unit,
  onItemClick: (HistoryItem) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    SectionHeader(
      title = stringResource(R.string.history),
      onViewAll = onHeaderClick
    )

    if (isLoading) {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(5) {
          HistoryItemSkeleton()
        }
      }
    } else if (error != null) {
      ErrorRetrySection(onRetry = onRetry)
    } else if (histories.isEmpty()) {
      Text(
        text = stringResource(R.string.no_history),
        color = TextGrey,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    } else {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(histories) { item ->
          HistoryCard(item = item, onClick = { onItemClick(item) })
        }
      }
    }
  }
}

@Composable
fun HistoryItemSkeleton() {
  Column(modifier = Modifier.width(200.dp)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .height(14.dp)
        .background(DarkCard, RoundedCornerShape(2.dp))
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.5f)
        .height(12.dp)
        .background(DarkCard, RoundedCornerShape(2.dp))
    )
  }
}
