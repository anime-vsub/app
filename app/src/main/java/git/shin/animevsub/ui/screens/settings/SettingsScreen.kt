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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.account.MenuSection
import git.shin.animevsub.ui.components.account.SettingsSelector
import git.shin.animevsub.ui.components.account.SettingsSlider
import git.shin.animevsub.ui.components.account.SettingsToggle
import git.shin.animevsub.ui.components.player.CustomTimePickerDialog
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.formatDurationMinutes
import git.shin.animevsub.ui.utils.formatTimeMinutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  var showStartTimePicker by remember { mutableStateOf(false) }
  var showEndTimePicker by remember { mutableStateOf(false) }

  if (showStartTimePicker) {
    val state = rememberTimePickerState(
      initialHour = (uiState.bedtimeReminderStartTime / 60).toInt(),
      initialMinute = (uiState.bedtimeReminderStartTime % 60).toInt(),
      is24Hour = true
    )
    CustomTimePickerDialog(
      onDismissRequest = { showStartTimePicker = false },
      onConfirm = {
        viewModel.setBedtimeReminderStartTime((state.hour * 60 + state.minute).toLong())
        showStartTimePicker = false
        showEndTimePicker = true
      }
    ) {
      TimePicker(state = state)
    }
  }

  if (showEndTimePicker) {
    val state = rememberTimePickerState(
      initialHour = (uiState.bedtimeReminderEndTime / 60).toInt(),
      initialMinute = (uiState.bedtimeReminderEndTime % 60).toInt(),
      is24Hour = true
    )
    CustomTimePickerDialog(
      onDismissRequest = { showEndTimePicker = false },
      onConfirm = {
        viewModel.setBedtimeReminderEndTime((state.hour * 60 + state.minute).toLong())
        showEndTimePicker = false
      }
    ) {
      TimePicker(state = state)
    }
  }

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
        SettingsSelector(
          label = stringResource(R.string.app_language),
          selectedOption = uiState.appLanguage,
          options = listOf(
            "auto" to stringResource(R.string.language_auto),
            "vi" to stringResource(R.string.vietnamese),
            "en" to stringResource(R.string.english),
            "ja" to stringResource(R.string.japanese),
            "zh" to stringResource(R.string.chinese)
          ),
          onOptionSelected = { viewModel.setAppLanguage(it) }
        )
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

      MenuSection(title = stringResource(R.string.general)) {
        SettingsSelector(
          label = stringResource(R.string.screen_transition),
          selectedOption = uiState.screenTransition,
          options = listOf(
            "system" to stringResource(R.string.transition_system),
            "slide" to stringResource(R.string.transition_slide),
            "fade" to stringResource(R.string.transition_fade),
            "none" to stringResource(R.string.transition_none)
          ),
          onOptionSelected = { viewModel.setScreenTransition(it) }
        )

        SettingsToggle(
          label = stringResource(R.string.remind_me_to_take_a_break),
          checked = uiState.breakReminderEnabled,
          onCheckedChange = { viewModel.setBreakReminderEnabled(it) }
        )
        if (uiState.breakReminderEnabled) {
          SettingsSlider(
            label = stringResource(R.string.reminder_frequency),
            value = uiState.breakReminderInterval,
            onValueChange = { viewModel.setBreakReminderInterval(it) },
            valueRange = 15f..480f,
            steps = 31,
            valueText = stringResource(R.string.reminder_every_format, formatDurationMinutes(uiState.breakReminderInterval))
          )
        }

        SettingsToggle(
          label = stringResource(R.string.remind_me_when_its_bedtime),
          checked = uiState.bedtimeReminderEnabled,
          onCheckedChange = { viewModel.setBedtimeReminderEnabled(it) }
        )
        if (uiState.bedtimeReminderEnabled) {
          git.shin.animevsub.ui.components.account.MenuItem(
            icon = Icons.Default.Bedtime,
            label = stringResource(
              R.string.bedtime_reminder_schedule,
              formatTimeMinutes(uiState.bedtimeReminderStartTime),
              formatTimeMinutes(uiState.bedtimeReminderEndTime)
            ),
            onClick = { showStartTimePicker = true }
          )
          SettingsToggle(
            label = stringResource(R.string.wait_until_i_finish_video_to_show_reminder),
            checked = uiState.bedtimeReminderWaitFinish,
            onCheckedChange = { viewModel.setBedtimeReminderWaitFinish(it) }
          )
        }
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

      if (uiState.isDeveloperMode) {
        Spacer(modifier = Modifier.height(16.dp))
        MenuSection(title = stringResource(R.string.developer_options_title)) {
          SettingsToggle(
            label = stringResource(R.string.hide_donation_popups),
            checked = uiState.hideDonationPopup,
            onCheckedChange = { viewModel.setHideDonationPopup(it) }
          )
        }
      }
    }
  }
}
