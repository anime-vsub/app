package git.shin.animevsub.ui.components.player

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import git.shin.animevsub.R
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun SettingsBottomSheetContent(
  settingsSubMenu: String?,
  onSubMenuChange: (String?) -> Unit,
  servers: List<ServerInfo>,
  currentServer: ServerInfo?,
  onServerSelected: (ServerInfo) -> Unit,
  availableQualities: List<String>,
  selectedQualityLabel: String,
  onQualitySelected: (String) -> Unit,
  speeds: List<Float>,
  playbackSpeed: Float,
  onSpeedSelected: (Float) -> Unit,
  volumeGestureEnabled: Boolean,
  onVolumeGestureToggle: (Boolean) -> Unit,
  brightnessGestureEnabled: Boolean,
  onBrightnessGestureToggle: (Boolean) -> Unit,
  autoSkipEnabled: Boolean,
  onAutoSkipToggle: (Boolean) -> Unit,
  doubleTapSkipDuration: Int,
  onDoubleTapSkipDurationChange: (Int) -> Unit,
  longPressSpeed: Float,
  onLongPressSpeedChange: (Float) -> Unit,
  syncMode: Int,
  onSyncModeChange: (Int) -> Unit,
  sleepTimerMinutes: Int,
  onSleepTimerChange: (Int) -> Unit,
  pauseAfterCurrentEpisode: Boolean,
  onPauseAfterCurrentEpisodeChange: (Boolean) -> Unit,
  sleepTimerRemainingSeconds: Long,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val onSupportClick = { subjectType: String ->
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data =
        "mailto:support@animevsub.eu.org?subject=$subjectType%20application%20git.shin.animevsub".toUri()
    }
    try {
      context.startActivity(intent)
    } catch (_: Exception) {
    }
    onDismiss()
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 32.dp)
  ) {
    AnimatedContent(
      targetState = settingsSubMenu,
      transitionSpec = {
        if (targetState != null) {
          slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        } else {
          slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
        }.using(SizeTransform(clip = false))
      },
      label = "SettingsMenu"
    ) { subMenu ->
      when (subMenu) {
        null -> {
          Column {
            SettingsItem(
              icon = Icons.Default.BugReport,
              title = stringResource(R.string.report_bug),
              onClick = { onSupportClick("Report") }
            )
            SettingsItem(
              icon = Icons.Default.Feedback,
              title = stringResource(R.string.feedback),
              onClick = { onSupportClick("Feedback") }
            )
            HorizontalDivider(
              modifier = Modifier.padding(vertical = 8.dp),
              color = Color.White.copy(alpha = 0.1f)
            )
            SettingsToggleItem(
              icon = Icons.Default.SkipNext,
              title = stringResource(R.string.auto_skip),
              checked = autoSkipEnabled,
              onCheckedChange = onAutoSkipToggle
            )
            SettingsToggleItem(
              icon = Icons.AutoMirrored.Filled.VolumeUp,
              title = stringResource(R.string.volume_gesture),
              checked = volumeGestureEnabled,
              onCheckedChange = onVolumeGestureToggle
            )
            SettingsToggleItem(
              icon = Icons.Default.BrightnessLow,
              title = stringResource(R.string.brightness_gesture),
              checked = brightnessGestureEnabled,
              onCheckedChange = onBrightnessGestureToggle
            )
            SettingsItem(
              icon = Icons.Default.History,
              title = stringResource(R.string.double_tap_skip),
              value = stringResource(R.string.seconds_unit, doubleTapSkipDuration),
              onClick = { onSubMenuChange("doubleTapSkip") }
            )
            SettingsItem(
              icon = Icons.Default.Speed,
              title = stringResource(R.string.long_press_speed),
              value = "${longPressSpeed}x",
              onClick = { onSubMenuChange("longPressSpeed") }
            )
            SettingsItem(
              icon = Icons.Default.Sync,
              title = stringResource(R.string.sync_mode),
              value = when (syncMode) {
                0 -> stringResource(R.string.sync_mode_full)
                1 -> stringResource(R.string.sync_mode_upload_only)
                else -> stringResource(R.string.sync_mode_none)
              },
              onClick = { onSubMenuChange("sync") }
            )
            SettingsItem(
              icon = Icons.Default.Timer,
              title = stringResource(R.string.sleep_timer),
              value = when {
                pauseAfterCurrentEpisode -> stringResource(R.string.sleep_timer_end_of_episode)
                sleepTimerMinutes > 0 -> {
                  val minutes = sleepTimerRemainingSeconds / 60
                  val seconds = sleepTimerRemainingSeconds % 60
                  stringResource(R.string.sleep_timer_remaining, String.format("%02d:%02d", minutes, seconds))
                }

                else -> stringResource(R.string.sleep_timer_off)
              },
              onClick = { onSubMenuChange("sleepTimer") }
            )
            HorizontalDivider(
              modifier = Modifier.padding(vertical = 8.dp),
              color = Color.White.copy(alpha = 0.1f)
            )
            SettingsItem(
              icon = Icons.Default.Dns,
              title = stringResource(R.string.server_label),
              value = currentServer?.name,
              onClick = { onSubMenuChange("server") }
            )
            SettingsItem(
              icon = Icons.Default.HighQuality,
              title = stringResource(R.string.quality),
              value = selectedQualityLabel,
              onClick = { onSubMenuChange("quality") }
            )
            SettingsItem(
              icon = Icons.Default.Speed,
              title = stringResource(R.string.playback_speed),
              value = "${if (playbackSpeed % 1.0f == 0.0f) playbackSpeed.toInt() else playbackSpeed}x",
              onClick = { onSubMenuChange("speed") }
            )
          }
        }

        "sleepTimer" -> {
          SettingsSubMenuContainer(
            title = stringResource(R.string.sleep_timer),
            onBack = { onSubMenuChange(null) }
          ) {
            SettingsOptionItem(
              title = stringResource(R.string.sleep_timer_off),
              isSelected = sleepTimerMinutes == 0 && !pauseAfterCurrentEpisode,
              onClick = {
                onSleepTimerChange(0)
                onPauseAfterCurrentEpisodeChange(false)
                onDismiss()
              }
            )
            SettingsOptionItem(
              title = stringResource(R.string.sleep_timer_end_of_episode),
              isSelected = pauseAfterCurrentEpisode,
              onClick = {
                onPauseAfterCurrentEpisodeChange(true)
                onDismiss()
              }
            )
            listOf(15, 30, 45, 60, 90).forEach { minutes ->
              SettingsOptionItem(
                title = stringResource(R.string.sleep_timer_minutes, minutes),
                isSelected = sleepTimerMinutes == minutes,
                onClick = { onSleepTimerChange(minutes); onDismiss() }
              )
            }
            SettingsCustomSleepTimer(
              onSleepTimerChange = {
                onSleepTimerChange(it)
                onDismiss()
              },
              isSideMenu = false
            )
          }
        }

        "server" -> {
          SettingsSubMenuContainer(
            title = stringResource(R.string.server_label),
            onBack = { onSubMenuChange(null) }
          ) {
            servers.forEach { server ->
              SettingsOptionItem(
                title = server.name,
                isSelected = server == currentServer,
                onClick = { onServerSelected(server); onDismiss() }
              )
            }
          }
        }

        "quality" -> {
          SettingsSubMenuContainer(
            title = stringResource(R.string.quality),
            onBack = { onSubMenuChange(null) }
          ) {
            SettingsOptionItem(
              title = stringResource(R.string.quality_auto),
              isSelected = selectedQualityLabel == "Auto",
              onClick = { onQualitySelected("Auto"); onDismiss() }
            )
            availableQualities.forEach { quality ->
              SettingsOptionItem(
                title = quality,
                isSelected = selectedQualityLabel == quality,
                onClick = { onQualitySelected(quality); onDismiss() }
              )
            }
          }
        }

        "speed" -> {
          SettingsSubMenuContainer(
            title = stringResource(R.string.playback_speed),
            onBack = { onSubMenuChange(null) }
          ) {
            speeds.forEach { speed ->
              SettingsOptionItem(
                title = "${speed}x",
                isSelected = playbackSpeed == speed,
                onClick = { onSpeedSelected(speed); onDismiss() }
              )
            }
          }
        }

        "sync" -> {
          SettingsSubMenuContainer(
            title = stringResource(R.string.sync_mode),
            onBack = { onSubMenuChange(null) }
          ) {
            SettingsOptionItem(
              title = stringResource(R.string.sync_mode_full),
              isSelected = syncMode == 0,
              onClick = { onSyncModeChange(0); onDismiss() }
            )
            SettingsOptionItem(
              title = stringResource(R.string.sync_mode_upload_only),
              isSelected = syncMode == 1,
              onClick = { onSyncModeChange(1); onDismiss() }
            )
            SettingsOptionItem(
              title = stringResource(R.string.sync_mode_none),
              isSelected = syncMode == 2,
              onClick = { onSyncModeChange(2); onDismiss() }
            )
          }
        }

        "doubleTapSkip" -> {
          SettingsSubMenuContainer(
            title = stringResource(R.string.double_tap_skip),
            onBack = { onSubMenuChange(null) }
          ) {
            listOf(5, 10, 15, 20, 30).forEach { seconds ->
              SettingsOptionItem(
                title = stringResource(R.string.seconds_unit, seconds),
                isSelected = doubleTapSkipDuration == seconds,
                onClick = { onDoubleTapSkipDurationChange(seconds); onDismiss() }
              )
            }
          }
        }

        "longPressSpeed" -> {
          SettingsSubMenuContainer(
            title = stringResource(R.string.long_press_speed),
            onBack = { onSubMenuChange(null) }
          ) {
            listOf(1.5f, 2.0f, 2.5f, 3.0f).forEach { speed ->
              SettingsOptionItem(
                title = "${speed}x",
                isSelected = longPressSpeed == speed,
                onClick = { onLongPressSpeedChange(speed); onDismiss() }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SettingsCustomSleepTimer(onSleepTimerChange: (Int) -> Unit, isSideMenu: Boolean) {
  var showInput by remember { mutableStateOf(false) }
  var value by remember { mutableStateOf("") }

  if (!showInput) {
    if (isSideMenu) {
      SideMenuOptionChip(
        text = stringResource(R.string.sleep_timer_custom),
        isSelected = false,
        onClick = { showInput = true }
      )
    } else {
      SettingsOptionItem(
        title = stringResource(R.string.sleep_timer_custom),
        isSelected = false,
        onClick = { showInput = true }
      )
    }
  } else {
    Row(
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = value,
        onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 4) value = it },
        placeholder = { Text(stringResource(R.string.sleep_timer_custom_hint), fontSize = 12.sp) },
        modifier = Modifier.width(if (isSideMenu) 100.dp else 140.dp),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MainColor,
          unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          cursorColor = MainColor
        )
      )
      IconButton(
        onClick = {
          val mins = value.toIntOrNull() ?: 0
          if (mins in 1..1440) {
            onSleepTimerChange(mins)
          }
          showInput = false
        },
        enabled = value.isNotEmpty()
      ) {
        Icon(Icons.Default.Check, null, tint = if (value.isNotEmpty()) MainColor else Color.Gray)
      }
      IconButton(onClick = { showInput = false }) {
        Icon(Icons.Default.Close, null, tint = Color.White.copy(alpha = 0.6f))
      }
    }
  }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, value: String? = null, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
    Spacer(modifier = Modifier.width(16.dp))
    Text(text = title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
    if (value != null) {
      Text(text = value, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        Icons.Default.ChevronRight,
        null,
        tint = Color.White.copy(alpha = 0.3f),
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
fun SettingsToggleItem(
  icon: ImageVector,
  title: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
    Spacer(modifier = Modifier.width(16.dp))
    Text(text = title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = MainColor,
        uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
        uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
      )
    )
  }
}

@Composable
fun SettingsSubMenuContainer(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
  Column {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack) {
        Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          stringResource(R.string.back),
          tint = Color.White
        )
      }
      Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp)
      )
    }
    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) { content() }
  }
}

@Composable
fun SettingsOptionItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      color = if (isSelected) MainColor else Color.White,
      fontSize = 16.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      modifier = Modifier.weight(1f)
    )
    if (isSelected) {
      Icon(Icons.Default.Check, null, tint = MainColor, modifier = Modifier.size(20.dp))
    }
  }
}

@Composable
fun SettingsSideMenuContent(
  servers: List<ServerInfo>,
  currentServer: ServerInfo?,
  onServerSelected: (ServerInfo) -> Unit,
  qualities: List<String>,
  currentQuality: String,
  onQualitySelected: (String) -> Unit,
  speeds: List<Float>,
  currentSpeed: Float,
  onSpeedSelected: (Float) -> Unit,
  volumeGestureEnabled: Boolean,
  onVolumeGestureToggle: (Boolean) -> Unit,
  brightnessGestureEnabled: Boolean,
  onBrightnessGestureToggle: (Boolean) -> Unit,
  autoSkipEnabled: Boolean,
  onAutoSkipToggle: (Boolean) -> Unit,
  doubleTapSkipDuration: Int,
  onDoubleTapSkipDurationChange: (Int) -> Unit,
  longPressSpeed: Float,
  onLongPressSpeedChange: (Float) -> Unit,
  syncMode: Int,
  onSyncModeChange: (Int) -> Unit,
  sleepTimerMinutes: Int,
  onSleepTimerChange: (Int) -> Unit,
  pauseAfterCurrentEpisode: Boolean,
  onPauseAfterCurrentEpisodeChange: (Boolean) -> Unit,
  sleepTimerRemainingSeconds: Long
) {
  val context = LocalContext.current
  val onSupportClick = { subjectType: String ->
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data =
        "mailto:support@animevsub.eu.org?subject=$subjectType%20application%20git.shin.animevsub".toUri()
    }
    try {
      context.startActivity(intent)
    } catch (_: Exception) {
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp)
  ) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      TextButton(
        onClick = { onSupportClick("Report") },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
      ) {
        Icon(Icons.Default.BugReport, null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(stringResource(R.string.report_bug), fontSize = 14.sp)
      }
      TextButton(
        onClick = { onSupportClick("Feedback") },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
      ) {
        Icon(Icons.Default.Feedback, null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.feedback), fontSize = 14.sp)
      }
    }
    Spacer(modifier = Modifier.height(16.dp))
    SideMenuSection(title = stringResource(R.string.general)) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SettingsToggleItem(
          icon = Icons.Default.SkipNext,
          title = stringResource(R.string.auto_skip),
          checked = autoSkipEnabled,
          onCheckedChange = onAutoSkipToggle
        )
        SettingsToggleItem(
          icon = Icons.AutoMirrored.Filled.VolumeUp,
          title = stringResource(R.string.volume_gesture),
          checked = volumeGestureEnabled,
          onCheckedChange = onVolumeGestureToggle
        )
        SettingsToggleItem(
          icon = Icons.Default.BrightnessLow,
          title = stringResource(R.string.brightness_gesture),
          checked = brightnessGestureEnabled,
          onCheckedChange = onBrightnessGestureToggle
        )
      }
    }
    SideMenuSection(title = stringResource(R.string.double_tap_skip)) {
      FlowRow(spacing = 8.dp) {
        listOf(5, 10, 15, 20, 30).forEach { seconds ->
          SideMenuOptionChip(
            text = stringResource(R.string.seconds_unit, seconds),
            isSelected = doubleTapSkipDuration == seconds,
            onClick = { onDoubleTapSkipDurationChange(seconds) }
          )
        }
      }
    }
    SideMenuSection(title = stringResource(R.string.long_press_speed)) {
      FlowRow(spacing = 8.dp) {
        listOf(1.5f, 2.0f, 2.5f, 3.0f).forEach { speed ->
          SideMenuOptionChip(
            text = "${speed}x",
            isSelected = longPressSpeed == speed,
            onClick = { onLongPressSpeedChange(speed) }
          )
        }
      }
    }
    SideMenuSection(title = stringResource(R.string.sync_mode)) {
      FlowRow(spacing = 8.dp) {
        SideMenuOptionChip(
          text = stringResource(R.string.sync_mode_full),
          isSelected = syncMode == 0,
          onClick = { onSyncModeChange(0) }
        )
        SideMenuOptionChip(
          text = stringResource(R.string.sync_mode_upload_only),
          isSelected = syncMode == 1,
          onClick = { onSyncModeChange(1) }
        )
        SideMenuOptionChip(
          text = stringResource(R.string.sync_mode_none),
          isSelected = syncMode == 2,
          onClick = { onSyncModeChange(2) }
        )
      }
    }
    SideMenuSection(title = stringResource(R.string.sleep_timer)) {
      FlowRow(spacing = 8.dp) {
        SideMenuOptionChip(
          text = stringResource(R.string.sleep_timer_off),
          isSelected = sleepTimerMinutes == 0 && !pauseAfterCurrentEpisode,
          onClick = {
            onSleepTimerChange(0)
            onPauseAfterCurrentEpisodeChange(false)
          }
        )
        SideMenuOptionChip(
          text = stringResource(R.string.sleep_timer_end_of_episode),
          isSelected = pauseAfterCurrentEpisode,
          onClick = { onPauseAfterCurrentEpisodeChange(true) }
        )
        listOf(15, 30, 45, 60, 90).forEach { minutes ->
          SideMenuOptionChip(
            text = stringResource(R.string.sleep_timer_minutes, minutes),
            isSelected = sleepTimerMinutes == minutes,
            onClick = { onSleepTimerChange(minutes) }
          )
        }
        SettingsCustomSleepTimer(
          onSleepTimerChange = onSleepTimerChange,
          isSideMenu = true
        )
      }
      if (sleepTimerMinutes > 0 && !pauseAfterCurrentEpisode) {
        val minutes = sleepTimerRemainingSeconds / 60
        val seconds = sleepTimerRemainingSeconds % 60
        Text(
          text = stringResource(R.string.sleep_timer_remaining, String.format("%02d:%02d", minutes, seconds)),
          color = MainColor,
          fontSize = 12.sp,
          modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )
      }
    }
    SideMenuSection(title = stringResource(R.string.server_label)) {
      FlowRow(spacing = 8.dp) {
        servers.forEach { server ->
          SideMenuOptionChip(
            text = server.name,
            isSelected = server == currentServer,
            onClick = { onServerSelected(server) }
          )
        }
      }
    }
    SideMenuSection(title = stringResource(R.string.quality)) {
      FlowRow(spacing = 8.dp) {
        SideMenuOptionChip(
          text = stringResource(R.string.quality_auto),
          isSelected = currentQuality == "Auto",
          onClick = { onQualitySelected("Auto") }
        )
        qualities.forEach { quality ->
          SideMenuOptionChip(
            text = quality,
            isSelected = currentQuality == quality,
            onClick = { onQualitySelected(quality) }
          )
        }
      }
    }
    SideMenuSection(title = stringResource(R.string.playback_speed)) {
      FlowRow(spacing = 8.dp) {
        speeds.forEach { speed ->
          SideMenuOptionChip(
            text = "${speed}x",
            isSelected = currentSpeed == speed,
            onClick = { onSpeedSelected(speed) }
          )
        }
      }
    }
  }
}

@Composable
fun SideMenuSection(title: String, content: @Composable () -> Unit) {
  Column(modifier = Modifier.padding(vertical = 8.dp)) {
    Text(
      text = title,
      color = TextSecondary,
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    content()
  }
}

@Composable
fun SideMenuOptionChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
  TextButton(
    onClick = onClick,
    colors = ButtonDefaults.textButtonColors(contentColor = if (isSelected) MainColor else Color.White),
    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
    modifier = Modifier.height(36.dp)
  ) {
    Text(
      text = text,
      fontSize = 14.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
  }
}

@Composable
fun FlowRow(spacing: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
  androidx.compose.ui.layout.Layout(content = content) { measurables, constraints ->
    val gap = spacing.roundToPx()
    val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
    var rowWidth = 0
    val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
    var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
    placeables.forEach { placeable ->
      if (rowWidth + placeable.width + gap > constraints.maxWidth && currentRow.isNotEmpty()) {
        rows.add(currentRow); rowWidth = 0; currentRow = mutableListOf()
      }
      currentRow.add(placeable); rowWidth += placeable.width + gap
    }
    if (currentRow.isNotEmpty()) rows.add(currentRow)
    val totalHeight = rows.sumOf { row -> row.maxOf { it.height } + gap }
    layout(constraints.maxWidth, totalHeight) {
      var y = 0
      rows.forEach { row ->
        var x = 0
        val maxHeight = row.maxOf { it.height }
        row.forEach { placeable -> placeable.placeRelative(x, y); x += placeable.width + gap }
        y += maxHeight + gap
      }
    }
  }
}
