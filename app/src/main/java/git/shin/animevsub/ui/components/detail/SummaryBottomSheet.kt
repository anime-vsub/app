package git.shin.animevsub.ui.components.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryBottomSheet(
  title: String,
  content: String?,
  isLoading: Boolean,
  error: String?,
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  onRetry: () -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = DarkSurface,
    dragHandle = { BottomSheetDefaults.DragHandle(color = TextGrey) }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .padding(bottom = 32.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = MainColor,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = title,
          color = TextPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (isLoading) {
        SummarySkeleton()
      } else if (error != null) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = error, color = TextSecondary, textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.height(16.dp))
          TextButton(onClick = onRetry) {
            Text(stringResource(R.string.retry), color = MainColor)
          }
        }
      } else if (content != null) {
        MarkdownText(
          markdown = content,
          style = MaterialTheme.typography.bodyLarge.copy(
            color = TextPrimary,
            fontSize = 16.sp,
            lineHeight = 24.sp
          )
        )
      }
    }
  }
}

@Composable
fun SummarySkeleton(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth()) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .height(18.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(12.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.7f)
        .height(18.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(12.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .height(18.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(12.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.5f)
        .height(18.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(24.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .height(18.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(12.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.6f)
        .height(18.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecapBottomSheet(
  recap: String?,
  isLoading: Boolean,
  error: String?,
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  onRetry: () -> Unit
) {
  SummaryBottomSheet(
    title = stringResource(R.string.ai_recap_title),
    content = recap,
    isLoading = isLoading,
    error = error,
    sheetState = sheetState,
    onDismissRequest = onDismissRequest,
    onRetry = onRetry
  )
}
