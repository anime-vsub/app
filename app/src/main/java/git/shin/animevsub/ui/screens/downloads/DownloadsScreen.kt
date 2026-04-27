package git.shin.animevsub.ui.screens.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.local.download.DownloadEntity
import git.shin.animevsub.data.local.download.DownloadStatus
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.ErrorColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.tvFocusScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String, String?) -> Unit,
  viewModel: DownloadsViewModel = hiltViewModel()
) {
  val downloads by viewModel.downloads.collectAsState()

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.downloads),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back),
              tint = TextPrimary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
      )
    },
    containerColor = DarkBackground
  ) { padding ->
    Box(modifier = Modifier.padding(padding)) {
      if (downloads.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(text = stringResource(R.string.no_downloads), color = TextGrey)
        }
      } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          items(downloads, key = { it.id }) { item ->
            DownloadItemRow(
              item = item,
              onClick = { onNavigateToDetail(item.animeId, item.id) },
              onDelete = { viewModel.deleteDownload(item.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun DownloadItemRow(
  item: DownloadEntity,
  onClick: () -> Unit,
  onDelete: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .tvFocusScale()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .width(120.dp)
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(8.dp))
        .background(DarkCard)
    ) {
      AsyncImage(
        model = item.thumbnail,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
      
      if (item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.CONVERTING) {
         Box(
             modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
             contentAlignment = Alignment.Center
         ) {
             Text(text = "${item.progress}%", color = androidx.compose.ui.graphics.Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
         }
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = item.animeTitle,
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = item.episodeTitle,
        color = TextSecondary,
        fontSize = 13.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      
      if (item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.CONVERTING) {
          Spacer(modifier = Modifier.height(4.dp))
          LinearProgressIndicator(
              progress = { item.progress / 100f },
              modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
              color = AccentMain,
              trackColor = DarkCard
          )
          Text(
              text = when(item.status) {
                  DownloadStatus.QUEUED -> stringResource(R.string.download_queued)
                  DownloadStatus.DOWNLOADING -> stringResource(R.string.download_downloading)
                  DownloadStatus.CONVERTING -> stringResource(R.string.download_converting)
                  DownloadStatus.COMPLETED -> stringResource(R.string.download_completed)
                  DownloadStatus.FAILED -> stringResource(R.string.download_failed)
              },
              color = if (item.status == DownloadStatus.FAILED) ErrorColor else AccentMain,
              fontSize = 11.sp
          )
      } else if (item.status == DownloadStatus.FAILED) {
           Text(
              text = item.errorMessage ?: stringResource(R.string.download_failed),
              color = ErrorColor,
              fontSize = 11.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
          )
      } else {
          Text(
              text = stringResource(R.string.download_completed),
              color = TextGrey,
              fontSize = 11.sp
          )
      }
    }

    IconButton(onClick = onDelete) {
      Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = stringResource(R.string.delete),
        tint = TextGrey,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}
