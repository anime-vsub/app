package git.shin.animevsub.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.account.MenuSection
import git.shin.animevsub.ui.components.account.SettingsSlider
import git.shin.animevsub.ui.components.account.SettingsToggle
import git.shin.animevsub.ui.screens.account.AccountViewModel
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit,
  viewModel: AccountViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.settings), color = TextPrimary) },
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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
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
      }

      Spacer(modifier = Modifier.height(16.dp))

      MenuSection(title = stringResource(R.string.gesture_controls)) {
        SettingsToggle(
          label = stringResource(R.string.volume_gesture),
          checked = uiState.volumeGesture,
          onCheckedChange = { viewModel.setVolumeGesture(it) }
        )
        SettingsToggle(
          label = stringResource(R.string.brightness_gesture),
          checked = uiState.brightnessGesture,
          onCheckedChange = { viewModel.setBrightnessGesture(it) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      MenuSection(title = stringResource(R.string.notification_settings)) {
        SettingsToggle(
          label = stringResource(R.string.enable_notifications),
          checked = uiState.enableNotifications,
          onCheckedChange = { viewModel.setEnableNotifications(it) }
        )
        SettingsToggle(
          label = stringResource(R.string.enable_background_sync),
          checked = uiState.enableBackgroundSync,
          onCheckedChange = { viewModel.setEnableBackgroundSync(it) }
        )
        SettingsToggle(
          label = stringResource(R.string.auto_sync_notify),
          checked = uiState.autoSyncNotify,
          onCheckedChange = { viewModel.setAutoSyncNotify(it) }
        )
        SettingsSlider(
          label = stringResource(R.string.notify_sync_interval),
          value = uiState.notifyInterval,
          onValueChange = { viewModel.setNotifyInterval(it) },
          valueRange = 15f..120f,
          steps = 6,
          valueText = stringResource(R.string.minutes_label, uiState.notifyInterval)
        )
        SettingsSlider(
          label = stringResource(R.string.db_notify_sync_interval),
          value = uiState.dbNotifyInterval,
          onValueChange = { viewModel.setDbNotifyInterval(it) },
          valueRange = 15f..240f,
          steps = 14,
          valueText = stringResource(R.string.minutes_label, uiState.dbNotifyInterval)
        )
      }
    }
  }
}
