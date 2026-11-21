package git.shin.animevsub.ui.components.christmas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.ChristmasGold
import git.shin.animevsub.ui.theme.ChristmasRose
import git.shin.animevsub.ui.theme.ChristmasRoseDeep

/** Soft rose shadow that sits under a festive pill, faking depth on dark UI. */
@Composable
private fun BoxScope.PillShadow(radius: Int) {
  Box(
    modifier = Modifier
      .matchParentSize()
      .offset(y = 3.dp)
      .clip(RoundedCornerShape(radius))
      .background(ChristmasRoseDeep.copy(alpha = 0.32f))
  )
}

/**
 * Primary festive button: rose → deep-rose pill with a gold hairline, a top
 * gloss and a press scale, so actions read as glossy toy buttons rather than
 * flat rectangles.
 */
@Composable
fun ChristmasPillButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  compact: Boolean = false
) {
  val interaction = remember { MutableInteractionSource() }
  val pressed by interaction.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (pressed) 0.96f else 1f,
    label = "xmas-pill-scale"
  )
  val shape = RoundedCornerShape(50)

  Box(
    modifier = modifier
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
  ) {
    PillShadow(radius = 50)
    Box(
      modifier = Modifier
        .clip(shape)
        .background(Brush.linearGradient(listOf(ChristmasRose, ChristmasRoseDeep)))
        .border(1.dp, ChristmasGold.copy(alpha = 0.55f), shape)
        .clickable(
          interactionSource = interaction,
          indication = null,
          onClick = onClick
        )
        .padding(
          horizontal = if (compact) 15.dp else 22.dp,
          vertical = if (compact) 7.dp else 12.dp
        ),
      contentAlignment = Alignment.Center
    ) {
      // Top gloss: the reference's buttons all catch a light from above.
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(
            Brush.verticalGradient(
              listOf(Color.White.copy(alpha = 0.24f), Color.Transparent)
            )
          )
      )
      Text(
        text = text,
        color = Color.White,
        fontSize = if (compact) 11.5.sp else 14.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

/**
 * Circular shortcut button on a pale card disc: the pastel 3D reference's
 * icon tile, tinted per action. Falls back to a bare icon when the theme is off.
 */
@Composable
fun ChristmasIconDisc(
  painter: Painter,
  contentDescription: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 48.dp,
  tint: Color? = null
) {
  // A real wreath rings the shortcut: the disc nests in the artwork's opening,
  // so needles and berries frame it all the way round.
  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painter = painterResource(R.drawable.xm_christmas_wreath_classic),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier.size(size)
    )
    Box(
      modifier = Modifier
        .size(size * 0.68f)
        .offset(y = 1.dp)
        .clip(CircleShape)
        .background(ChristmasRoseDeep.copy(alpha = 0.30f))
    )
    Box(
      modifier = Modifier
        .size(size * 0.7f)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(Color(0xFFFFF8FC), Color(0xFFF7D9EE))
          )
        )
        .border(1.dp, ChristmasGold.copy(alpha = 0.65f), CircleShape)
        .clickable(onClick = onClick),
      contentAlignment = Alignment.Center
    ) {
      // Blush bloom at the bottom keeps the disc from reading as flat grey.
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(
            Brush.verticalGradient(
              listOf(Color.White.copy(alpha = 0.55f), Color(0x00FFFFFF))
            )
          )
      )
      Image(
        painter = painter,
        contentDescription = contentDescription,
        colorFilter = tint?.let { androidx.compose.ui.graphics.ColorFilter.tint(it) },
        modifier = Modifier.size(size * 0.34f)
      )
    }
  }
}

/** Wide variant for form actions such as login or retry. */
@Composable
fun ChristmasWideButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  ChristmasPillButton(
    text = text,
    onClick = onClick,
    modifier = modifier.fillMaxWidth()
  )
}
