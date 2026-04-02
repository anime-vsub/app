package git.shin.animevsub.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import kotlinx.coroutines.launch

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
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val onSupportClick = { subjectType: String ->
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:support@animevsub.eu.org?subject=$subjectType%20application%20git.shin.animevsub".toUri()
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
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

                "server" -> {
                    SettingsSubMenuContainer(
                        title = stringResource(R.string.server_label),
                        onBack = { onSubMenuChange(null) }
                    ) {
                        servers.forEach { server ->
                            SettingsOptionItem(
                                title = server.name,
                                isSelected = server == currentServer,
                                onClick = {
                                    onServerSelected(server)
                                    onDismiss()
                                }
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
                            title = "Auto",
                            isSelected = selectedQualityLabel == "Auto",
                            onClick = {
                                onQualitySelected("Auto")
                                onDismiss()
                            }
                        )
                        availableQualities.forEach { quality ->
                            SettingsOptionItem(
                                title = quality,
                                isSelected = selectedQualityLabel == quality,
                                onClick = {
                                    onQualitySelected(quality)
                                    onDismiss()
                                }
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
                                onClick = {
                                    onSpeedSelected(speed)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
  icon: ImageVector,
  title: String,
  value: String? = null,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
    Spacer(modifier = Modifier.width(16.dp))
    Text(text = title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
    if (value != null) {
      Text(text = value, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        Icons.Default.ChevronRight,
        contentDescription = null,
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
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
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
fun SettingsSubMenuContainer(
  title: String,
  onBack: () -> Unit,
  content: @Composable () -> Unit
) {
  Column {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
      content()
    }
  }
}

@Composable
fun SettingsOptionItem(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
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
      Icon(
        Icons.Default.Check,
        contentDescription = null,
        tint = MainColor,
        modifier = Modifier.size(20.dp)
      )
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
  onAutoSkipToggle: (Boolean) -> Unit
) {
  val context = LocalContext.current
  val onSupportClick = { subjectType: String ->
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data = "mailto:support@animevsub.eu.org?subject=$subjectType%20application%20git.shin.animevsub".toUri()
    }
    try {
      context.startActivity(intent)
    } catch (_: Exception) {}
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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

    // Toggles
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

    // Servers
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

    // Quality
    SideMenuSection(title = stringResource(R.string.quality)) {
      FlowRow(spacing = 8.dp) {
        SideMenuOptionChip(
          text = "Auto",
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

    // Speed
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
fun SideMenuOptionChip(
  text: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  TextButton(
    onClick = onClick,
    colors = ButtonDefaults.textButtonColors(
      contentColor = if (isSelected) MainColor else TextPrimary
    ),
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
fun FlowRow(
  spacing: androidx.compose.ui.unit.Dp,
  content: @Composable () -> Unit
) {
  androidx.compose.ui.layout.Layout(content = content) { measurables, constraints ->
    val gap = spacing.roundToPx()
    val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }

    var rowWidth = 0
    val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
    var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()

    placeables.forEach { placeable ->
      if (rowWidth + placeable.width + gap > constraints.maxWidth && currentRow.isNotEmpty()) {
        rows.add(currentRow)
        rowWidth = 0
        currentRow = mutableListOf()
      }
      currentRow.add(placeable)
      rowWidth += placeable.width + gap
    }
    if (currentRow.isNotEmpty()) rows.add(currentRow)

    val totalHeight = rows.sumOf { row -> row.maxOf { it.height } + gap }
    layout(constraints.maxWidth, totalHeight) {
      var y = 0
      rows.forEach { row ->
        var x = 0
        val maxHeight = row.maxOf { it.height }
        row.forEach { placeable ->
          placeable.placeRelative(x, y)
          x += placeable.width + gap
        }
        y += maxHeight + gap
      }
    }
  }
}

@Composable
fun PlayerSideMenu(
  visible: Boolean,
  onDismiss: () -> Unit,
  title: String? = null,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it }),
    modifier = Modifier.fillMaxSize()
  ) {
    Row(modifier = Modifier.fillMaxSize()) {
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
          .background(Color.Black.copy(alpha = 0.3f))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismiss
          )
      )
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(if (findActivity(context)?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) 0.4f else 0.5f)
          .background(DarkSurface)
          .clickable(enabled = false) {}
          .padding(bottom = 16.dp)
      ) {
        if (title != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = title,
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
          }
        }
        Box(modifier = Modifier.weight(1f)) {
          content()
        }
      }
    }
  }
}

@Composable
fun PlayerControlSmallButton(
  icon: ImageVector,
  text: String? = null,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = Color.White,
      modifier = Modifier.size(20.dp)
    )
    if (text != null) {
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = text,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun SkipNotification(
    text: String,
    secondsRemaining: Int,
    onSkip: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.85f))
            .border(1.dp, MainColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.clickable(onClick = onSkip)
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.seconds_remaining, secondsRemaining),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GestureIndicator(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SeekIndicator(
    isForward: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isForward) {
        scope.launch {
            alpha.snapTo(0f)
            scale.snapTo(0.8f)
            alpha.animateTo(1f, tween(200))
            alpha.animateTo(0f, tween(600, delayMillis = 200))
        }
        scope.launch {
            scale.animateTo(1.2f, tween(800, easing = FastOutSlowInEasing))
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(0.4f)
            .alpha(alpha.value)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isForward) {
                        listOf(Color.Transparent, Color.White.copy(alpha = 0.2f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                    }
                ),
                shape = if (isForward) {
                    RoundedCornerShape(topStartPercent = 100, bottomStartPercent = 100)
                } else {
                    RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            Icon(
                imageVector = if (isForward) Icons.Default.FastForward else Icons.Default.FastRewind,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isForward) "+10s" else "-10s",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun findActivity(context: Context): Activity? = when (context) {
  is Activity -> context
  is ContextWrapper -> findActivity(context.baseContext)
  else -> null
}
