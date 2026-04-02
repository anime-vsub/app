package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.utils.shimmerEffect

@Composable
fun RankingSkeleton(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Rank number placeholder
    Box(
      modifier = Modifier
        .width(40.dp)
        .height(20.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )

    // Thumbnail placeholder
    Box(
      modifier = Modifier
        .size(55.dp, 75.dp)
        .clip(RoundedCornerShape(6.dp))
        .shimmerEffect()
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      // Title placeholder
      Box(
        modifier = Modifier
          .fillMaxWidth(0.8f)
          .height(16.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Subtitle placeholder
      Box(
        modifier = Modifier
          .fillMaxWidth(0.4f)
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Stats placeholder
      Row {
        Box(
          modifier = Modifier
            .width(40.dp)
            .height(14.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
          modifier = Modifier
            .width(60.dp)
            .height(14.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
        )
      }
    }
  }
}
