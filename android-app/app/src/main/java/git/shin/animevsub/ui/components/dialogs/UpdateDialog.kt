package git.shin.animevsub.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.UpdateInfo
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun UpdateDialog(
  info: UpdateInfo,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = stringResource(R.string.update_available), color = TextPrimary) },
    text = {
      Column {
        Text(
          text = stringResource(R.string.update_message, info.version),
          color = TextSecondary
        )
        if (info.description.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = info.description,
            color = TextGrey,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(text = stringResource(R.string.update_now), color = TextPrimary)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel), color = TextGrey)
      }
    },
    containerColor = DarkCard
  )
}
