package git.shin.animevsub.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.ErrorColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun AccountScreen(
  onNavigateToLogin: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToFollow: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToAbout: () -> Unit,
  onNavigateToPlaylists: () -> Unit,
  viewModel: AccountViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .background(DarkBackground)
      .padding(16.dp)
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Profile section
    if (uiState.isLoggedIn && uiState.user != null) {
      val user = uiState.user!!
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkCard)
          .padding(16.dp)
      ) {
        AsyncImage(
          model = user.avatar,
          contentDescription = user.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(DarkSurface)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = user.name,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = user.email,
            color = TextSecondary,
            fontSize = 13.sp
          )
        }
      }
    } else {
      // Login prompt
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkCard)
          .clickable(onClick = onNavigateToLogin)
          .padding(20.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = AccentMain,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = stringResource(R.string.login),
            color = AccentMain,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = stringResource(R.string.login_required),
            color = TextSecondary,
            fontSize = 13.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Menu items
    MenuSection(title = stringResource(R.string.personal)) {
      MenuItem(
        icon = Icons.Default.History,
        label = stringResource(R.string.history),
        onClick = onNavigateToHistory
      )
      MenuItem(
        icon = Icons.Default.Favorite,
        label = stringResource(R.string.follow),
        onClick = onNavigateToFollow
      )
      MenuItem(
        icon = Icons.AutoMirrored.Filled.PlaylistPlay,
        label = stringResource(R.string.playlists),
        onClick = onNavigateToPlaylists
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    MenuSection(title = stringResource(R.string.settings)) {
      SettingsToggle(
        label = stringResource(R.string.auto_next),
        checked = uiState.autoNext,
        onCheckedChange = { viewModel.setAutoNext(it) }
      )
      SettingsToggle(
        label = stringResource(R.string.auto_skip),
        checked = uiState.autoSkip,
        onCheckedChange = { viewModel.setAutoSkip(it) }
      )
      SettingsToggle(
        label = stringResource(R.string.movie_mode),
        checked = uiState.movieMode,
        onCheckedChange = { viewModel.setMovieMode(it) }
      )
      SettingsToggle(
        label = stringResource(R.string.show_comments),
        checked = uiState.showComments,
        onCheckedChange = { viewModel.setShowComments(it) }
      )
      SettingsToggle(
        label = stringResource(R.string.infinite_scroll),
        checked = uiState.infiniteScroll,
        onCheckedChange = { viewModel.setInfiniteScroll(it) }
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    MenuSection(title = stringResource(R.string.general)) {
      MenuItem(
        icon = Icons.Default.Settings,
        label = stringResource(R.string.settings),
        onClick = onNavigateToSettings
      )
      MenuItem(
        icon = Icons.Default.Info,
        label = stringResource(R.string.about),
        onClick = onNavigateToAbout
      )
    }

    // Logout button
    if (uiState.isLoggedIn) {
      Spacer(modifier = Modifier.height(24.dp))
      Button(
        onClick = { viewModel.logout() },
        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Logout,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.logout))
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }
}

@Composable
private fun MenuSection(
  title: String,
  content: @Composable ColumnScope.() -> Unit
) {
  Text(
    text = title,
    color = TextGrey,
    fontSize = 13.sp,
    fontWeight = FontWeight.Medium,
    modifier = Modifier.padding(bottom = 8.dp)
  )
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkCard),
    content = content
  )
}

@Composable
private fun MenuItem(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = AccentMain,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = label,
      color = TextPrimary,
      fontSize = 15.sp,
      modifier = Modifier.weight(1f)
    )
    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForward,
      contentDescription = null,
      tint = TextGrey,
      modifier = Modifier.size(16.dp)
    )
  }
}

@Composable
private fun SettingsToggle(
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      color = TextPrimary,
      fontSize = 15.sp,
      modifier = Modifier.weight(1f)
    )
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = AccentMain,
        checkedTrackColor = AccentMain.copy(alpha = 0.3f),
        uncheckedThumbColor = TextGrey,
        uncheckedTrackColor = DarkSurface
      )
    )
  }
}
