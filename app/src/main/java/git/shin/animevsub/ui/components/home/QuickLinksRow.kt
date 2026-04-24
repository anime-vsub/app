package git.shin.animevsub.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.tvFocusScale

@Composable
fun QuickLinksRow(
  onCatalogClick: () -> Unit,
  onScheduleClick: () -> Unit,
  onRankingsClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.Center
  ) {
    QuickLinkItem(
      label = stringResource(R.string.catalog),
      icon = painterResource(id = R.drawable.icon_tool_alp),
      onClick = onCatalogClick,
      modifier = Modifier
        .weight(1f)
        .tvFocusScale()
    )
    QuickLinkItem(
      label = stringResource(R.string.schedule),
      icon = painterResource(id = R.drawable.icon_tool_calc),
      onClick = onScheduleClick,
      modifier = Modifier
        .weight(1f)
        .tvFocusScale()
    )
    QuickLinkItem(
      label = stringResource(R.string.rankings),
      icon = painterResource(id = R.drawable.icon_tool_rank),
      onClick = onRankingsClick,
      modifier = Modifier
        .weight(1f)
        .tvFocusScale()
    )
  }
}

@Composable
private fun QuickLinkItem(
  label: String,
  icon: Painter,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(8.dp)
  ) {
    Image(
      painter = icon,
      contentDescription = label,
      modifier = Modifier.size(32.dp)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      color = TextPrimary,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center
    )
  }
}
