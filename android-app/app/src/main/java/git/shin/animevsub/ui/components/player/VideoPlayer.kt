package git.shin.animevsub.ui.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.*
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.data.model.DisplaySeason
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.ui.styles.SmallTextStyle
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.utils.formatDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt
import android.content.pm.ActivityInfo

@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
  playerData: PlayerData?,
  poster: String?,
  modifier: Modifier = Modifier,
  title: String = "",
  subtitle: String = "",
  isLoading: Boolean = false,
  errorMessage: String? = null,
  introRange: LongRange? = null,
  outroRange: LongRange? = null,
  autoNextEnabled: Boolean = false,
  hasNextEpisode: Boolean = false,
  onBack: () -> Unit = {},
  onReload: () -> Unit = {},
  onSettings: () -> Unit = {},
  onNextEpisode: () -> Unit = {},
  onSelectEpisode: () -> Unit = {},
  onSelectServer: () -> Unit = {},
  onVideoEnded: () -> Unit = {},
  servers: List<ServerInfo> = emptyList(),
  currentServer: ServerInfo? = null,
  onServerSelected: (ServerInfo) -> Unit = {},
  displaySeasons: List<DisplaySeason> = emptyList(),
  activeDisplaySeasonId: String = "",
  onSeasonSelected: (String) -> Unit = {},
  episodes: List<ChapterInfo> = emptyList(),
  currentEpisode: ChapterInfo? = null,
  onEpisodeSelected: (ChapterInfo, String) -> Unit = { _, _ -> }
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val preferencesManager = remember { PreferencesManager(context) }
  val volumeGestureEnabled by preferencesManager.volumeGesture.collectAsState(initial = true)
  val brightnessGestureEnabled by preferencesManager.brightnessGesture.collectAsState(initial = true)
  val autoSkipEnabled by preferencesManager.autoSkip.collectAsState(initial = false)

  var isPlaying by remember { mutableStateOf(true) }
  var isBuffering by remember { mutableStateOf(false) }
  var isFullScreen by remember { mutableStateOf(false) }
  var isFirstFrameRendered by remember(playerData) { mutableStateOf(false) }
  var playbackSpeed by remember { mutableFloatStateOf(1f) }

  var showEpisodeSideMenu by remember { mutableStateOf(false) }
  var showServerSideMenu by remember { mutableStateOf(false) }
  var showSettingsBottomSheet by remember { mutableStateOf(false) }
  var showSettingsSideMenu by remember { mutableStateOf(false) }
  var settingsSubMenu by remember { mutableStateOf<String?>(null) }

  data class QualityInfo(val label: String, val group: Tracks.Group, val trackIndex: Int)
  var availableQualities by remember { mutableStateOf<List<QualityInfo>>(emptyList()) }
  var selectedQualityLabel by remember { mutableStateOf("Auto") }

  var notificationText by remember { mutableStateOf("") }
  var showNotification by remember { mutableStateOf(false) }
  var notificationIcon by remember { mutableStateOf(Icons.Default.SkipNext) }
  var isNotificationClickable by remember { mutableStateOf(false) }
  var isAutoNexting by remember(playerData) { mutableStateOf(false) }

  var showSkipNotification by remember { mutableStateOf(false) }
  var skipNotificationText by remember { mutableStateOf("") }
  var skipTargetTime by remember { mutableLongStateOf(0L) }
  var skipRemainingSeconds by remember { mutableStateOf(0) }

  var gestureIcon by remember { mutableStateOf(Icons.AutoMirrored.Filled.VolumeUp) }
  var gestureText by remember { mutableStateOf("") }
  var showGestureIndicator by remember { mutableStateOf(false) }

  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      playWhenReady = true
      addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
          isBuffering = playbackState == Player.STATE_BUFFERING
          if (playbackState == Player.STATE_ENDED) {
            if (autoNextEnabled && hasNextEpisode) isAutoNexting = true
            onVideoEnded()
          }
        }
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) { isPlaying = playWhenReady }
        override fun onRenderedFirstFrame() { isFirstFrameRendered = true }
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) { playbackSpeed = playbackParameters.speed }
        override fun onTracksChanged(tracks: Tracks) {
          val qualities = mutableListOf<QualityInfo>()
          tracks.groups.forEach { group ->
            if (group.type == C.TRACK_TYPE_VIDEO) {
              for (i in 0 until group.length) {
                val format = group.getTrackFormat(i)
                if (format.height > 0) qualities.add(QualityInfo("${format.height}p", group, i))
              }
            }
          }
          availableQualities = qualities.distinctBy { it.label }.sortedByDescending { it.label.replace("p", "").toIntOrNull() ?: 0 }
          val params = trackSelectionParameters
          val hasOverride = params.overrides.values.any { it.type == C.TRACK_TYPE_VIDEO }
          if (!hasOverride) selectedQualityLabel = "Auto"
          else {
            var foundLabel = "Auto"
            availableQualities.forEach { q ->
              val override = params.overrides[q.group.mediaTrackGroup]
              if (override != null && override.trackIndices.contains(q.trackIndex)) foundLabel = q.label
            }
            selectedQualityLabel = foundLabel
          }
        }
      })
    }
  }

  var currentTime by remember { mutableLongStateOf(0L) }
  var duration by remember { mutableLongStateOf(0L) }
  var bufferedPosition by remember { mutableLongStateOf(0L) }
  var isDragging by remember { mutableStateOf(false) }
  var dragTime by remember { mutableLongStateOf(0L) }
  var isControlsVisible by remember { mutableStateOf(true) }

  var showSpeedMenu by remember { mutableStateOf(false) }
  var showQualityMenu by remember { mutableStateOf(false) }
  val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

  LaunchedEffect(isAutoNexting) {
    if (isAutoNexting) {
      notificationText = context.getString(R.string.auto_next_hint)
      notificationIcon = Icons.Default.SkipNext
      isNotificationClickable = true
      showNotification = true
      delay(3000)
      showNotification = false
    }
  }

  LaunchedEffect(playbackSpeed) {
    if (isFirstFrameRendered) {
      notificationText = context.getString(R.string.playback_speed_changed, if (playbackSpeed % 1.0f == 0.0f) playbackSpeed.toInt() else playbackSpeed)
      notificationIcon = Icons.Default.Speed
      isNotificationClickable = false
      showNotification = true
      delay(2000)
      showNotification = false
    }
  }

  LaunchedEffect(currentTime, introRange, outroRange, autoSkipEnabled) {
    val currentMillis = currentTime
    if (introRange != null && currentMillis in introRange) {
      if (autoSkipEnabled) exoPlayer.seekTo(introRange.last + 1)
      else if (!showSkipNotification) {
        skipNotificationText = context.getString(R.string.skip_intro)
        skipTargetTime = introRange.last + 1
        showSkipNotification = true
      }
      if (showSkipNotification) skipRemainingSeconds = ((introRange.last - currentMillis) / 1000).toInt().coerceAtLeast(0)
    } else if (outroRange != null && currentMillis in outroRange) {
      if (autoSkipEnabled) { if (hasNextEpisode) onNextEpisode() else exoPlayer.pause() }
      else if (!showSkipNotification) {
        skipNotificationText = context.getString(R.string.skip_outro)
        skipTargetTime = outroRange.last + 1
        showSkipNotification = true
      }
      if (showSkipNotification) skipRemainingSeconds = ((outroRange.last - currentMillis) / 1000).toInt().coerceAtLeast(0)
    } else showSkipNotification = false
  }

  LaunchedEffect(exoPlayer) {
    while (true) {
      if (!isDragging) {
        currentTime = exoPlayer.currentPosition
        duration = exoPlayer.duration.coerceAtLeast(0L)
        bufferedPosition = exoPlayer.bufferedPosition
      }
      delay(500)
    }
  }

  LaunchedEffect(isControlsVisible, isDragging, isPlaying, isBuffering, showSpeedMenu, showQualityMenu, showEpisodeSideMenu, showServerSideMenu, showSettingsBottomSheet, showSettingsSideMenu, showSkipNotification) {
    if (isControlsVisible && !isDragging && isPlaying && !isBuffering && !showSpeedMenu && !showQualityMenu && !showEpisodeSideMenu && !showServerSideMenu && !showSettingsBottomSheet && !showSettingsSideMenu && !showSkipNotification) {
      delay(5000)
      isControlsVisible = false
    }
  }

  LaunchedEffect(isFullScreen) {
    val activity = findActivity(context) ?: return@LaunchedEffect
    val window = activity.window
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    if (isFullScreen) {
      activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
      controller.hide(WindowInsetsCompat.Type.systemBars())
      controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
      activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
      controller.show(WindowInsetsCompat.Type.systemBars())
    }
  }

  LaunchedEffect(playerData) {
    if (playerData == null || playerData.link.isEmpty()) return@LaunchedEffect
    val httpDataSourceFactory = DefaultHttpDataSource.Factory().setDefaultRequestProperties(playerData.headers ?: emptyMap())
    val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context, httpDataSourceFactory)
    val mediaItem: MediaItem = if (playerData.isContent && playerData.type.lowercase() == "hls") {
      val tempFile = File(context.cacheDir, "temp_playlist.m3u8")
      tempFile.writeText(playerData.link)
      MediaItem.fromUri(tempFile.toUri())
    } else MediaItem.fromUri(playerData.link.toUri())

    if (playerData.type.lowercase() == "hls") {
      exoPlayer.setMediaSource(HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem))
    } else exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()
  }

  DisposableEffect(Unit) {
    onDispose {
      exoPlayer.release()
      findActivity(context)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
  }

  Box(
    modifier = (if (isFullScreen) Modifier.fillMaxSize() else modifier)
      .background(Color.Black)
      .clipToBounds()
      .pointerInput(volumeGestureEnabled, brightnessGestureEnabled) {
        val activity = findActivity(context)
        detectVerticalDragGestures(
          onDragStart = { showGestureIndicator = true },
          onDragEnd = { showGestureIndicator = false },
          onVerticalDrag = { change, dragAmount ->
            val isLeftSide = change.position.x < size.width / 2
            if (isLeftSide && brightnessGestureEnabled) {
              activity?.let {
                val params = it.window.attributes
                val currentBrightness = if (params.screenBrightness < 0) 0.5f else params.screenBrightness
                val newBrightness = (currentBrightness - dragAmount / size.height).coerceIn(0f, 1f)
                params.screenBrightness = newBrightness
                it.window.attributes = params
                gestureIcon = Icons.Default.BrightnessLow
                gestureText = "${(newBrightness * 100).roundToInt()}%"
              }
            } else if (!isLeftSide && volumeGestureEnabled) {
              val currentVolume = exoPlayer.volume
              val newVolume = (currentVolume - dragAmount / size.height).coerceIn(0f, 1f)
              exoPlayer.volume = newVolume
              gestureIcon = Icons.AutoMirrored.Filled.VolumeUp
              gestureText = "${(newVolume * 100).roundToInt()}%"
            }
          }
        )
      }
      .pointerInput(Unit) {
        detectTapGestures(
          onTap = { isControlsVisible = !isControlsVisible },
          onDoubleTap = { offset ->
            if (offset.x < size.width / 2) exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0))
            else exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration))
          }
        )
      }
  ) {
    AndroidView(factory = { ctx -> PlayerView(ctx).apply { player = exoPlayer; useController = false } }, modifier = Modifier.fillMaxSize())
    if (!isFirstFrameRendered && !poster.isNullOrEmpty()) {
      AsyncImage(model = poster, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }

    Box(modifier = Modifier.fillMaxSize()) {
      AnimatedVisibility(visible = isControlsVisible && errorMessage == null, enter = fadeIn(), exit = fadeOut()) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
      }

      AnimatedVisibility(visible = isControlsVisible && !isDragging, enter = fadeIn() + slideInVertically { -it }, exit = fadeOut() + slideOutVertically { -it }) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = if (isFullScreen) 32.dp else 16.dp).padding(horizontal = 8.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { if (isFullScreen) isFullScreen = false else onBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
          }
          Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
            Text(text = title, color = Color.White, fontSize = if (isFullScreen) 18.sp else 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, style = SmallTextStyle)
            if (subtitle.isNotEmpty()) {
              Text(text = stringResource(id = R.string.ep_label, subtitle), color = Color.White.copy(alpha = 0.7f), fontSize = if (isFullScreen) 16.sp else 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, style = SmallTextStyle)
            }
          }
          IconButton(onClick = onReload) { Icon(Icons.Default.Refresh, null, tint = Color.White) }
          IconButton(onClick = { if (isFullScreen) showSettingsSideMenu = true else showSettingsBottomSheet = true; isControlsVisible = false }) {
            Icon(Icons.Default.Settings, "Settings", tint = Color.White)
          }
        }
      }

      if (errorMessage != null) {
        Column(modifier = Modifier.align(Alignment.Center).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.ErrorOutline, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(48.dp))
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = errorMessage, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.height(16.dp))
          Button(onClick = onReload, colors = ButtonDefaults.buttonColors(containerColor = MainColor)) { Text(stringResource(R.string.retry)) }
        }
      } else {
        val showLoading = (isLoading || isBuffering || playerData == null) && !isDragging
        if (showLoading) {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(42.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { exoPlayer.pause() }, color = Color.White, strokeWidth = 3.dp)
        }
        AnimatedVisibility(visible = isControlsVisible && !isDragging, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.Center)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            IconButton(onClick = { exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0)) }, modifier = Modifier.size(32.dp), enabled = playerData != null) {
              Icon(Icons.Default.Replay10, "Rewind 10s", tint = if (playerData != null) Color.White else Color.White.copy(alpha = 0.5f), modifier = Modifier.fillMaxSize())
            }
            if (!showLoading) {
              IconButton(onClick = { if (isPlaying) exoPlayer.pause() else exoPlayer.play() }, modifier = Modifier.size(48.dp)) {
                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, if (isPlaying) "Pause" else "Play", tint = Color.White, modifier = Modifier.fillMaxSize())
              }
            } else Spacer(modifier = Modifier.size(48.dp))
            IconButton(onClick = { exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration)) }, modifier = Modifier.size(32.dp), enabled = playerData != null) {
              Icon(Icons.Default.Forward10, "Forward 10s", tint = if (playerData != null) Color.White else Color.White.copy(alpha = 0.5f), modifier = Modifier.fillMaxSize())
            }
          }
        }
      }

      if (isDragging) {
        Box(modifier = Modifier.align(Alignment.Center).background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(6.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
          Text(text = formatDuration(dragTime), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
      }

      if (showGestureIndicator) GestureIndicator(icon = gestureIcon, text = gestureText, modifier = Modifier.align(Alignment.Center))

      if (showSkipNotification) {
        SkipNotification(text = skipNotificationText, secondsRemaining = skipRemainingSeconds, onSkip = { exoPlayer.seekTo(skipTargetTime); showSkipNotification = false }, onClose = { showSkipNotification = false }, modifier = Modifier.align(Alignment.BottomEnd).padding(end = if (isFullScreen) 32.dp else 16.dp, bottom = if (isFullScreen) 100.dp else 70.dp))
      }

      AnimatedVisibility(visible = showNotification, enter = fadeIn() + slideInVertically { it }, exit = fadeOut() + slideOutVertically { it }, modifier = Modifier.align(Alignment.BottomStart).padding(start = if (isFullScreen) 32.dp else 16.dp, bottom = if (isFullScreen) 100.dp else 70.dp)) {
        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.8f)).border(1.dp, MainColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp)).clickable(enabled = isNotificationClickable) { showNotification = false; isNotificationClickable = false; isAutoNexting = false; onNextEpisode() }.padding(horizontal = 12.dp, vertical = 8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(notificationIcon, null, tint = MainColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = notificationText, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
          }
        }
      }

      AnimatedVisibility(visible = isControlsVisible && errorMessage == null, enter = fadeIn() + slideInVertically { it }, exit = fadeOut() + slideOutVertically { it }, modifier = Modifier.align(Alignment.BottomCenter)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = if (isFullScreen) 32.dp else 12.dp, vertical = if (isFullScreen) 16.dp else 2.dp)) {
          if (!isDragging) {
            Row(modifier = Modifier.fillMaxWidth().height(28.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text(text = "${formatDuration(currentTime)} / ${formatDuration(duration)}", color = Color.White, fontSize = if (isFullScreen) 16.sp else 13.sp)
              IconButton(onClick = { isFullScreen = !isFullScreen }, modifier = Modifier.size(28.dp)) {
                Icon(if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen, "Toggle Full Screen", tint = Color.White, modifier = Modifier.fillMaxSize())
              }
            }
          }
          Slider(
            value = (if (isDragging) dragTime else currentTime).toFloat(),
            onValueChange = { isDragging = true; dragTime = it.toLong() },
            onValueChangeFinished = { exoPlayer.seekTo(dragTime); isDragging = false },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(activeTrackColor = MainColor, inactiveTrackColor = Color.White.copy(alpha = 0.2f), thumbColor = Color.Transparent),
            thumb = { Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) { Box(modifier = Modifier.size(12.dp).background(MainColor, CircleShape).border(1.dp, Color.White, CircleShape)) } },
            track = { sliderState ->
              Box(modifier = Modifier.fillMaxWidth().height(24.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxWidth().height(3.dp)) {
                  val trackWidth = size.width; val trackH = size.height; val radius = trackH / 2
                  drawRoundRect(color = Color.White.copy(alpha = 0.2f), cornerRadius = CornerRadius(radius, radius))
                  if (duration > 0) {
                    val bufferedEnd = (bufferedPosition.toFloat() / duration) * trackWidth
                    drawRoundRect(color = Color.White.copy(alpha = 0.4f), size = Size(bufferedEnd, trackH), cornerRadius = CornerRadius(radius, radius))
                  }
                  drawRoundRect(color = MainColor, size = Size((sliderState.value / sliderState.valueRange.endInclusive) * trackWidth, trackH), cornerRadius = CornerRadius(radius, radius))
                  introRange?.let { range -> if (duration > 0) { val start = (range.first.toFloat() / duration).coerceIn(0f, 1f) * trackWidth; val end = (range.last.toFloat() / duration).coerceIn(0f, 1f) * trackWidth; drawRect(color = Color(0xFF2196F3), topLeft = Offset(start, 0f), size = Size((end - start).coerceAtLeast(0f), trackH)) } }
                  outroRange?.let { range -> if (duration > 0) { val start = (range.first.toFloat() / duration).coerceIn(0f, 1f) * trackWidth; val end = (range.last.toFloat() / duration).coerceIn(0f, 1f) * trackWidth; drawRect(color = Color(0xFF2196F3), topLeft = Offset(start, 0f), size = Size((end - start).coerceAtLeast(0f), trackH)) } }
                }
              }
            },
            modifier = Modifier.height(24.dp)
          )

          if (isFullScreen) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PlayerControlSmallButton(icon = Icons.Default.SkipNext, text = stringResource(R.string.next_ep), onClick = onNextEpisode)
                PlayerControlSmallButton(icon = Icons.AutoMirrored.Filled.PlaylistPlay, onClick = { showEpisodeSideMenu = true; isControlsVisible = false; onSelectEpisode() })
              }
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PlayerControlSmallButton(icon = Icons.Default.Dns, onClick = { showServerSideMenu = true; isControlsVisible = false; onSelectServer() })
                Box {
                  PlayerControlSmallButton(icon = Icons.Default.HighQuality, text = selectedQualityLabel, onClick = { showQualityMenu = true })
                  DropdownMenu(expanded = showQualityMenu, onDismissRequest = { showQualityMenu = false }, modifier = Modifier.background(Color(0xFF2B2B2B))) {
                    DropdownMenuItem(text = { Text("Auto", color = if (selectedQualityLabel == "Auto") MainColor else Color.White) }, onClick = { exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon().clearOverridesOfType(C.TRACK_TYPE_VIDEO).build(); showQualityMenu = false })
                    availableQualities.forEach { quality -> DropdownMenuItem(text = { Text(quality.label, color = if (selectedQualityLabel == quality.label) MainColor else Color.White) }, onClick = { exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon().setOverrideForType(TrackSelectionOverride(quality.group.mediaTrackGroup, quality.trackIndex)).build(); selectedQualityLabel = quality.label; showQualityMenu = false }) }
                  }
                }
                Box {
                  PlayerControlSmallButton(icon = Icons.Default.SlowMotionVideo, text = "${if (playbackSpeed % 1.0f == 0.0f) playbackSpeed.toInt() else playbackSpeed}x", onClick = { showSpeedMenu = true })
                  DropdownMenu(expanded = showSpeedMenu, onDismissRequest = { showSpeedMenu = false }, modifier = Modifier.background(Color(0xFF2B2B2B))) {
                    speeds.forEach { speed -> DropdownMenuItem(text = { Text("${speed}x", color = if (playbackSpeed == speed) MainColor else Color.White) }, onClick = { exoPlayer.setPlaybackSpeed(speed); showSpeedMenu = false }) }
                  }
                }
              }
            }
          }
        }
      }

      PlayerSideMenu(visible = showEpisodeSideMenu, onDismiss = { showEpisodeSideMenu = false }) {
        EpisodeSelectorContent(displaySeasons = displaySeasons, activeDisplaySeasonId = activeDisplaySeasonId, episodes = episodes, currentEpisodeId = currentEpisode?.id, onSeasonClick = onSeasonSelected, onChapterClick = { chap, seasonId -> onEpisodeSelected(chap, seasonId); showEpisodeSideMenu = false }, isSideMenu = true, onClose = { showEpisodeSideMenu = false })
      }
      PlayerSideMenu(visible = showServerSideMenu, onDismiss = { showServerSideMenu = false }, title = stringResource(R.string.server_label)) {
        ServerSelectorContent(servers = servers, currentServer = currentServer, onServerClick = { onServerSelected(it); showServerSideMenu = false })
      }
      PlayerSideMenu(visible = showSettingsSideMenu, onDismiss = { showSettingsSideMenu = false }, title = stringResource(R.string.settings)) {
        SettingsSideMenuContent(servers = servers, currentServer = currentServer, onServerSelected = { onServerSelected(it); showSettingsSideMenu = false }, qualities = availableQualities.map { it.label }, currentQuality = selectedQualityLabel, onQualitySelected = { label -> if (label == "Auto") exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon().clearOverridesOfType(C.TRACK_TYPE_VIDEO).build() else availableQualities.find { it.label == label }?.let { q -> exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon().setOverrideForType(TrackSelectionOverride(q.group.mediaTrackGroup, q.trackIndex)).build() }; showSettingsSideMenu = false }, speeds = speeds, currentSpeed = playbackSpeed, onSpeedSelected = { exoPlayer.setPlaybackSpeed(it); showSettingsSideMenu = false }, volumeGestureEnabled = volumeGestureEnabled, onVolumeGestureToggle = { scope.launch { preferencesManager.setVolumeGesture(it) } }, brightnessGestureEnabled = brightnessGestureEnabled, onBrightnessGestureToggle = { scope.launch { preferencesManager.setBrightnessGesture(it) } }, autoSkipEnabled = autoSkipEnabled, onAutoSkipToggle = { scope.launch { preferencesManager.setAutoSkip(it) } })
      }
      if (showSettingsBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showSettingsBottomSheet = false; settingsSubMenu = null }, sheetState = rememberModalBottomSheetState(), containerColor = DarkSurface, contentColor = Color.White, dragHandle = null) {
          SettingsBottomSheetContent(settingsSubMenu = settingsSubMenu, onSubMenuChange = { settingsSubMenu = it }, servers = servers, currentServer = currentServer, onServerSelected = onServerSelected, availableQualities = availableQualities.map { it.label }, selectedQualityLabel = selectedQualityLabel, onQualitySelected = { label -> if (label == "Auto") exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon().clearOverridesOfType(C.TRACK_TYPE_VIDEO).build() else availableQualities.find { it.label == label }?.let { q -> exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon().setOverrideForType(TrackSelectionOverride(q.group.mediaTrackGroup, q.trackIndex)).build() } }, speeds = speeds, playbackSpeed = playbackSpeed, onSpeedSelected = { exoPlayer.setPlaybackSpeed(it) }, volumeGestureEnabled = volumeGestureEnabled, onVolumeGestureToggle = { scope.launch { preferencesManager.setVolumeGesture(it) } }, brightnessGestureEnabled = brightnessGestureEnabled, onBrightnessGestureToggle = { scope.launch { preferencesManager.setBrightnessGesture(it) } }, autoSkipEnabled = autoSkipEnabled, onAutoSkipToggle = { scope.launch { preferencesManager.setAutoSkip(it) } }, onDismiss = { showSettingsBottomSheet = false; settingsSubMenu = null })
        }
      }
    }
  }
}
