package git.shin.animevsub.ui.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.tvFocusScale

@Composable
fun AiRecapBlock(
  aiRecap: String?,
  isRecapLoading: Boolean,
  recapError: String?,
  onGenerateClick: () -> Unit,
  onExpandClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (isRecapLoading || !aiRecap.isNullOrBlank() || recapError != null) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(MainColor.copy(alpha = 0.05f))
        .border(1.dp, MainColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        .tvFocusScale()
        .clickable {
          if (aiRecap == null && !isRecapLoading) {
            onGenerateClick()
          } else {
            onExpandClick()
          }
        }
        .padding(12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = MainColor,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(R.string.ai_recap_title),
          color = MainColor,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        if (isRecapLoading) {
          Spacer(modifier = Modifier.width(8.dp))
          CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
            color = MainColor
          )
        }
      }

      if (!aiRecap.isNullOrBlank()) {
        MarkdownText(
          markdown = aiRecap,
          maxLines = 4,
          truncateOnTextOverflow = true,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 18.sp
          ),
          modifier = Modifier.fillMaxWidth()
        )
      } else if (recapError != null) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = recapError,
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
          )
          TextButton(onClick = onGenerateClick) {
            Text(stringResource(R.string.retry), color = MainColor, fontSize = 13.sp)
          }
        }
      } else if (aiRecap == null && !isRecapLoading) {
        // Placeholder when not loaded yet
        Text(
          text = stringResource(R.string.ai_recap_not_loaded),
          color = TextSecondary,
          fontSize = 13.sp,
          fontStyle = FontStyle.Italic
        )
      }
    }
  }
}
