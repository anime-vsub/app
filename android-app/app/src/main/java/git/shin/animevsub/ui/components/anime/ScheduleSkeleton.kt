package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import git.shin.animevsub.ui.utils.shimmerEffect

@Composable
fun ScheduleItemSkeleton(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(85.dp, 120.dp)
        .clip(RoundedCornerShape(8.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column(
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.8f)
          .height(20.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.4f)
          .height(16.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
      Spacer(modifier = Modifier.height(12.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
    }
  }
}

@Composable
fun ScheduleLoadingSkeleton(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxSize()) {
    // Tabs placeholder
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      repeat(5) {
        Box(
          modifier = Modifier
            .size(48.dp, 36.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
        )
      }
    }

    // List placeholder
    Column {
      repeat(3) {
        Box(
          modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            .size(60.dp, 20.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
        )
        repeat(2) {
          ScheduleItemSkeleton()
        }
      }
    }
  }
}
