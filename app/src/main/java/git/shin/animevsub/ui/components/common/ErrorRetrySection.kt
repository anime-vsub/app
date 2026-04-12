package git.shin.animevsub.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.ErrorColor

@Composable
fun ErrorRetrySection(
  onRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = stringResource(R.string.error_occurred),
      color = ErrorColor,
      fontSize = 13.sp,
      modifier = Modifier.weight(1f)
    )
    TextButton(
      onClick = onRetry,
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
      modifier = Modifier.height(32.dp)
    ) {
      Text(
        text = stringResource(R.string.retry),
        color = AccentMain,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
