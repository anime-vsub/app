package git.shin.animevsub.ui.components.account

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.local.download.DownloadEntity
import git.shin.animevsub.data.local.download.DownloadStatus
import git.shin.animevsub.ui.components.common.ErrorRetrySection
import git.shin.animevsub.ui.components.common.SectionHeader
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.ErrorColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.tvFocusScale

@Composable
fun DownloadHorizontalList(
  downloads: List<DownloadEntity>,
  isLoading: Boolean,
  error: String?,
  onHeaderClick: () -> Unit,
  onRetry: () -> Unit,
  onItemClick: (DownloadEntity) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    SectionHeader(
      title = stringResource(R.string.downloads),
      onViewAll = onHeaderClick
    )

    if (isLoading) {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(5) {
          DownloadItemSkeleton()
        }
      }
    } else if (error != null) {
      ErrorRetrySection(onRetry = onRetry)
    } else if (downloads.isEmpty()) {
      Text(
        text = stringResource(R.string.no_downloads),
        color = TextGrey,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    } else {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(downloads, key = { it.id }) { item ->
          DownloadCompactCard(item = item, onClick = { onItemClick(item) })
        }
      }
    }
  }
}

@Composable
fun DownloadCompactCard(
  item: DownloadEntity,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .width(200.dp)
      .clip(RoundedCornerShape(8.dp))
      .tvFocusScale()
      .clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(8.dp))
        .background(DarkCard)
    ) {
      AsyncImage(
        model = item.thumbnail,
        contentDescription = item.animeTitle,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      // Status overlay
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            if (item.status == DownloadStatus.COMPLETED) androidx.compose.ui.graphics.Color.Transparent
            else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)
          ),
        contentAlignment = Alignment.Center
      ) {
        when (item.status) {
          DownloadStatus.QUEUED -> Icon(
            Icons.Default.Download,
            null,
            tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
          )
          DownloadStatus.DOWNLOADING, DownloadStatus.CONVERTING -> Icon(
            Icons.Default.Sync,
            null,
            tint = AccentMain
          )
          DownloadStatus.FAILED -> Icon(
            Icons.Default.Error,
            null,
            tint = ErrorColor
          )
          DownloadStatus.COMPLETED -> {
             Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.BottomEnd) {
                Icon(Icons.Default.CheckCircle, null, tint = AccentMain, modifier = Modifier.size(20.dp))
             }
          }
        }
      }
      
      if (item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.CONVERTING) {
          LinearProgressIndicator(
              progress = { item.progress / 100f },
              modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).height(4.dp),
              color = AccentMain,
              trackColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f)
          )
      }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Text(
      text = item.animeTitle,
      color = TextPrimary,
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    
    Text(
      text = item.episodeTitle,
      color = TextSecondary,
      fontSize = 12.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun DownloadItemSkeleton() {
  Column(modifier = Modifier.width(200.dp)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(8.dp))
        .background(DarkCard)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .height(14.dp)
        .background(DarkCard, RoundedCornerShape(2.dp))
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.5f)
        .height(12.dp)
        .background(DarkCard, RoundedCornerShape(2.dp))
    )
  }
}
