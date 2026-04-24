package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import git.shin.animevsub.ui.utils.shimmerEffect

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(2f / 3f)
        .clip(RoundedCornerShape(8.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(8.dp))
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
        .fillMaxWidth(0.6f)
        .height(14.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
  }
}
