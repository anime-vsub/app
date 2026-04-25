package git.shin.animevsub.ui.components.player.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import git.shin.animevsub.R
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.ui.theme.MainColor
import java.util.Locale

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
  autoNextEnabled: Boolean,
  onAutoNextToggle: (Boolean) -> Unit,
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
          icon = Icons.Default.SkipNext,
          title = stringResource(R.string.auto_next),
          checked = autoNextEnabled,
          onCheckedChange = onAutoNextToggle
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
          text = stringResource(R.string.sleep_timer_remaining, String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)),
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
