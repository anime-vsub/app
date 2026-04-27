package git.shin.animevsub.ui.components.playlist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun CreatePlaylistDialog(
  onDismiss: () -> Unit,
  onCreate: (String) -> Unit
) {
  var playlistName by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.create_playlist), color = TextPrimary) },
    text = {
      OutlinedTextField(
        value = playlistName,
        onValueChange = { playlistName = it },
        label = { Text(stringResource(R.string.playlist_name)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          focusedBorderColor = AccentMain,
          unfocusedBorderColor = DarkCard
        )
      )
    },
    confirmButton = {
      TextButton(
        onClick = { if (playlistName.isNotBlank()) onCreate(playlistName) },
        enabled = playlistName.isNotBlank()
      ) {
        Text(stringResource(R.string.create), color = AccentMain)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel), color = TextSecondary)
      }
    },
    containerColor = DarkSurface
  )
}
