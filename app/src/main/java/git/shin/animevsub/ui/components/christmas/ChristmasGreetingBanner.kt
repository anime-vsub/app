package git.shin.animevsub.ui.components.christmas

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.ChristmasBlush
import git.shin.animevsub.ui.theme.ChristmasGold
import git.shin.animevsub.ui.theme.ChristmasGreen
import git.shin.animevsub.ui.theme.ChristmasInk
import git.shin.animevsub.ui.theme.ChristmasInkSoft
import git.shin.animevsub.ui.theme.ChristmasNavy
import git.shin.animevsub.ui.theme.ChristmasRose
import git.shin.animevsub.ui.theme.christmasCardBrush
import java.util.Calendar

/** Days left until 25 December of the current (or next) year. */
fun daysUntilChristmas(date: Calendar = Calendar.getInstance()): Int {
  val today = date.clone() as Calendar
  today.set(Calendar.HOUR_OF_DAY, 0)
  today.set(Calendar.MINUTE, 0)
  today.set(Calendar.SECOND, 0)
  today.set(Calendar.MILLISECOND, 0)

  val xmas = today.clone() as Calendar
  xmas.set(Calendar.MONTH, Calendar.DECEMBER)
  xmas.set(Calendar.DAY_OF_MONTH, 25)
  if (xmas.before(today)) {
    xmas.add(Calendar.YEAR, 1)
  }

  val millis = xmas.timeInMillis - today.timeInMillis
  return (millis / (24 * 60 * 60 * 1000L)).toInt()
}

/**
 * Festive greeting card for the home screen.
 *
 * Follows the "Christmas App (3D)" reference design: a light blush → lavender
 * card, a pale pink disc holding the full-colour 3D scene, near-black heading,
 * muted body copy and a rose pill button. The light surface is what lets the
 * pastel 3D artwork keep its volume instead of turning into a dark silhouette.
 */
@Composable
fun ChristmasGreetingBanner(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String,
  badge: String? = null,
  action: String? = null,
  onClick: (() -> Unit)? = null
) {
  val transition = rememberInfiniteTransition(label = "xmas-banner")
  val bob by transition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(3200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "xmas-banner-bob"
  )
  val twinkle by transition.animateFloat(
    initialValue = 0.45f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1700, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "xmas-banner-twinkle"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(172.dp)
      .padding(horizontal = 16.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(christmasCardBrush())
  ) {
    // Blush disc + folded ribbon wedge, the backdrop the reference puts behind
    // its 3D scene so the artwork never floats on a bare background.
    Canvas(modifier = Modifier.fillMaxSize()) {
      // Centre the disc on the artwork column (the row keeps ~156dp on the
      // right), not on a fraction guessed from the card width.
      val radius = size.height * 0.50f
      val center = Offset(size.width - 88.dp.toPx(), size.height * 0.52f)
      drawCircle(color = ChristmasBlush, radius = radius, center = center)
      drawCircle(color = ChristmasBlush.copy(alpha = 0.55f), radius = radius * 0.84f, center = center)
      drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = radius,
        center = center,
        style = Stroke(width = 2f)
      )
      // Confetti kept tiny so it reads as dust, not as noise over the copy.
      listOf(
        0.60f to 0.14f,
        0.94f to 0.30f,
        0.66f to 0.86f,
        0.30f to 0.12f
      ).forEachIndexed { index, (fx, fy) ->
        drawCircle(
          color = if (index % 2 == 0) {
            ChristmasGold.copy(alpha = 0.75f * twinkle)
          } else {
            ChristmasRose.copy(alpha = 0.45f * twinkle)
          },
          radius = 2.4f + index,
          center = Offset(size.width * fx, size.height * fy)
        )
      }
    }

    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(start = 18.dp, end = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        if (badge != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(50))
              .background(ChristmasNavy)
              .padding(horizontal = 9.dp, vertical = 3.dp)
          ) {
            Text(
              text = badge,
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 0.4.sp
            )
          }
          Spacer(modifier = Modifier.height(7.dp))
        }

        Text(
          text = title,
          color = ChristmasInk,
          fontSize = 17.sp,
          lineHeight = 21.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
          text = subtitle,
          color = ChristmasInkSoft,
          fontSize = 11.5.sp,
          lineHeight = 16.sp,
          maxLines = 3
        )

        if (action != null && onClick != null) {
          Spacer(modifier = Modifier.height(10.dp))
          ChristmasPillButton(
            text = action,
            onClick = onClick,
            compact = true
          )
        }
      }

      // The 3D scene, resting on a thin snow shelf inside the disc.
      Box(
        modifier = Modifier
          .width(156.dp)
          .height(160.dp)
          .padding(start = 4.dp, end = 8.dp, bottom = 6.dp),
        contentAlignment = Alignment.BottomCenter
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawOval(
            brush = Brush.radialGradient(
              colors = listOf(Color.White.copy(alpha = 0.95f), Color.Transparent),
              center = Offset(size.width * 0.5f, size.height * 0.86f),
              radius = size.width * 0.42f
            ),
            topLeft = Offset(size.width * 0.05f, size.height * 0.72f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.95f, size.height * 0.24f)
          )
        }

        Image(
          painter = painterResource(R.drawable.xm_christmas_present_stack),
          contentDescription = null,
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 8.dp, bottom = 12.dp)
            .size(62.dp)
        )

        Image(
          painter = painterResource(R.drawable.xm_christmas_tree_decorated),
          contentDescription = null,
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 10.dp)
            .size(118.dp)
            .graphicsLayer {
              rotationZ = bob * 0.4f
              transformOrigin = TransformOrigin(0.5f, 1f)
            }
        )

        Image(
          painter = painterResource(R.drawable.xm_snowman_classic),
          contentDescription = null,
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 12.dp, bottom = 14.dp)
            .size(50.dp)
            .graphicsLayer { translationY = -bob * 0.6f }
        )

        // Small gold star sticker above the tree, twinkling with the confetti.
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 6.dp, end = 26.dp)
            .size(20.dp)
            .graphicsLayer {
              alpha = 0.55f + 0.45f * twinkle
              rotationZ = bob * 2.2f
            }
            .background(ChristmasGold, CircleShape)
        )
        Box(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(top = 26.dp, start = 14.dp)
            .size(9.dp)
            .background(ChristmasGreen.copy(alpha = 0.75f), CircleShape)
        )
      }
    }
  }
}
