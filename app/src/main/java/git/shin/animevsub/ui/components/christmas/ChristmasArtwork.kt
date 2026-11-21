package git.shin.animevsub.ui.components.christmas

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import git.shin.animevsub.R

/**
 * Decorative festive artwork drawn behind a screen header.
 *
 * The image is cropped to fill, then a vertical gradient fades it into the
 * screen background so text stays readable on top.
 */
@Composable
fun ChristmasHeaderArtwork(
  modifier: Modifier = Modifier,
  @DrawableRes imageRes: Int = R.drawable.xm_christmas_tree_decorated,
  height: Dp = 148.dp,
  fadeTo: Color = Color(0xFF0D1A16),
  cornerRadius: Dp = 0.dp
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .clip(RoundedCornerShape(cornerRadius))
  ) {
    Image(
      painter = painterResource(imageRes),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            0.0f to Color.Transparent,
            0.45f to fadeTo.copy(alpha = 0.35f),
            1.0f to fadeTo
          )
        )
    )
  }
}

/** Small inline festive sticker, used as an accent next to headings. */
@Composable
fun ChristmasSticker(
  @DrawableRes imageRes: Int,
  modifier: Modifier = Modifier,
  size: Dp = 22.dp
) {
  Image(
    painter = painterResource(imageRes),
    contentDescription = null,
    modifier = modifier.size(size),
    alignment = Alignment.Center
  )
}
