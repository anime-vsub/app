package git.shin.animevsub.ui.components.player.settings

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import git.shin.animevsub.R
import git.shin.animevsub.data.model.ServerInfo
import java.util.Locale

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
  sleepTimerRemainingSeconds: Long,
  onAiSummary: () -> Unit,
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
            SettingsItem(
              icon = Icons.Default.AutoAwesome,
              title = stringResource(R.string.ai_episode_summary),
              onClick = onAiSummary
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
                  stringResource(R.string.sleep_timer_remaining, String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds))
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
