package git.shin.animevsub.ui.components.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.ui.components.anime.AnimeCardItem
import git.shin.animevsub.ui.components.common.ErrorRetrySection
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary

@Composable
fun FollowHorizontalList(
  follows: List<AnimeCard>,
  isLoading: Boolean,
  error: String?,
  onHeaderClick: () -> Unit,
  onRetry: () -> Unit,
  onItemClick: (AnimeCard) -> Unit,
  onFilterClick: (() -> Unit)? = null
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onHeaderClick)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = stringResource(R.string.follow),
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (onFilterClick != null) {
          IconButton(
            onClick = onFilterClick,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = stringResource(R.string.filter),
              tint = AccentMain,
              modifier = Modifier.size(20.dp)
            )
          }
        }
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = TextGrey,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    if (isLoading) {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(5) {
          FollowItemSkeleton()
        }
      }
    } else if (error != null) {
      ErrorRetrySection(onRetry = onRetry)
    } else if (follows.isEmpty()) {
      Text(
        text = stringResource(R.string.no_follows),
        color = TextGrey,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    } else {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(follows) { anime ->
          AnimeCardItem(
            anime = anime,
            onClick = { onItemClick(anime) },
            modifier = Modifier.width(120.dp)
          )
        }
      }
    }
  }
}

@Composable
fun FollowItemSkeleton() {
  Column(modifier = Modifier.width(120.dp)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(2f / 3f)
        .clip(RoundedCornerShape(8.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(14.dp)
        .background(DarkCard, RoundedCornerShape(2.dp))
    )
  }
}
