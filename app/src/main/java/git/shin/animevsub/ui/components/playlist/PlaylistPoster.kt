package git.shin.animevsub.ui.components.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import git.shin.animevsub.ui.theme.DarkCard

@Composable
fun PlaylistPoster(
  modifier: Modifier = Modifier,
  posterUrl: String?
) {
  Box(
    modifier = modifier
      .aspectRatio(16f / 9f),
    contentAlignment = Alignment.BottomCenter
  ) {
    // Background layer (the "stacked" effect behind)
    Box(
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .fillMaxHeight(0.15f)
        .align(Alignment.TopCenter)
        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        .background(Color.White.copy(alpha = 0.2f))
    )

    // Main poster container
    Box(
      modifier = Modifier
        .padding(top = 4.dp)
        .fillMaxSize()
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    ) {
      if (!posterUrl.isNullOrEmpty()) {
        // Blur background to fill the gaps
        AsyncImage(
          model = posterUrl,
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxSize()
            .blur(20.dp)
        )
        // Dark overlay on the blurred background
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
        )
        // Main poster (Fit)
        AsyncImage(
          model = posterUrl,
          contentDescription = null,
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}
