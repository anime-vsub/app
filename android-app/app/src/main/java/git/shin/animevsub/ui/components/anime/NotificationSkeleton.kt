package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import git.shin.animevsub.ui.utils.shimmerEffect

@Composable
fun NotificationSkeleton(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.Top
  ) {
    // Image placeholder
    Box(
      modifier = Modifier
        .width(120.dp)
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(8.dp))
        .shimmerEffect()
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      // Title placeholder
      Box(
        modifier = Modifier
          .fillMaxWidth(0.6f)
          .height(18.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
      Spacer(modifier = Modifier.height(8.dp))

      // Description lines
      Box(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.7f)
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Time ago placeholder
      Box(
        modifier = Modifier
          .width(60.dp)
          .height(12.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
    }
  }
}

@Composable
fun NotificationListSkeleton() {
  LazyColumn(userScrollEnabled = false) {
    items(10) {
      NotificationSkeleton()
    }
  }
}
