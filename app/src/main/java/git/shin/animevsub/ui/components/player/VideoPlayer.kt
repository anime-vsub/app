package git.shin.animevsub.ui.components.player

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
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
import git.shin.animevsub.data.model.DoubleRange
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.data.model.WatchProgress
import git.shin.animevsub.ui.components.player.settings.SettingsBottomSheetContent
import git.shin.animevsub.ui.components.player.settings.SettingsSideMenuContent
import git.shin.animevsub.ui.styles.SmallTextStyle
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.utils.formatDuration
import git.shin.animevsub.ui.utils.rememberScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

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
  introRange: DoubleRange? = null,
  outroRange: DoubleRange? = null,
  autoNextEnabled: Boolean = false,
  onBack: () -> Unit,
  onReload: () -> Unit,
  onNextEpisode: () -> Unit,
  onVideoEnded: () -> Boolean,
  servers: List<ServerInfo> = emptyList(),
  currentServer: ServerInfo? = null,
  onServerSelected: (ServerInfo) -> Unit,
  displaySeasons: List<DisplaySeason> = emptyList(),
  activeDisplaySeasonId: String = "",
  onSeasonSelected: (String) -> Unit,
  episodes: List<ChapterInfo> = emptyList(),
  currentEpisode: ChapterInfo? = null,
  onEpisodeSelected: (ChapterInfo, String) -> Unit,
  initialPosition: Long = 0L,
  onProgressUpdate: (Long, Long) -> Unit,
  chapterProgress: Map<String, WatchProgress>,
  isFullScreen: Boolean = false,
  onFullScreenChange: (Boolean) -> Unit,
  isInPipMode: Boolean = false,
  onPlayingStateChange: (Boolean) -> Unit,
  syncMode: Int = 0,
  onSyncModeChange: (Int) -> Unit,
  sleepTimerMinutes: Int,
  onSleepTimerChange: (Int) -> Unit,
  pauseAfterCurrentEpisode: Boolean,
  onPauseAfterCurrentEpisodeChange: (Boolean) -> Unit,
  sleepTimerRemainingSeconds: Long,
  isEpisodesLoading: Boolean,
  onExoPlayerCreated: (ExoPlayer) -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val screenState = rememberScreenState()
  val isTV = screenState.isTV

  val focusRequester = remember(isTV) { if (isTV) FocusRequester() else null }

  val preferencesManager = remember { PreferencesManager(context) }
  val volumeGestureEnabled by preferencesManager.volumeGesture.collectAsState(initial = true)
  val brightnessGestureEnabled by preferencesManager.brightnessGesture.collectAsState(initial = true)
  val autoSkipEnabled by preferencesManager.autoSkip.collectAsState(initial = false)
  val doubleTapSkipDuration by preferencesManager.doubleTapSkip.collectAsState(initial = 10)
  val longPressSpeedValue by preferencesManager.longPressSpeed.collectAsState(initial = 2.0f)

  var isPlaying by remember { mutableStateOf(true) }
  var isBuffering by remember { mutableStateOf(false) }
//  var isFirstFrameRendered by remember(playerData) { mutableStateOf(false) }
  var playbackSpeed by remember { mutableFloatStateOf(1f) }
  var originalSpeedBeforeLongPress by remember { mutableFloatStateOf(1f) }
  var isLongPressing by remember { mutableStateOf(false) }

  var videoZoomScale by remember { mutableFloatStateOf(1f) }
  var videoOffset by remember { mutableStateOf(Offset.Zero) }
  var isInteractingWithZoom by remember { mutableStateOf(false) }
  var containerSize by remember { mutableStateOf<IntSize>(IntSize.Zero) }
  var snapJob by remember { mutableStateOf<Job?>(null) }

  var showEpisodeSideMenu by remember { mutableStateOf(false) }
  var showServerSideMenu by remember { mutableStateOf(false) }
  var showSettingsBottomSheet by remember { mutableStateOf(false) }
  var showSettingsSideMenu by remember { mutableStateOf(false) }
  var settingsSubMenu by remember { mutableStateOf<String?>(null) }

  var showSpeedMenu by remember { mutableStateOf(false) }
  var showQualityMenu by remember { mutableStateOf(false) }

  val anyMenuVisible = showEpisodeSideMenu || showServerSideMenu || showSettingsSideMenu || showSettingsBottomSheet || showSpeedMenu || showQualityMenu

  data class QualityInfo(val label: String, val group: Tracks.Group, val trackIndex: Int)

  var availableQualities by remember { mutableStateOf<List<QualityInfo>>(emptyList()) }
  var selectedQualityLabel by remember { mutableStateOf("Auto") }

  var notificationText by remember { mutableStateOf("") }
  val view = LocalView.current

  DisposableEffect(isPlaying) {
    if (isPlaying) {
      view.keepScreenOn = true
    }

    onDispose {
      view.keepScreenOn = false
    }
  }

  var showNotification by remember { mutableStateOf(false) }
  var notificationIcon by remember { mutableStateOf(Icons.Default.SkipNext) }
  var isNotificationClickable by remember { mutableStateOf(false) }
  var isAutoNexting by remember(playerData) { mutableStateOf(false) }

  var showSkipNotification by remember { mutableStateOf(false) }
  var skipNotificationText by remember { mutableStateOf("") }
  var skipTargetTime by remember { mutableDoubleStateOf(0.0) }
  var skipRemainingSeconds by remember { mutableIntStateOf(0) }

  var gestureIcon by remember { mutableStateOf(Icons.AutoMirrored.Filled.VolumeUp) }
  var gestureText by remember { mutableStateOf("") }
  var showGestureIndicator by remember { mutableStateOf(false) }

  var showDoubleTapIndicator by remember { mutableStateOf(false) }
  var doubleTapSide by remember { mutableStateOf("right") }
  var doubleTapText by remember { mutableStateOf("") }

  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      playWhenReady = true
      onExoPlayerCreated(this)
      addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
          isBuffering = playbackState == Player.STATE_BUFFERING
          if (playbackState == Player.STATE_ENDED) {
            val hasNext = onVideoEnded()
            if (autoNextEnabled && hasNext) {
              onNextEpisode()
              isAutoNexting = true
            }
          }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
          isPlaying = playWhenReady
          onPlayingStateChange(playWhenReady)
        }

//        override fun onRenderedFirstFrame() {
//          isFirstFrameRendered = true
//        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
          playbackSpeed = playbackParameters.speed
        }

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
          availableQualities = qualities.distinctBy { it.label }
            .sortedByDescending { it.label.replace("p", "").toIntOrNull() ?: 0 }
          val params = trackSelectionParameters
          val hasOverride = params.overrides.values.any { it.type == C.TRACK_TYPE_VIDEO }
          if (!hasOverride) {
            selectedQualityLabel = "Auto"
          } else {
            var foundLabel = "Auto"
            availableQualities.forEach { q ->
              val override = params.overrides[q.group.mediaTrackGroup]
              if (override != null && override.trackIndices.contains(q.trackIndex)) {
                foundLabel =
                  q.label
              }
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
  val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

  LaunchedEffect(isAutoNexting) {
    if (isAutoNexting) {
      notificationText = context.getString(R.string.auto_next_hint)
      notificationIcon = Icons.Default.SkipNext
      isNotificationClickable = true
      showNotification = true
      delay(3000)
      if (isAutoNexting) {
        isAutoNexting = false
      }
      showNotification = false
    }
  }
//
//  LaunchedEffect(playbackSpeed) {
//    if (isFirstFrameRendered) {
//      notificationText = context.getString(
//        R.string.playback_speed_changed,
//        if (playbackSpeed % 1.0f == 0.0f) playbackSpeed.toInt() else playbackSpeed
//      )
//      notificationIcon = Icons.Default.Speed
//      isNotificationClickable = false
//      showNotification = true
//      delay(2000)
//      showNotification = false
//    }
//  }

  LaunchedEffect(currentTime, introRange, outroRange, autoSkipEnabled) {
    val currentSeconds = currentTime / 1000.0
    if (introRange != null && currentSeconds in introRange) {
      if (autoSkipEnabled) {
        exoPlayer.seekTo((introRange.last * 1000).toLong())
      } else if (!showSkipNotification) {
        skipNotificationText = context.getString(R.string.skip_intro)
        skipTargetTime = introRange.last * 1000
        showSkipNotification = true
      }
      if (showSkipNotification) {
        skipRemainingSeconds =
          (introRange.last - currentSeconds).toInt().coerceAtLeast(0)
      }
    } else if (outroRange != null && currentSeconds in outroRange) {
      if (autoSkipEnabled) {
        exoPlayer.seekTo((outroRange.last * 1000).toLong())
      } else if (!showSkipNotification) {
        skipNotificationText = context.getString(R.string.skip_outro)
        skipTargetTime = outroRange.last * 1000
        showSkipNotification = true
      }
      if (showSkipNotification) {
        skipRemainingSeconds =
          (outroRange.last - currentSeconds).toInt().coerceAtLeast(0)
      }
    } else {
      showSkipNotification = false
    }
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

  LaunchedEffect(
    isControlsVisible,
    isDragging,
    isPlaying,
    isBuffering,
    showSpeedMenu,
    showQualityMenu,
    showEpisodeSideMenu,
    showServerSideMenu,
    showSettingsBottomSheet,
    showSettingsSideMenu,
    showSkipNotification
  ) {
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
      controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
      activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
      controller.show(WindowInsetsCompat.Type.systemBars())
    }
  }

  // Track the current URI to avoid reloading the same content
  var loadedUri by remember { mutableStateOf<android.net.Uri?>(null) }
  var loadedEpisodeId by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(playerData, currentEpisode?.id) {
    if (playerData == null || playerData.link.isEmpty()) {
      exoPlayer.stop()
      exoPlayer.clearMediaItems()
      loadedUri = null
      loadedEpisodeId = null
      return@LaunchedEffect
    }
    val httpDataSourceFactory =
      DefaultHttpDataSource.Factory().setDefaultRequestProperties(playerData.headers ?: emptyMap())
    val dataSourceFactory: DataSource.Factory =
      DefaultDataSource.Factory(context, httpDataSourceFactory)

    val newUri = if (playerData.isContent && playerData.type.lowercase() == "hls") {
      val tempFile = File(context.cacheDir, "temp_playlist.m3u8")
      tempFile.writeText(playerData.link)
      tempFile.toUri()
    } else {
      playerData.link.toUri()
    }

    if (loadedUri != newUri || loadedEpisodeId != currentEpisode?.id || playerData.isContent) {
      if (playerData.type.lowercase() == "hls") {
        exoPlayer.setMediaSource(
          HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(newUri))
        )
      } else {
        exoPlayer.setMediaItem(MediaItem.fromUri(newUri))
      }

      // Only seek to initialPosition on the VERY FIRST load of this URI, and if syncMode allows restoring
      if (initialPosition > 0 && syncMode == 0) {
        exoPlayer.seekTo(initialPosition)
        val minutes = (initialPosition / 1000 / 60).toInt()
        val seconds = ((initialPosition / 1000) % 60).toInt()
        notificationText = context.getString(R.string.restored_progress, minutes, seconds)
        notificationIcon = Icons.Default.History
        isNotificationClickable = false
        showNotification = true
        scope.launch {
          delay(3000)
          showNotification = false
        }
      }
      exoPlayer.prepare()
      exoPlayer.play()
      loadedUri = newUri
      loadedEpisodeId = currentEpisode?.id
    }
  }

  LaunchedEffect(currentTime, duration) {
    if (duration > 0 && currentTime > 0 && syncMode != 2) {
      onProgressUpdate(currentTime, duration)
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      exoPlayer.release()
      findActivity(context)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
  }

  LaunchedEffect(isTV || !isControlsVisible) {
    focusRequester?.requestFocus()
  }

  Box(
    modifier = (if (isFullScreen) Modifier.fillMaxSize() else modifier)
      .background(Color.Black)
      .onGloballyPositioned { containerSize = it.size }
      .clipToBounds()
      .then(
        if (focusRequester != null) {
          Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
              if (keyEvent.type == KeyEventType.KeyDown) {
                when (keyEvent.nativeKeyEvent.keyCode) {
                  KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                    isControlsVisible = !isControlsVisible
                    true
                  }

                  KeyEvent.KEYCODE_DPAD_LEFT -> {
                    val newPos = (exoPlayer.currentPosition - 10000).coerceAtLeast(0)
                    exoPlayer.seekTo(newPos)
                    if (!isControlsVisible) {
                      doubleTapSide = "left"
                      doubleTapText = "-10s"
                      showDoubleTapIndicator = true
                      scope.launch { delay(800); showDoubleTapIndicator = false }
                    }
                    true
                  }

                  KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    val newPos = (exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration)
                    exoPlayer.seekTo(newPos)
                    if (!isControlsVisible) {
                      doubleTapSide = "right"
                      doubleTapText = "+10s"
                      showDoubleTapIndicator = true
                      scope.launch { delay(800); showDoubleTapIndicator = false }
                    }
                    true
                  }

                  KeyEvent.KEYCODE_BACK -> {
                    if (isControlsVisible) {
                      isControlsVisible = false
                      true
                    } else {
                      false
                    }
                  }

                  else -> false
                }
              } else {
                false
              }
            }
        } else {
          Modifier
        }
      )
      .pointerInput(isFullScreen, anyMenuVisible) {
        if (!isFullScreen) return@pointerInput
        awaitEachGesture {
          if (anyMenuVisible) {
            awaitFirstDown(requireUnconsumed = false)
            return@awaitEachGesture
          }
          var pastTouchSlop = false
          val touchSlop = viewConfiguration.touchSlop

          val down = awaitFirstDown(requireUnconsumed = false)
          snapJob?.cancel()
          // We don't set isInteractingWithZoom = true here to avoid hiding UI on simple tap
          try {
            do {
              val event = awaitPointerEvent()
              val canceled = event.changes.any { it.isConsumed }
              if (canceled) break

              if (event.changes.size > 1) {
                val zoomChange = event.calculateZoom()
                val panChange = event.calculatePan()

                if (!pastTouchSlop) {
                  val centroidSize = event.calculateCentroidSize(useCurrent = false)
                  val zoomMotion = abs(1 - zoomChange) * centroidSize
                  if (zoomMotion > touchSlop || panChange != Offset.Zero) {
                    pastTouchSlop = true
                    isInteractingWithZoom = true
                  }
                }

                if (pastTouchSlop) {
                  videoZoomScale = (videoZoomScale * zoomChange).coerceIn(0.75f, 3.0f)

                  if (videoZoomScale > 1f) {
                    val newOffset = videoOffset + panChange
                    var maxX = (containerSize.width * (videoZoomScale - 1f)) / 2f
                    var maxY = (containerSize.height * (videoZoomScale - 1f)) / 2f

                    val videoSize = exoPlayer.videoSize
                    if (videoSize.width > 0 && videoSize.height > 0) {
                      val videoAspect = videoSize.width.toFloat() / videoSize.height.toFloat()
                      val screenAspect = containerSize.width.toFloat() / containerSize.height.toFloat()
                      if (videoAspect > screenAspect) {
                        val actualVideoHeight = containerSize.width / videoAspect
                        if (actualVideoHeight * videoZoomScale <= containerSize.height) maxY = 0f
                      } else {
                        val actualVideoWidth = containerSize.height * videoAspect
                        if (actualVideoWidth * videoZoomScale <= containerSize.width) maxX = 0f
                      }
                    }

                    videoOffset = Offset(
                      newOffset.x.coerceIn(-maxX.coerceAtLeast(0f), maxX.coerceAtLeast(0f)),
                      newOffset.y.coerceIn(-maxY.coerceAtLeast(0f), maxY.coerceAtLeast(0f))
                    )
                  } else {
                    videoOffset = Offset.Zero
                  }
                  event.changes.forEach { it.consume() }
                }
              } else if (videoZoomScale > 1f) {
                val change = event.changes.first()
                val panChange = change.position - change.previousPosition
                val maxEdgeSize = 30.dp.toPx()
                val edgeSize = (size.width * 0.2f).coerceAtMost(maxEdgeSize)
                val isEdgeTouch = down.position.x < edgeSize || down.position.x > size.width - edgeSize

                if (!pastTouchSlop) {
                  if (isEdgeTouch && abs(panChange.y) > abs(panChange.x) * 1.5f) {
                    break
                  }
                  if (panChange.getDistance() > touchSlop) {
                    pastTouchSlop = true
                    isInteractingWithZoom = true
                  }
                }

                if (pastTouchSlop && panChange != Offset.Zero) {
                  val newOffset = videoOffset + panChange
                  var maxX = (containerSize.width * (videoZoomScale - 1f)) / 2f
                  var maxY = (containerSize.height * (videoZoomScale - 1f)) / 2f

                  val videoSize = exoPlayer.videoSize
                  if (videoSize.width > 0 && videoSize.height > 0) {
                    val videoAspect = videoSize.width.toFloat() / videoSize.height.toFloat()
                    val screenAspect = containerSize.width.toFloat() / containerSize.height.toFloat()
                    if (videoAspect > screenAspect) {
                      val actualVideoHeight = containerSize.width / videoAspect
                      if (actualVideoHeight * videoZoomScale <= containerSize.height) maxY = 0f
                    } else {
                      val actualVideoWidth = containerSize.height * videoAspect
                      if (actualVideoWidth * videoZoomScale <= containerSize.width) maxX = 0f
                    }
                  }

                  videoOffset = Offset(
                    newOffset.x.coerceIn(-maxX.coerceAtLeast(0f), maxX.coerceAtLeast(0f)),
                    newOffset.y.coerceIn(-maxY.coerceAtLeast(0f), maxY.coerceAtLeast(0f))
                  )
                  change.consume()
                }
              }
            } while (event.changes.any { it.pressed })

            // Snap logic on release with animation
            val videoSize = exoPlayer.videoSize
            if (videoSize.width > 0 && videoSize.height > 0) {
              val videoAspect = videoSize.width.toFloat() / videoSize.height.toFloat()
              val screenAspect = if (containerSize.width > 0) containerSize.width.toFloat() / containerSize.height.toFloat() else 1.777f
              val fitScale = if (videoAspect > screenAspect) {
                containerSize.height / (containerSize.width / videoAspect)
              } else {
                containerSize.width / (containerSize.height * videoAspect)
              }

              var targetScale = videoZoomScale
              var targetOffset = videoOffset

              val distToFit = abs(videoZoomScale - fitScale)
              val distToOriginal = abs(videoZoomScale - 1f)

              if (distToFit < 0.15f && (distToFit < distToOriginal || videoZoomScale > 1.03f)) {
                targetScale = fitScale
                val maxX = (containerSize.width * (targetScale - 1f)) / 2f
                val maxY = (containerSize.height * (targetScale - 1f)) / 2f
                targetOffset = Offset(
                  videoOffset.x.coerceIn(-maxX.coerceAtLeast(0f), maxX.coerceAtLeast(0f)),
                  videoOffset.y.coerceIn(-maxY.coerceAtLeast(0f), maxY.coerceAtLeast(0f))
                )
              } else if (videoZoomScale < 1.08f || videoZoomScale < 1.0f) {
                targetScale = 1.0f
                targetOffset = Offset.Zero
              }

              if (targetScale != videoZoomScale || targetOffset != videoOffset) {
                isInteractingWithZoom = true
                snapJob = scope.launch {
                  val startScale = videoZoomScale
                  val startOffset = videoOffset
                  animate(0f, 1f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { fraction, _ ->
                    videoZoomScale = startScale + (targetScale - startScale) * fraction
                    videoOffset = Offset(
                      startOffset.x + (targetOffset.x - startOffset.x) * fraction,
                      startOffset.y + (targetOffset.y - startOffset.y) * fraction
                    )
                  }
                  isInteractingWithZoom = false
                }
              } else {
                isInteractingWithZoom = false
              }
            } else {
              isInteractingWithZoom = false
            }
          } catch (e: Exception) {
            print(e)
            isInteractingWithZoom = false
          } finally {
            if (snapJob?.isActive != true) {
              isInteractingWithZoom = false
            }
          }
        }
      }
      .pointerInput(volumeGestureEnabled, brightnessGestureEnabled, isInPipMode, videoZoomScale, anyMenuVisible) {
        if (isInPipMode) return@pointerInput
        val activity = findActivity(context)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var volumeAccumulator = 0f
        var brightnessAccumulator = 0f

        awaitEachGesture {
          val down = awaitFirstDown(requireUnconsumed = false)
          if (anyMenuVisible) return@awaitEachGesture
          var dragStarted = false
          val touchSlop = viewConfiguration.touchSlop

          do {
            val event = awaitPointerEvent()
            if (event.changes.any { it.isConsumed }) {
              dragStarted = false
              showGestureIndicator = false
              break
            }
            val maxEdgeSize = 100.dp.toPx()
            val edgeSize = (size.width * 0.2f).coerceAtMost(maxEdgeSize)
            val isEdgeTouch = down.position.x < edgeSize || down.position.x > size.width - edgeSize

            if (event.changes.size > 1 || (videoZoomScale > 1.05f && !isEdgeTouch)) {
              dragStarted = false
              showGestureIndicator = false
              break
            }

            val change = event.changes.first()
            if (change.pressed) {
              val dragAmountY = change.position.y - down.position.y
              if (!dragStarted && abs(dragAmountY) > touchSlop) {
                dragStarted = true
                showGestureIndicator = true
                volumeAccumulator = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                val params = activity?.window?.attributes
                brightnessAccumulator = if (params != null && params.screenBrightness >= 0f) {
                  params.screenBrightness
                } else {
                  // If screenBrightness is -1 (default), try to get from system
                  try {
                    android.provider.Settings.System.getInt(
                      context.contentResolver,
                      android.provider.Settings.System.SCREEN_BRIGHTNESS
                    ) / 255f
                  } catch (e: Exception) {
                    print(e)
                    0.5f
                  }
                }
              }

              if (dragStarted) {
                val deltaY = change.position.y - change.previousPosition.y
                val isLeftSide = down.position.x < size.width / 2

                if (isLeftSide && brightnessGestureEnabled) {
                  activity?.let {
                    brightnessAccumulator -= deltaY / size.height
                    brightnessAccumulator = brightnessAccumulator.coerceIn(0f, 1f)
                    val params = it.window.attributes
                    params.screenBrightness = brightnessAccumulator
                    it.window.attributes = params
                    gestureIcon = Icons.Default.BrightnessLow
                    gestureText = "${(brightnessAccumulator * 100).roundToInt()}%"
                  }
                } else if (!isLeftSide && volumeGestureEnabled) {
                  val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                  val oldVolumeInt = volumeAccumulator.roundToInt().coerceIn(0, maxVolume)
                  // Increase sensitivity slightly: 0.7 of screen height for full volume range
                  volumeAccumulator -= (deltaY / (size.height * 0.7f) * maxVolume)
                  val newVolumeInt = volumeAccumulator.roundToInt().coerceIn(0, maxVolume)

                  if (newVolumeInt != oldVolumeInt) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolumeInt, 0)
                  }
                  gestureIcon = Icons.AutoMirrored.Filled.VolumeUp
                  // Use volumeAccumulator for smooth percentage display
                  val smoothPercent = (volumeAccumulator.coerceIn(0f, maxVolume.toFloat()) / maxVolume * 100).roundToInt()
                  gestureText = "$smoothPercent%"
                }
                change.consume()
              }
            } else if (dragStarted) {
              // Consume the up event to prevent triggering tap
              change.consume()
            }
          } while (event.changes.any { it.pressed })
          showGestureIndicator = false
        }
      }
      .pointerInput(isInPipMode, isControlsVisible, doubleTapSkipDuration, longPressSpeedValue, videoZoomScale, anyMenuVisible) {
        if (isInPipMode) return@pointerInput
        awaitEachGesture {
          val down = awaitFirstDown(requireUnconsumed = false)
          if (anyMenuVisible) return@awaitEachGesture
          val initialX = down.position.x
          val initialY = down.position.y
          var longPressTriggered = false
          val touchSlop = viewConfiguration.touchSlop

          val longPressJob = scope.launch {
            delay(500)
            longPressTriggered = true
            if (isControlsVisible) isControlsVisible = false
            originalSpeedBeforeLongPress = playbackSpeed
            exoPlayer.setPlaybackSpeed(longPressSpeedValue)
            isLongPressing = true
          }

          var seekingStarted = false
          var basePositionForSeeking = 0L

          do {
            val event = awaitPointerEvent()
            if (event.changes.any { it.isConsumed }) {
              longPressJob.cancel()
              if (isLongPressing) {
                exoPlayer.setPlaybackSpeed(originalSpeedBeforeLongPress)
                isLongPressing = false
              }
              break
            }
            if (event.changes.size > 1) {
              longPressJob.cancel()
              if (isLongPressing) {
                exoPlayer.setPlaybackSpeed(originalSpeedBeforeLongPress)
                isLongPressing = false
              }
            }
            val change = event.changes.first()

            if (!longPressTriggered) {
              val dragDistance = abs(change.position.x - initialX) + abs(change.position.y - initialY)
              if (dragDistance > touchSlop) {
                longPressJob.cancel()
              }
            }

            if (isLongPressing || longPressTriggered) {
              if (isLongPressing) {
                val dragAmountX = change.position.x - initialX
                if (!seekingStarted && abs(dragAmountX) > touchSlop) {
                  seekingStarted = true
                  isDragging = true
                  basePositionForSeeking = exoPlayer.currentPosition
                }

                if (seekingStarted) {
                  val screenWidth = size.width.toFloat()
                  // Swipe across full screen = 2 minutes seek
                  val seekDelta = (dragAmountX / screenWidth * 120000L).toLong()
                  dragTime = (basePositionForSeeking + seekDelta).coerceIn(0, exoPlayer.duration)
                }
              }
              change.consume()
            }
          } while (event.changes.any { it.pressed })

          longPressJob.cancel()
          if (isLongPressing || longPressTriggered) {
            if (isLongPressing) {
              exoPlayer.setPlaybackSpeed(originalSpeedBeforeLongPress)
              isLongPressing = false
              if (isDragging) {
                exoPlayer.seekTo(dragTime)
                isDragging = false
              }
            }
          }
        }
      }
      .pointerInput(isInPipMode, isControlsVisible, doubleTapSkipDuration, anyMenuVisible) {
        if (isInPipMode) return@pointerInput
        detectTapGestures(
          onTap = {
            if (anyMenuVisible) {
              showEpisodeSideMenu = false
              showServerSideMenu = false
              showSettingsSideMenu = false
              showSettingsBottomSheet = false
              showSpeedMenu = false
              showQualityMenu = false
            } else {
              isControlsVisible = !isControlsVisible
            }
          },
          onDoubleTap = { offset ->
            if (anyMenuVisible) return@detectTapGestures
            if (isControlsVisible) isControlsVisible = false
            val skipMs = doubleTapSkipDuration * 1000L
            if (offset.x < size.width / 2) {
              exoPlayer.seekTo((exoPlayer.currentPosition - skipMs).coerceAtLeast(0))
              doubleTapSide = "left"
              doubleTapText = "-${doubleTapSkipDuration}s"
            } else {
              exoPlayer.seekTo((exoPlayer.currentPosition + skipMs).coerceAtMost(exoPlayer.duration))
              doubleTapSide = "right"
              doubleTapText = "+${doubleTapSkipDuration}s"
            }
            showDoubleTapIndicator = true
            scope.launch {
              delay(800)
              showDoubleTapIndicator = false
            }
          }
        )
      }
  ) {
    key(screenState.isLandscape && isFullScreen) {
      AndroidView(
        factory = { ctx ->
          PlayerView(ctx).apply {
            player = exoPlayer
            useController = false
          }
        },
        modifier = Modifier
          .fillMaxSize()
          .graphicsLayer(
            scaleX = videoZoomScale,
            scaleY = videoZoomScale,
            translationX = videoOffset.x,
            translationY = videoOffset.y
          )
      )
    }
    if (playerData == null && !poster.isNullOrEmpty()) {
      AsyncImage(
        model = poster,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }

    Box(modifier = Modifier.fillMaxSize()) {
      AnimatedVisibility(
        visible = isControlsVisible && errorMessage == null && !isInPipMode,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
        )
      }

      AnimatedVisibility(
        visible = isControlsVisible && !isDragging && !isInPipMode,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isFullScreen) 32.dp else 16.dp)
            .padding(horizontal = 8.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { if (isFullScreen) onFullScreenChange(false) else onBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
          }
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 4.dp)
          ) {
            Text(
              text = title,
              color = Color.White,
              fontSize = if (isFullScreen) 18.sp else 15.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = SmallTextStyle
            )
            if (subtitle.isNotEmpty()) {
              Text(
                text = stringResource(id = R.string.episode_label, subtitle),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = if (isFullScreen) 16.sp else 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = SmallTextStyle
              )
            }
          }
          IconButton(onClick = onReload) { Icon(Icons.Default.Refresh, null, tint = Color.White) }
          IconButton(onClick = {
            if (isFullScreen) {
              showSettingsSideMenu = true
            } else {
              showSettingsBottomSheet =
                true
            }; isControlsVisible = false
          }) {
            Icon(Icons.Default.Settings, "Settings", tint = Color.White)
          }
        }
      }

      if (errorMessage != null && !isInPipMode) {
        Column(
          modifier = Modifier
            .align(Alignment.Center)
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            Icons.Default.ErrorOutline,
            null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = errorMessage,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onReload,
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
          ) { Text(stringResource(R.string.retry)) }
        }
      } else {
        val showLoading = (isLoading || isBuffering || playerData == null) && !isDragging
        if (showLoading && !isInPipMode) {
          CircularProgressIndicator(
            modifier = Modifier
              .align(Alignment.Center)
              .size(42.dp)
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
              ) { exoPlayer.pause() },
            color = Color.White, strokeWidth = 3.dp
          )
        }
        AnimatedVisibility(
          visible = isControlsVisible && !isDragging && !isInPipMode,
          enter = fadeIn(),
          exit = fadeOut(),
          modifier = Modifier.align(Alignment.Center)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
          ) {
            IconButton(onClick = {
              exoPlayer.seekTo(
                (exoPlayer.currentPosition - 10000).coerceAtLeast(
                  0
                )
              )
            }, modifier = Modifier.size(32.dp), enabled = playerData != null) {
              Icon(
                Icons.Default.Replay10,
                "Rewind 10s",
                tint = if (playerData != null) Color.White else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxSize()
              )
            }
            if (!showLoading) {
              IconButton(
                onClick = { if (isPlaying) exoPlayer.pause() else exoPlayer.play() },
                modifier = Modifier.size(48.dp)
              ) {
                Icon(
                  if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  if (isPlaying) "Pause" else "Play",
                  tint = Color.White,
                  modifier = Modifier.fillMaxSize()
                )
              }
            } else {
              Spacer(modifier = Modifier.size(48.dp))
            }
            IconButton(onClick = {
              exoPlayer.seekTo(
                (exoPlayer.currentPosition + 10000).coerceAtMost(
                  exoPlayer.duration
                )
              )
            }, modifier = Modifier.size(32.dp), enabled = playerData != null) {
              Icon(
                Icons.Default.Forward10,
                "Forward 10s",
                tint = if (playerData != null) Color.White else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }

      if (isDragging) {
        Box(
          modifier = Modifier
            .align(Alignment.Center)
            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = formatDuration(dragTime),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (showGestureIndicator) {
        GestureIndicator(
          icon = gestureIcon,
          text = gestureText,
          modifier = Modifier.align(Alignment.Center)
        )
      }

      if (showDoubleTapIndicator) {
        DoubleTapIndicator(
          side = doubleTapSide,
          text = doubleTapText,
          modifier = Modifier.align(
            if (doubleTapSide == "left") Alignment.CenterStart else Alignment.CenterEnd
          )
        )
      }

      if ((isLongPressing || (videoZoomScale > 1.01f && isInteractingWithZoom)) && !isDragging) {
        val labelText = when {
          isLongPressing -> "${longPressSpeedValue}x"
          videoZoomScale >= 2.99f -> context.getString(R.string.video_aspect_zoom_format, videoZoomScale)
          videoZoomScale <= 1.01f -> context.getString(R.string.video_aspect_original)
          else -> {
            val videoSize = exoPlayer.videoSize
            if (videoSize.width > 0 && videoSize.height > 0) {
              val videoAspect = videoSize.width.toFloat() / videoSize.height.toFloat()
              val screenWidth = containerSize.width.toFloat()
              val screenHeight = containerSize.height.toFloat()
              val screenAspect = if (screenWidth > 0) screenWidth / screenHeight else 1.777f
              val fitScale = if (videoAspect > screenAspect) {
                screenHeight / (screenWidth / videoAspect)
              } else {
                screenWidth / (screenHeight * videoAspect)
              }
              if (abs(videoZoomScale - fitScale) < 0.02f) {
                context.getString(R.string.video_aspect_zoom_fill)
              } else {
                context.getString(R.string.video_aspect_zoom_format, videoZoomScale)
              }
            } else {
              context.getString(R.string.video_aspect_zoom_format, videoZoomScale)
            }
          }
        }

        Box(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = if (isFullScreen) 48.dp else 16.dp)
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              if (isLongPressing) Icons.Default.Speed else Icons.Default.Fullscreen,
              null,
              tint = MainColor,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = labelText,
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      if (showSkipNotification) {
        SkipNotification(
          text = skipNotificationText,
          secondsRemaining = skipRemainingSeconds,
          onSkip = {
            exoPlayer.seekTo(skipTargetTime.toLong())
            showSkipNotification = false
          },
          onClose = { showSkipNotification = false },
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(
              end = if (isFullScreen) 32.dp else 16.dp,
              bottom = if (isFullScreen) 32.dp else 24.dp
            )
        )
      }

      AnimatedVisibility(
        visible = showNotification,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it },
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(
            top = if (isFullScreen) 48.dp else 16.dp
          )
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(enabled = isNotificationClickable) {
              showNotification = false
              isNotificationClickable = false
              isAutoNexting = false
              onNextEpisode()
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(notificationIcon, null, tint = MainColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = notificationText,
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      AnimatedVisibility(
        visible = isControlsVisible && errorMessage == null && !isInPipMode,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it },
        modifier = Modifier.align(Alignment.BottomCenter)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(
              horizontal = if (isFullScreen) 32.dp else 12.dp,
              vertical = if (isFullScreen) 16.dp else 2.dp
            )
        ) {
          if (!isDragging) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${formatDuration(currentTime)} / ${formatDuration(duration)}",
                color = Color.White,
                fontSize = if (isFullScreen) 16.sp else 13.sp
              )
              IconButton(
                onClick = { onFullScreenChange(!isFullScreen) },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                  "Toggle Full Screen",
                  tint = Color.White,
                  modifier = Modifier.fillMaxSize()
                )
              }
            }
          }
          Slider(
            value = (if (isDragging) dragTime else currentTime).toFloat(),
            onValueChange = { isDragging = true; dragTime = it.toLong() },
            onValueChangeFinished = { exoPlayer.seekTo(dragTime); isDragging = false },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(
              activeTrackColor = MainColor,
              inactiveTrackColor = Color.White.copy(alpha = 0.2f),
              thumbColor = Color.Transparent
            ),
            thumb = {
              Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(12.dp)
                    .background(MainColor, CircleShape)
                    .border(1.dp, Color.White, CircleShape)
                )
              }
            },
            track = { sliderState ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Canvas(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                ) {
                  val trackWidth = size.width
                  val trackH = size.height
                  val radius = trackH / 2
                  drawRoundRect(
                    color = Color.White.copy(alpha = 0.2f),
                    cornerRadius = CornerRadius(radius, radius)
                  )
                  if (duration > 0) {
                    val bufferedEnd = (bufferedPosition.toFloat() / duration) * trackWidth
                    drawRoundRect(
                      color = Color.White.copy(alpha = 0.4f),
                      size = Size(bufferedEnd, trackH),
                      cornerRadius = CornerRadius(radius, radius)
                    )
                  }
                  drawRoundRect(
                    color = MainColor,
                    size = Size(
                      (sliderState.value / sliderState.valueRange.endInclusive) * trackWidth,
                      trackH
                    ),
                    cornerRadius = CornerRadius(radius, radius)
                  )
                  introRange?.let { range ->
                    if (duration > 0) {
                      val start =
                        (range.first.toFloat() * 1000 / duration).coerceIn(0f, 1f) * trackWidth
                      val end =
                        (range.last.toFloat() * 1000 / duration).coerceIn(0f, 1f) * trackWidth
                      drawRect(
                        color = Color(0xFF2196F3),
                        topLeft = Offset(start, 0f),
                        size = Size((end - start).coerceAtLeast(0f), trackH)
                      )
                    }
                  }
                  outroRange?.let { range ->
                    if (duration > 0) {
                      val start =
                        (range.first.toFloat() * 1000 / duration).coerceIn(0f, 1f) * trackWidth
                      val end =
                        (range.last.toFloat() * 1000 / duration).coerceIn(0f, 1f) * trackWidth
                      drawRect(
                        color = Color(0xFF2196F3),
                        topLeft = Offset(start, 0f),
                        size = Size((end - start).coerceAtLeast(0f), trackH)
                      )
                    }
                  }
                }
              }
            },
            modifier = Modifier.height(24.dp)
          )

          if (isFullScreen) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PlayerControlSmallButton(
                  icon = Icons.Default.SkipNext,
                  text = stringResource(R.string.next_ep),
                  onClick = onNextEpisode
                )
                PlayerControlSmallButton(
                  icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                  onClick = {
                    showEpisodeSideMenu = true; isControlsVisible = false
                  }
                )
              }
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PlayerControlSmallButton(
                  icon = Icons.Default.Dns,
                  onClick = {
                    showServerSideMenu = true; isControlsVisible = false
                  }
                )
                Box {
                  PlayerControlSmallButton(
                    icon = Icons.Default.HighQuality,
                    text = selectedQualityLabel,
                    onClick = { showQualityMenu = true }
                  )
                  DropdownMenu(
                    expanded = showQualityMenu,
                    onDismissRequest = { showQualityMenu = false },
                    modifier = Modifier.background(Color(0xFF2B2B2B))
                  ) {
                    DropdownMenuItem(
                      text = {
                        Text(
                          "Auto",
                          color = if (selectedQualityLabel == "Auto") MainColor else Color.White
                        )
                      },
                      onClick = {
                        exoPlayer.trackSelectionParameters =
                          exoPlayer.trackSelectionParameters.buildUpon()
                            .clearOverridesOfType(C.TRACK_TYPE_VIDEO).build(); showQualityMenu =
                          false
                      }
                    )
                    availableQualities.forEach { quality ->
                      DropdownMenuItem(
                        text = {
                          Text(
                            quality.label,
                            color = if (selectedQualityLabel == quality.label) MainColor else Color.White
                          )
                        },
                        onClick = {
                          exoPlayer.trackSelectionParameters =
                            exoPlayer.trackSelectionParameters.buildUpon().setOverrideForType(
                              TrackSelectionOverride(
                                quality.group.mediaTrackGroup,
                                quality.trackIndex
                              )
                            ).build(); selectedQualityLabel = quality.label; showQualityMenu = false
                        }
                      )
                    }
                  }
                }
                Box {
                  PlayerControlSmallButton(
                    icon = Icons.Default.SlowMotionVideo,
                    text = "${if (playbackSpeed % 1.0f == 0.0f) playbackSpeed.toInt() else playbackSpeed}x",
                    onClick = { showSpeedMenu = true }
                  )
                  DropdownMenu(
                    expanded = showSpeedMenu,
                    onDismissRequest = { showSpeedMenu = false },
                    modifier = Modifier.background(Color(0xFF2B2B2B))
                  ) {
                    speeds.forEach { speed ->
                      DropdownMenuItem(text = {
                        Text(
                          "${speed}x",
                          color = if (playbackSpeed == speed) MainColor else Color.White
                        )
                      }, onClick = { exoPlayer.setPlaybackSpeed(speed); showSpeedMenu = false })
                    }
                  }
                }
              }
            }
          }
        }
      }

      PlayerSideMenu(visible = showEpisodeSideMenu, onDismiss = { showEpisodeSideMenu = false }) {
        EpisodeSelectorContent(
          displaySeasons = displaySeasons,
          activeDisplaySeasonId = activeDisplaySeasonId,
          episodes = episodes,
          currentEpisodeId = currentEpisode?.id,
          onSeasonClick = onSeasonSelected,
          onChapterClick = { chap, seasonId ->
            onEpisodeSelected(
              chap,
              seasonId
            ); showEpisodeSideMenu = false
          },
          chapterProgress = chapterProgress,
          isSideMenu = true,
          onClose = { showEpisodeSideMenu = false },
          isLoading = isEpisodesLoading
        )
      }
      PlayerSideMenu(
        visible = showServerSideMenu,
        onDismiss = { showServerSideMenu = false },
        title = stringResource(R.string.server_label)
      ) {
        ServerSelectorContent(
          servers = servers,
          currentServer = currentServer,
          onServerClick = { onServerSelected(it); showServerSideMenu = false }
        )
      }
      PlayerSideMenu(
        visible = showSettingsSideMenu,
        onDismiss = { showSettingsSideMenu = false },
        title = stringResource(R.string.settings)
      ) {
        SettingsSideMenuContent(
          servers = servers,
          currentServer = currentServer,
          onServerSelected = { onServerSelected(it); showSettingsSideMenu = false },
          qualities = availableQualities.map { it.label },
          currentQuality = selectedQualityLabel,
          onQualitySelected = { label ->
            if (label == "Auto") {
              exoPlayer.trackSelectionParameters =
                exoPlayer.trackSelectionParameters.buildUpon()
                  .clearOverridesOfType(C.TRACK_TYPE_VIDEO)
                  .build()
            } else {
              availableQualities.find { it.label == label }?.let { q ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                  .setOverrideForType(TrackSelectionOverride(q.group.mediaTrackGroup, q.trackIndex))
                  .build()
              }
            }; showSettingsSideMenu = false
          },
          speeds = speeds,
          currentSpeed = playbackSpeed,
          onSpeedSelected = { exoPlayer.setPlaybackSpeed(it); showSettingsSideMenu = false },
          volumeGestureEnabled = volumeGestureEnabled,
          onVolumeGestureToggle = { scope.launch { preferencesManager.setVolumeGesture(it) } },
          brightnessGestureEnabled = brightnessGestureEnabled,
          onBrightnessGestureToggle = { scope.launch { preferencesManager.setBrightnessGesture(it) } },
          autoSkipEnabled = autoSkipEnabled,
          onAutoSkipToggle = { scope.launch { preferencesManager.setAutoSkip(it) } },
          doubleTapSkipDuration = doubleTapSkipDuration,
          onDoubleTapSkipDurationChange = { scope.launch { preferencesManager.setDoubleTapSkip(it) } },
          longPressSpeed = longPressSpeedValue,
          onLongPressSpeedChange = { scope.launch { preferencesManager.setLongPressSpeed(it) } },
          syncMode = syncMode,
          onSyncModeChange = onSyncModeChange,
          sleepTimerMinutes = sleepTimerMinutes,
          onSleepTimerChange = onSleepTimerChange,
          pauseAfterCurrentEpisode = pauseAfterCurrentEpisode,
          onPauseAfterCurrentEpisodeChange = onPauseAfterCurrentEpisodeChange,
          sleepTimerRemainingSeconds = sleepTimerRemainingSeconds
        )
      }
    }
    if (showSettingsBottomSheet) {
      ModalBottomSheet(
        onDismissRequest = {
          showSettingsBottomSheet = false; settingsSubMenu = null
        },
        sheetState = rememberModalBottomSheetState(),
        containerColor = DarkSurface,
        contentColor = Color.White,
        dragHandle = null
      ) {
        SettingsBottomSheetContent(
          settingsSubMenu = settingsSubMenu,
          onSubMenuChange = { settingsSubMenu = it },
          servers = servers,
          currentServer = currentServer,
          onServerSelected = onServerSelected,
          availableQualities = availableQualities.map { it.label },
          selectedQualityLabel = selectedQualityLabel,
          onQualitySelected = { label ->
            if (label == "Auto") {
              exoPlayer.trackSelectionParameters =
                exoPlayer.trackSelectionParameters.buildUpon()
                  .clearOverridesOfType(C.TRACK_TYPE_VIDEO)
                  .build()
            } else {
              availableQualities.find { it.label == label }?.let { q ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                  .setOverrideForType(TrackSelectionOverride(q.group.mediaTrackGroup, q.trackIndex))
                  .build()
              }
            }
          },
          speeds = speeds,
          playbackSpeed = playbackSpeed,
          onSpeedSelected = { exoPlayer.setPlaybackSpeed(it) },
          volumeGestureEnabled = volumeGestureEnabled,
          onVolumeGestureToggle = { scope.launch { preferencesManager.setVolumeGesture(it) } },
          brightnessGestureEnabled = brightnessGestureEnabled,
          onBrightnessGestureToggle = { scope.launch { preferencesManager.setBrightnessGesture(it) } },
          autoSkipEnabled = autoSkipEnabled,
          onAutoSkipToggle = { scope.launch { preferencesManager.setAutoSkip(it) } },
          doubleTapSkipDuration = doubleTapSkipDuration,
          onDoubleTapSkipDurationChange = { scope.launch { preferencesManager.setDoubleTapSkip(it) } },
          longPressSpeed = longPressSpeedValue,
          onLongPressSpeedChange = { scope.launch { preferencesManager.setLongPressSpeed(it) } },
          syncMode = syncMode,
          onSyncModeChange = onSyncModeChange,
          sleepTimerMinutes = sleepTimerMinutes,
          onSleepTimerChange = onSleepTimerChange,
          pauseAfterCurrentEpisode = pauseAfterCurrentEpisode,
          onPauseAfterCurrentEpisodeChange = onPauseAfterCurrentEpisodeChange,
          sleepTimerRemainingSeconds = sleepTimerRemainingSeconds,
          onDismiss = { showSettingsBottomSheet = false; settingsSubMenu = null }
        )
      }
    }
  }
}
