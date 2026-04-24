package git.shin.animevsub.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import git.shin.animevsub.ui.components.anime.SkeletonCard
import git.shin.animevsub.ui.utils.shimmerEffect
import git.shin.animevsub.utils.ResponsiveUtils

@Composable
fun HomeLoadingSkeleton(windowSize: WindowSizeClass) {
  val columns = ResponsiveUtils.calculateGridColumns(windowSize)

  val aspectRatio = when {
    windowSize.heightSizeClass == WindowHeightSizeClass.Compact -> {
      if (windowSize.widthSizeClass == WindowWidthSizeClass.Expanded) 16f / 4f else 16f / 5f
    }

    windowSize.widthSizeClass == WindowWidthSizeClass.Expanded -> 16f / 6f
    windowSize.widthSizeClass == WindowWidthSizeClass.Medium -> 16f / 8f
    else -> 16f / 9.5f
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    // Carousel skeleton (Full width)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(aspectRatio)
        .shimmerEffect()
    )

    // Quick links skeleton
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 20.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      repeat(3) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(45.dp)
              .clip(CircleShape)
              .shimmerEffect()
          )
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .width(50.dp)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .shimmerEffect()
          )
        }
      }
    }

    // This Season (Horizontal Scroll)
    SectionSkeleton()
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      userScrollEnabled = false
    ) {
      items(10) {
        SkeletonCard(modifier = Modifier.width(110.dp))
      }
    }

    // Nominated (Grid responsive columns)
    Spacer(modifier = Modifier.height(24.dp))
    SectionSkeleton()
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
      repeat(2) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          repeat(columns) {
            SkeletonCard(modifier = Modifier.weight(1f))
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    // Top / Hot Update (Horizontal)
    Spacer(modifier = Modifier.height(8.dp))
    SectionSkeleton()
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      userScrollEnabled = false
    ) {
      items(10) {
        SkeletonCard(modifier = Modifier.width(110.dp))
      }
    }

    Spacer(modifier = Modifier.height(100.dp))
  }
}

@Composable
private fun SectionSkeleton() {
  Box(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .width(120.dp)
      .height(20.dp)
      .clip(RoundedCornerShape(4.dp))
      .shimmerEffect()
  )
}
