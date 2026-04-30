package git.shin.animevsub.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.MainActivity
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.account.MenuItem
import git.shin.animevsub.ui.components.account.MenuSection
import git.shin.animevsub.ui.components.account.SettingsSelector
import git.shin.animevsub.ui.components.account.SettingsSlider
import git.shin.animevsub.ui.components.account.SettingsToggle
import git.shin.animevsub.ui.components.player.CustomTimePickerDialog
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.ErrorColor
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.formatDurationMinutes
import git.shin.animevsub.ui.utils.formatTimeMinutes
import kotlinx.coroutines.launch

@Composable
fun AppIconSelector(
  selectedIcon: String,
  onIconSelected: (String) -> Unit
) {
  val icons = listOf(
    "default" to R.drawable.ic_launcher_foreground,
    "old" to R.drawable.ic_launcher_old,
    "vibrant" to R.drawable.ic_launcher_foreground_vibrant,
    "rainbow" to R.drawable.ic_launcher_foreground_rainbow,
    "neon" to R.drawable.ic_launcher_foreground_neon,
    "ai" to R.drawable.ic_launcher_foreground_ai
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Text(
      text = stringResource(R.string.app_icon),
      color = TextPrimary,
      fontSize = 15.sp,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      icons.forEach { (id, resId) ->
        val isSelected = selectedIcon == id
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onIconSelected(id) }
            .padding(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(RoundedCornerShape(12.dp))
              .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
              )
          ) {
            Icon(
              painter = painterResource(resId),
              contentDescription = id,
              tint = Color.Unspecified,
              modifier = Modifier
                .fillMaxSize()
                .padding(if (isSelected) 8.dp else 4.dp)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = when (id) {
              "default" -> stringResource(R.string.app_icon_default)
              "old" -> stringResource(R.string.app_icon_old)
              "vibrant" -> stringResource(R.string.app_icon_vibrant)
              "rainbow" -> stringResource(R.string.app_icon_rainbow)
              "neon" -> stringResource(R.string.app_icon_neon)
              "ai" -> stringResource(R.string.app_icon_ai)
              else -> id
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else TextPrimary
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
          SettingsToggle(
            label = stringResource(R.string.dynamic_color),
            checked = uiState.dynamicColor,
            onCheckedChange = { viewModel.setDynamicColor(it) }
          )
        }

        AppIconSelector(
          selectedIcon = uiState.appIcon,
          onIconSelected = {
            viewModel.setAppIcon(it)
            (context as? MainActivity)?.setAppIcon(it)
          }
        )

        val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
          if (uri != null) {
            scope.launch {
              (context as? MainActivity)?.createCustomShortcut(uri)
            }
          }
        }

        MenuItem(
          icon = Icons.Default.Image,
          label = stringResource(R.string.custom_shortcut),
          onClick = {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          }
        )

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
          MenuItem(
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

      Spacer(modifier = Modifier.height(16.dp))

      var showAiGuide by remember { mutableStateOf(false) }
      val uriHandler = LocalUriHandler.current

      MenuSection(title = stringResource(R.string.ai_features)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          OutlinedTextField(
            value = uiState.geminiApiKey,
            onValueChange = { viewModel.setGeminiApiKey(it) },
            label = { Text(stringResource(R.string.gemini_api_key)) },
            placeholder = { Text(stringResource(R.string.gemini_api_key_hint)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
              IconButton(onClick = { showAiGuide = true }) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = AccentMain)
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedBorderColor = AccentMain,
              unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = { viewModel.testGeminiKey() },
              enabled = !uiState.isTestingKey && uiState.geminiApiKey.isNotBlank(),
              colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
              modifier = Modifier.weight(1f)
            ) {
              if (uiState.isTestingKey) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
              } else {
                Text(stringResource(R.string.test_api_key))
              }
            }

            Button(
              onClick = { uriHandler.openUri("https://aistudio.google.com/app/apikey") },
              colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
              modifier = Modifier.weight(1f)
            ) {
              Text(stringResource(R.string.get_api_key), color = TextPrimary)
            }
          }

          uiState.testResult?.let { result ->
            val isSuccess = uiState.testSuccess
            Text(
              text = result,
              color = if (isSuccess) Color(0xFF4CAF50) else ErrorColor,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Model:",
              color = TextPrimary,
              fontSize = 14.sp,
              modifier = Modifier.weight(0.3f)
            )
            Box(modifier = Modifier.weight(0.7f)) {
              var expanded by remember { mutableStateOf(false) }
              OutlinedTextField(
                value = uiState.geminiModel,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                  Row {
                    if (uiState.availableModels.isNotEmpty()) {
                      IconButton(onClick = { expanded = true }) {
                        Icon(
                          Icons.Default.ArrowDropDown,
                          contentDescription = null,
                          tint = TextSecondary
                        )
                      }
                    }
                    IconButton(onClick = { viewModel.loadAvailableModels() }) {
                      if (uiState.isLoadingModels) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = AccentMain)
                      } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary)
                      }
                    }
                  }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedBorderColor = AccentMain,
                  unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                )
              )
              DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
              ) {
                uiState.availableModels.forEach { model ->
                  DropdownMenuItem(
                    text = { Text(model, color = TextPrimary) },
                    onClick = {
                      viewModel.setGeminiModel(model)
                      expanded = false
                    }
                  )
                }
              }
            }
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DarkSurface)

        SettingsToggle(
          label = stringResource(R.string.ai_summary_enabled),
          description = stringResource(R.string.ai_summary_desc),
          checked = uiState.aiSummaryEnabled,
          onCheckedChange = { viewModel.setAiSummaryEnabled(it) }
        )
        SettingsToggle(
          label = stringResource(R.string.ai_recap_enabled),
          description = stringResource(R.string.ai_recap_desc),
          checked = uiState.aiRecapEnabled,
          onCheckedChange = { viewModel.setAiRecapEnabled(it) }
        )
      }

      if (showAiGuide) {
        AlertDialog(
          onDismissRequest = { showAiGuide = false },
          title = { Text(stringResource(R.string.gemini_guide_title), color = TextPrimary) },
          text = { Text(stringResource(R.string.gemini_guide_content), color = TextSecondary) },
          confirmButton = {
            TextButton(onClick = { showAiGuide = false }) {
              Text(stringResource(R.string.ok), color = AccentMain)
            }
          },
          containerColor = DarkCard
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
          SettingsSlider(
            label = stringResource(R.string.history_sync_interval),
            value = uiState.historySyncInterval,
            onValueChange = { viewModel.setHistorySyncInterval(it) },
            valueRange = 10f..120f,
            steps = 11,
            valueText = stringResource(R.string.seconds_label, uiState.historySyncInterval)
          )

          Button(
            onClick = { viewModel.testNotification() },
            colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp)
          ) {
            Text("Test System Notification", color = Color.White)
          }
        }
      }
    }
  }
}
