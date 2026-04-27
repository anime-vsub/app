package git.shin.animevsub.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary

@Composable
fun SectionHeader(
  title: String,
  modifier: Modifier = Modifier,
  onViewAll: (() -> Unit)? = null,
  onAddClick: (() -> Unit)? = null,
  compactArrow: Boolean = false
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .then(if (onViewAll != null && !compactArrow) Modifier.clickable(onClick = onViewAll) else Modifier),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = if (compactArrow) {
        Modifier.clickable(onClick = onViewAll ?: {})
      } else {
        Modifier.weight(1f)
      }
    ) {
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
      if (onViewAll != null) {
        if (compactArrow) {
          Spacer(modifier = Modifier.width(4.dp))
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = TextGrey,
          modifier = Modifier.size(18.dp)
        )
        if (!compactArrow && onAddClick == null) {
          Spacer(modifier = Modifier.width(8.dp))
        }
      }
    }
    if (onAddClick != null) {
      IconButton(
        onClick = onAddClick,
        modifier = Modifier.size(24.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = stringResource(R.string.create_playlist),
          tint = AccentMain,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
