package git.shin.animevsub.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.ui.components.badge.QualityBadge
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.StarColor
import java.util.Locale
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselSection(
  items: List<AnimeCard>,
  onItemClick: (AnimeCard) -> Unit,
  onNavigateToCategory: (List<SelectedFilter>) -> Unit
) {
  val pagerState = rememberPagerState(pageCount = { items.size })

  // Auto-scroll
  LaunchedEffect(pagerState) {
    while (true) {
      delay(5000)
      if (items.isNotEmpty()) {
        val nextPage = (pagerState.currentPage + 1) % items.size
        pagerState.animateScrollToPage(nextPage)
      }
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    HorizontalPager(
      state = pagerState,
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 0.dp),
      pageSpacing = 0.dp
    ) { page ->
      val item = items[page]
      val pageOffset = (
        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        ).absoluteValue

      val scale = 1f - (pageOffset * 0.02f).coerceIn(0f, 0.02f)
      val alpha = 1f - (pageOffset * 0.05f).coerceIn(0f, 0.05f)

      Box(
        modifier = Modifier
          .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
          }
          .aspectRatio(16f / 9.5f)
          .clickable { onItemClick(item) }
      ) {
        AsyncImage(
          model = item.image,
          contentDescription = item.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Improved Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.2f),
                  Color.Black.copy(alpha = 0.95f)
                )
              )
            )
        )

        // Info
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
          Spacer(modifier = Modifier.weight(1f))

          if (!item.quality.isNullOrEmpty()) {
            QualityBadge(quality = item.quality, isCarousel = true)
            Spacer(modifier = Modifier.height(6.dp))
          }

          Text(
            text = item.name,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(4.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            if (item.rate > 0) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = StarColor,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = String.format(Locale.US, "%.1f", item.rate),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = " | ",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
              )
            }

            val metaInfo = mutableListOf<String>()
            item.year?.let { metaInfo.add(it.toString()) }
            item.process?.let { metaInfo.add(it) }

            Text(
              text = metaInfo.joinToString(" | "),
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 12.sp
            )
          }

          if (item.genre.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              item.genre.forEachIndexed { index, genre ->
                Text(
                  text = genre.name + (if (index < item.genre.size - 1) "," else ""),
                  color = if (genre.filters.isNotEmpty()) MainColor else Color.White.copy(alpha = 0.6f),
                  fontSize = 12.sp,
                  modifier = Modifier.clickable(enabled = genre.filters.isNotEmpty()) {
                    onNavigateToCategory(genre.filters)
                  }
                )
              }
            }
          }

          val description = item.description?.trim()
          if (!description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = description,
              color = Color.White.copy(alpha = 0.7f),
              fontSize = 12.sp,
              maxLines = 2,
              lineHeight = 16.sp,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    // Indicators
    Row(
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(vertical = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      repeat(items.size) { index ->
        val isSelected = pagerState.currentPage == index
        Box(
          modifier = Modifier
            .height(6.dp)
            .width(if (isSelected) 20.dp else 6.dp)
            .clip(CircleShape)
            .background(
              if (isSelected) {
                MainColor
              } else {
                Color.White.copy(alpha = 0.3f)
              }
            )
        )
      }
    }
  }
}
