package git.shin.animevsub.ui.components.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import git.shin.animevsub.ui.components.anime.SkeletonCard
import git.shin.animevsub.ui.theme.DarkCard

@Composable
fun HomeLoadingSkeleton() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    // Carousel skeleton (Full width)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9.5f)
        .background(DarkCard)
    )

    // Quick links skeleton
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      repeat(3) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(DarkCard)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .width(60.dp)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(DarkCard)
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
      items(5) {
        SkeletonCard(modifier = Modifier.width(110.dp))
      }
    }

    // Nominated (Grid 3 columns)
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
          repeat(3) {
            SkeletonCard(modifier = Modifier.weight(1f))
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    // Timeline skeleton
    Spacer(modifier = Modifier.height(16.dp))
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(20.dp),
      userScrollEnabled = false
    ) {
      items(5) {
        Box(
          modifier = Modifier
            .width(80.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(DarkCard)
        )
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
      .background(DarkCard)
  )
}
