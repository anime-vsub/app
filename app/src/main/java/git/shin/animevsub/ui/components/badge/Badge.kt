package git.shin.animevsub.ui.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.ChristmasGold
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.isChristmasThemeActive
import git.shin.animevsub.ui.theme.TextGrey

@Composable
fun Badge(
  text: String,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = TextStyle.Default
) {
  Box(
    modifier = modifier
      .background(DarkCard, RoundedCornerShape(4.dp))
      .then(
        if (isChristmasThemeActive()) {
          Modifier.border(1.dp, ChristmasGold.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        } else {
          Modifier
        }
      )
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = text,
      color = if (isChristmasThemeActive()) ChristmasGold else TextGrey,
      fontSize = 11.sp,
      style = textStyle
    )
  }
}
