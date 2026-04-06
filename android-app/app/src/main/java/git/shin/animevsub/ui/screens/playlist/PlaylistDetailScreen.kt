package git.shin.animevsub.ui.screens.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.model.PlaylistItem
import git.shin.animevsub.ui.components.playlist.AddToPlaylistBottomSheet
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.components.status.LoadingScreen
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String) -> Unit,
  onNavigateToPlayer: (String, String) -> Unit,
  viewModel: PlaylistViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val scrollState = rememberLazyListState()
  rememberCoroutineScope()

  var showEditNameDialog by remember { mutableStateOf(false) }
  var showEditDescriptionDialog by remember { mutableStateOf(false) }
  var showDeleteConfirmDialog by remember { mutableStateOf(false) }
  var selectedItemForMenu by remember { mutableStateOf<PlaylistItem?>(null) }

  LaunchedEffect(scrollState) {
    snapshotFlow { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
      .collect { lastIndex ->
        if (lastIndex != null && lastIndex >= uiState.items.size - 5) {
          viewModel.loadMore()
        }
      }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = uiState.playlist?.name ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.Transparent,
          titleContentColor = TextPrimary,
          navigationIconContentColor = TextPrimary
        )
      )
    },
    containerColor = DarkBackground
  ) { padding ->
    Box(modifier = Modifier.fillMaxSize()) {
      if (uiState.isRefreshing && uiState.playlist == null) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          item { PlaylistHeaderSkeleton() }
          items(5) { PlaylistItemSkeleton() }
        }
      } else if (uiState.error != null && uiState.playlist == null) {
        ErrorScreen(error = uiState.error!!, onRetry = { viewModel.refresh() })
      } else {
        LazyColumn(
          state = scrollState,
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 16.dp)
        ) {
          item {
            PlaylistHeader(
              playlist = uiState.playlist,
              onEditName = { showEditNameDialog = true },
              onEditDescription = { showEditDescriptionDialog = true },
              onDeletePlaylist = { showDeleteConfirmDialog = true }
            )
          }

          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              var showSortMenu by remember { mutableStateOf(false) }

              Button(
                onClick = { showSortMenu = true },
                colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
              ) {
                Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (uiState.sorter == "asc") stringResource(R.string.newest_added)
                  else stringResource(R.string.oldest_added),
                  fontSize = 14.sp
                )

                DropdownMenu(
                  expanded = showSortMenu,
                  onDismissRequest = { showSortMenu = false },
                  modifier = Modifier.background(DarkSurface)
                ) {
                  DropdownMenuItem(
                    text = { Text(stringResource(R.string.newest_added)) },
                    onClick = {
                      viewModel.setSorter("asc")
                      showSortMenu = false
                    }
                  )
                  DropdownMenuItem(
                    text = { Text(stringResource(R.string.oldest_added)) },
                    onClick = {
                      viewModel.setSorter("desc")
                      showSortMenu = false
                    }
                  )
                }
              }
            }
          }

          if (uiState.items.isEmpty() && !uiState.isLoadingMore && !uiState.isRefreshing) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 64.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(text = stringResource(R.string.empty_playlist), color = TextGrey)
              }
            }
          } else {
            items(uiState.items, key = { it.seasonId }) { item ->
              PlaylistItemRow(
                item = item,
                onClick = {
                  if (item.chapId != null) {
                    onNavigateToPlayer(item.seasonId, item.chapId)
                  } else {
                    onNavigateToDetail(item.seasonId)
                  }
                },
                onMenuClick = { selectedItemForMenu = item }
              )
            }

            if (uiState.isLoadingMore) {
              items(3) {
                PlaylistItemSkeleton()
              }
            }
          }
        }
      }
    }

    // Dialogs and Menus
    if (showEditNameDialog) {
      var name by remember { mutableStateOf(uiState.playlist?.name ?: "") }
      AlertDialog(
        onDismissRequest = { showEditNameDialog = false },
        title = { Text(stringResource(R.string.rename_playlist)) },
        text = {
          TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
        },
        confirmButton = {
          TextButton(onClick = {
            viewModel.updatePlaylistName(name)
            showEditNameDialog = false
          }) {
            Text(stringResource(R.string.save))
          }
        },
        dismissButton = {
          TextButton(onClick = { showEditNameDialog = false }) {
            Text(stringResource(R.string.cancel))
          }
        }
      )
    }

    if (showEditDescriptionDialog) {
      var description by remember { mutableStateOf(uiState.playlist?.description ?: "") }
      AlertDialog(
        onDismissRequest = { showEditDescriptionDialog = false },
        title = { Text(stringResource(R.string.edit_description)) },
        text = {
          TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
          )
        },
        confirmButton = {
          TextButton(onClick = {
            viewModel.updatePlaylistDescription(description)
            showEditDescriptionDialog = false
          }) {
            Text(stringResource(R.string.save))
          }
        },
        dismissButton = {
          TextButton(onClick = { showEditDescriptionDialog = false }) {
            Text(stringResource(R.string.cancel))
          }
        }
      )
    }

    if (showDeleteConfirmDialog) {
      AlertDialog(
        onDismissRequest = { showDeleteConfirmDialog = false },
        title = { Text(stringResource(R.string.delete_playlist)) },
        text = { Text(stringResource(R.string.confirm_delete_playlist)) },
        confirmButton = {
          TextButton(onClick = {
            viewModel.deletePlaylist {
              onNavigateBack()
            }
            showDeleteConfirmDialog = false
          }) {
            Text(stringResource(R.string.delete), color = Color.Red)
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteConfirmDialog = false }) {
            Text(stringResource(R.string.cancel))
          }
        }
      )
    }

    if (selectedItemForMenu != null) {
      var showAddToPlaylistSheet by remember { mutableStateOf(false) }

      ModalBottomSheet(
        onDismissRequest = { selectedItemForMenu = null },
        containerColor = DarkSurface
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
        ) {
          ListItem(
            headlineContent = { Text(stringResource(R.string.add_to_other_playlist)) },
            leadingContent = { Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null) },
            modifier = Modifier.clickable {
              showAddToPlaylistSheet = true
            }
          )
          ListItem(
            headlineContent = { Text(stringResource(R.string.remove_from_playlist)) },
            leadingContent = { Icon(Icons.Default.Delete, contentDescription = null) },
            modifier = Modifier.clickable {
              viewModel.removeAnimeFromPlaylist(selectedItemForMenu!!.seasonId)
              selectedItemForMenu = null
            }
          )
        }
      }

      if (showAddToPlaylistSheet) {
        AddToPlaylistBottomSheet(
          onDismissRequest = {
            showAddToPlaylistSheet = false
            selectedItemForMenu = null
          },
          onPlaylistSelected = { playlistId ->
            viewModel.addToOtherPlaylist(playlistId, selectedItemForMenu!!)
            showAddToPlaylistSheet = false
            selectedItemForMenu = null
          }
        )
      }
    }
  }
}

@Composable
fun PlaylistHeader(
  playlist: git.shin.animevsub.data.model.Playlist?,
  onEditName: () -> Unit,
  onEditDescription: () -> Unit,
  onDeletePlaylist: () -> Unit
) {
  if (playlist == null) {
    PlaylistHeaderSkeleton()
    return
  }

  Box(
    modifier = Modifier.fillMaxWidth()
  ) {
    // Blur Background
    AsyncImage(
      model = playlist.poster,
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .matchParentSize()
        .blur(40.dp)
        .background(Color.Black.copy(alpha = 0.6f))
    )

    // Gradient overlay
    Box(
      modifier = Modifier
        .matchParentSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.5f),
              Color.Transparent,
              DarkBackground
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Poster
      Box(
        modifier = Modifier
          .fillMaxWidth() // Larger width
          .aspectRatio(16 / 9f)
          .clip(RoundedCornerShape(12.dp))
          .background(DarkCard)
      ) {
        // Blur background to fill the box
        AsyncImage(
          model = playlist.poster,
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxSize()
            .blur(20.dp)
            .background(Color.Black.copy(alpha = 0.3f))
        )
        // Main poster (Fit)
        AsyncImage(
          model = playlist.poster,
          contentDescription = null,
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Playlist Name and Edit
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = playlist.name,
          color = TextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f, fill = false)
        )
        IconButton(onClick = onEditName) {
          Icon(
            Icons.Default.Edit,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Info
      Text(
        text = stringResource(R.string.video_count, playlist.movieCount),
        color = TextGrey,
        fontSize = 14.sp
      )

      // Actions
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(
          onClick = onDeletePlaylist,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.1f))
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, tint = TextPrimary)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Description
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onEditDescription() }
          .clip(RoundedCornerShape(8.dp))
          .background(Color.White.copy(alpha = 0.05f))
          .padding(12.dp),
        verticalAlignment = Alignment.Top
      ) {
        Text(
          text = playlist.description?.ifBlank { stringResource(R.string.coming_soon_desc) }
            ?: stringResource(R.string.coming_soon_desc),
          color = TextSecondary,
          fontSize = 14.sp,
          maxLines = 5,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        Icon(
          Icons.Default.Edit,
          contentDescription = null,
          tint = TextGrey,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
fun PlaylistHeaderSkeleton() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16 / 9f)
        .clip(RoundedCornerShape(12.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.height(20.dp))
    Box(
      modifier = Modifier
        .width(200.dp)
        .height(28.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Box(
      modifier = Modifier
        .width(100.dp)
        .height(16.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(DarkCard)
    )
  }
}

@Composable
fun PlaylistItemSkeleton() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .width(120.dp)
        .aspectRatio(16 / 9f)
        .clip(RoundedCornerShape(8.dp))
        .background(DarkCard)
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.7f)
          .height(18.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(DarkCard)
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.4f)
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(DarkCard)
      )
    }
  }
}

@Composable
fun PlaylistItemRow(
  item: PlaylistItem,
  onClick: () -> Unit,
  onMenuClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      model = item.poster,
      contentDescription = item.name,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .width(120.dp)
        .aspectRatio(16 / 9f)
        .clip(RoundedCornerShape(8.dp))
        .background(DarkCard)
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = item.name,
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = if (item.seasonName.isNotBlank()) "${item.seasonName} - ${item.chapName ?: ""}"
        else item.chapName ?: "",
        color = TextGrey,
        fontSize = 13.sp
      )
    }

    IconButton(onClick = onMenuClick) {
      Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextGrey)
    }
  }
}
