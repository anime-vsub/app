package git.shin.animevsub.ui.components.player

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.DarkSurface

@Composable
fun BedtimeReminderDialog(
  onDismiss: () -> Unit,
  onSettings: () -> Unit
) {
  AlertDialog(
    onDismissRequest = {},
    properties = DialogProperties(
      dismissOnBackPress = false,
      dismissOnClickOutside = false
    ),
    containerColor = DarkSurface,
    title = { Text(text = stringResource(R.string.reminder_title)) },
    text = { Text(text = stringResource(R.string.bedtime_message)) },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.dismiss))
      }
    },
    dismissButton = {
      TextButton(onClick = onSettings) {
        Text(text = stringResource(R.string.settings_capital))
      }
    }
  )
}
