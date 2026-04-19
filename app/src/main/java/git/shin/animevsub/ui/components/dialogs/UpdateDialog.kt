package git.shin.animevsub.ui.components.dialogs

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import git.shin.animevsub.R
import git.shin.animevsub.data.model.UpdateInfo
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDialog(
  info: UpdateInfo,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val configuration = LocalConfiguration.current
  val maxHeight = configuration.screenHeightDp.dp * 0.8f

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkSurface,
    dragHandle = { BottomSheetDefaults.DragHandle(color = TextSecondary.copy(alpha = 0.4f)) }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = maxHeight)
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      Text(
        text = stringResource(R.string.update_available),
        color = TextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(R.string.update_message, info.version),
        color = TextSecondary,
        fontSize = 14.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Markdown description area
      Column(
        modifier = Modifier
          .weight(1f, fill = false)
          .verticalScroll(rememberScrollState())
      ) {
        MarkdownText(
          markdown = info.description,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 22.sp
          ),
          linkColor = AccentMain,
          syntaxHighlightColor = Color.Transparent,
          syntaxHighlightTextColor = TextPrimary,
          beforeSetMarkdown = { _, spanned ->
            if (spanned is Spannable) {
              val spans = spanned.getSpans(0, spanned.length, TypefaceSpan::class.java)
              spans.forEach { span ->
                if (span.family == "monospace") {
                  val start = spanned.getSpanStart(span)
                  val end = spanned.getSpanEnd(span)
                  spanned.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                  )
                }
              }
            }
          }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(onClick = onDismiss) {
          Text(text = stringResource(R.string.cancel), color = TextSecondary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = onConfirm,
          colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = stringResource(R.string.update_now),
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
