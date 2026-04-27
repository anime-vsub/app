package git.shin.animevsub.ui.components.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.screens.account.AccountViewModel
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
  animeId: String,
  onDismissRequest: () -> Unit,
  onTogglePlaylist: suspend (Int, Boolean) -> Result<Unit>,
  accountViewModel: AccountViewModel = hiltViewModel()
) {
  val uiState by accountViewModel.uiState.collectAsState()
  val sheetState = rememberModalBottomSheetState()
  var showCreateDialog by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  LaunchedEffect(animeId) {
    accountViewModel.checkAnimeInPlaylists(animeId)
  }

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

      if (uiState.isLoadingPlaylists) {
        Column {
          repeat(3) {
            PlaylistBottomSheetSkeleton()
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f, fill = false)
        ) {
          items(uiState.playlists) { playlist ->
            val isChecked = uiState.playlistCheckedStates[playlist.id] ?: false
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  val nextChecked = !isChecked
                  scope.launch {
                    onTogglePlaylist(playlist.id, nextChecked)
                      .onSuccess {
                        accountViewModel.togglePlaylistChecked(playlist.id, nextChecked)
                        val message = if (nextChecked) {
                          context.getString(R.string.added_to_playlist, playlist.name)
                        } else {
                          context.getString(R.string.removed_from_playlist, playlist.name)
                        }
                        android.widget.Toast
                          .makeText(context, message, android.widget.Toast.LENGTH_SHORT)
                          .show()
                        onDismissRequest()
                      }
                      .onFailure { error ->
                        android.widget.Toast
                          .makeText(
                            context,
                            error.message ?: context.getString(R.string.error_occurred),
                            android.widget.Toast.LENGTH_SHORT
                          )
                          .show()
                      }
                  }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              PlaylistPoster(
                posterUrl = playlist.poster,
                modifier = Modifier.width(100.dp)
              )
              Spacer(modifier = Modifier.width(16.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = playlist.name,
                  color = TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Medium,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = pluralStringResource(
                    R.plurals.video_count,
                    playlist.movieCount,
                    playlist.movieCount
                  ),
                  color = TextSecondary,
                  fontSize = 12.sp
                )
              }
              Checkbox(
                checked = isChecked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                  checkedColor = AccentMain,
                  uncheckedColor = TextSecondary
                )
              )
            }
          }
        }
      }

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
fun PlaylistBottomSheetSkeleton() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .width(100.dp)
        .height(56.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.6f)
          .height(14.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(DarkCard)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.3f)
          .height(12.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(DarkCard)
      )
    }
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    )
  }
}
