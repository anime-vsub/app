package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import git.shin.animevsub.R
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
import git.shin.animevsub.ui.components.badge.QualityBadge
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.StarColor
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.tvFocusScale

@Composable
fun AnimeCardItem(
  anime: AnimeCard,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  showQuality: Boolean = true,
  trendingIndex: Int? = null,
  showRating: Boolean = false
) {
  val context = LocalContext.current
  val imageRequest = remember(anime.image) {
    ImageRequest.Builder(context)
      .data(anime.image)
      .apply {
        AnimeApi.getHeaders(anime.image ?: "").forEach { (key, value) ->
          addHeader(key, value)
        }
      }
      .crossfade(true)
      .build()
  }

  Column(
    modifier = modifier
      .tvFocusScale()
      .clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(2f / 3f)
        .clip(RoundedCornerShape(8.dp))
    ) {
      AsyncImage(
        model = imageRequest,
        contentDescription = anime.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      // Gradient overlay at bottom
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(60.dp)
          .align(Alignment.BottomCenter)
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
            )
          )
      )

      @Suppress("DEPRECATION")
      val badgeTextStyle = TextStyle(
        platformStyle = PlatformTextStyle(
          includeFontPadding = false
        )
      )

      // Chapter badge
      if (anime.lastEpisode != null) {
        Text(
          text = stringResource(id = R.string.ep_label, anime.lastEpisode.name),
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          style = badgeTextStyle,
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(6.dp)
            .background(
              AccentMain.copy(alpha = 0.85f),
              RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      // Quality badge
      if (showQuality && !anime.quality.isNullOrEmpty()) {
        QualityBadge(
          quality = anime.quality,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
        )
      }

      // Trending index with custom images
      if (trendingIndex != null) {
        val rankResId = when (trendingIndex) {
          1 -> R.drawable.bangumi_rank_ic_1
          2 -> R.drawable.bangumi_rank_ic_2
          3 -> R.drawable.bangumi_rank_ic_3
          4 -> R.drawable.bangumi_rank_ic_4
          5 -> R.drawable.bangumi_rank_ic_5
          6 -> R.drawable.bangumi_rank_ic_6
          7 -> R.drawable.bangumi_rank_ic_7
          8 -> R.drawable.bangumi_rank_ic_8
          9 -> R.drawable.bangumi_rank_ic_9
          10 -> R.drawable.bangumi_rank_ic_10
          else -> null
        }

        if (rankResId != null) {
          Image(
            painter = painterResource(id = rankResId),
            contentDescription = "Rank $trendingIndex",
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .fillMaxWidth(0.4f)
              .padding(0.dp)
              .align(Alignment.TopStart)
          )
        }
      }

      // Rating
      if (showRating && anime.rate > 0) {
        Row(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(6.dp)
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = StarColor,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = String.format("%.1f", anime.rate),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            style = badgeTextStyle
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = anime.name,
      color = TextPrimary,
      fontSize = 13.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      lineHeight = 16.sp
    )
  }
}
