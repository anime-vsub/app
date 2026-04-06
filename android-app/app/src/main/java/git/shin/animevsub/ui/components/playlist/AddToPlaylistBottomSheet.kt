package git.shin.animevsub.ui.components.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.ui.screens.account.AccountViewModel
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
  onDismissRequest: () -> Unit,
  onPlaylistSelected: (Int) -> Unit,
  accountViewModel: AccountViewModel = hiltViewModel()
) {
  val uiState by accountViewModel.uiState.collectAsState()
  val sheetState = rememberModalBottomSheetState()
  var showCreateDialog by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = DarkSurface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 32.dp)
    ) {
      Text(
        text = stringResource(R.string.add_to_playlist),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(16.dp)
      )

      LazyColumn {
        items(uiState.playlists) { playlist ->
          ListItem(
            headlineContent = { Text(playlist.name, color = TextPrimary) },
            supportingContent = {
              Text(
                stringResource(R.string.video_count, playlist.movieCount),
                color = TextSecondary
              )
            },
            leadingContent = {
              AsyncImage(
                model = playlist.poster,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                  .width(64.dp)
                  .aspectRatio(4 / 3f)
                  .clip(RoundedCornerShape(4.dp))
                  .background(DarkCard),
                error = coil.compose.rememberAsyncImagePainter(Icons.AutoMirrored.Filled.PlaylistPlay)
              )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier = Modifier.clickable {
              onPlaylistSelected(playlist.id)
              onDismissRequest()
            }
          )
        }

        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.create_playlist),
                color = AccentMain
              )
            },
            leadingContent = {
              Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = AccentMain
              )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier = Modifier.clickable {
              showCreateDialog = true
            }
          )
        }
      }
    }
  }

  if (showCreateDialog) {
    CreatePlaylistDialog(
      onDismiss = { showCreateDialog = false },
      onCreate = { name ->
        accountViewModel.createPlaylist(name)
        showCreateDialog = false
      }
    )
  }
}

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
