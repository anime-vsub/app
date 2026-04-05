package git.shin.animevsub.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.components.status.LoadingScreen
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun NotificationScreen(
  onNavigateToDetail: (String) -> Unit,
  onNavigateToLogin: () -> Unit,
  viewModel: NotificationViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    // Title
    Text(
      text = stringResource(R.string.notifications),
      color = TextPrimary,
      fontSize = 20.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(16.dp)
    )

    if (!uiState.isLoggedIn) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Outlined.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextGrey
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = stringResource(R.string.login_required),
            color = TextSecondary,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onNavigateToLogin,
            colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(stringResource(R.string.login))
          }
        }
      }
    } else {
      when {
        uiState.isLoading -> LoadingScreen()
        uiState.error != null -> {
          ErrorScreen(error = uiState.error, onRetry = { viewModel.retry() })
        }

        uiState.data != null -> {
          val items = uiState.data!!.items
          if (items.isEmpty()) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Outlined.NotificationsNone,
                  contentDescription = null,
                  modifier = Modifier.size(64.dp),
                  tint = TextGrey
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                  text = stringResource(R.string.no_notifications),
                  color = TextSecondary,
                  fontSize = 14.sp
                )
              }
            }
          } else {
            LazyColumn(
              contentPadding = PaddingValues(horizontal = 16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(items) { notification ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                      if (notification.isRead) DarkCard
                      else DarkCard.copy(alpha = 0.8f)
                    )
                    .clickable {
                      if (notification.link.isNotEmpty()) {
                        val animeId = notification.animeId
                        if (animeId != null) {
                          onNavigateToDetail(animeId)
                        }
                      }
                    }
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  AsyncImage(
                    model = notification.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                      .size(40.dp)
                      .clip(CircleShape)
                      .background(DarkSurface)
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = notification.content,
                      color = TextPrimary,
                      fontSize = 13.sp,
                      maxLines = 3,
                      overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = notification.time,
                      color = TextGrey,
                      fontSize = 11.sp
                    )
                  }
                  if (!notification.isRead) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentMain)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
