package git.shin.animevsub.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import git.shin.animevsub.R
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.ui.styles.SmallTextStyle
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.utils.formatDuration
import kotlinx.coroutines.delay
import java.io.File
import androidx.core.net.toUri

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
  onBack: () -> Unit = {},
  onReload: () -> Unit = {},
  onSettings: () -> Unit = {},
  onVideoEnded: () -> Unit = {}
) {
  val context = LocalContext.current
  var isPlaying by remember { mutableStateOf(true) }
  var isBuffering by remember { mutableStateOf(false) }

  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      playWhenReady = true
      addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
          isBuffering = playbackState == Player.STATE_BUFFERING
          if (playbackState == Player.STATE_ENDED) {
            onVideoEnded()
          }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
          isPlaying = playWhenReady
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

  LaunchedEffect(isControlsVisible, isDragging, isPlaying) {
    if (isControlsVisible && !isDragging && isPlaying) {
      delay(5000)
      isControlsVisible = false
    }
  }

  LaunchedEffect(playerData) {
    if (playerData == null || playerData.link.isEmpty()) return@LaunchedEffect

    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
      .setDefaultRequestProperties(playerData.headers ?: emptyMap())

    // Combine with default factory to support both file and HTTP
    val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
      context,
      httpDataSourceFactory
    )

    val mediaItem: MediaItem = if (playerData.isContent && playerData.type.lowercase() == "hls") {
      val tempFile = File(context.cacheDir, "temp_playlist.m3u8")
      tempFile.writeText(playerData.link)

      MediaItem.fromUri(tempFile.toUri())
    } else {
      MediaItem.fromUri(Uri.parse(playerData.link))
    }

    when (playerData.type.lowercase()) {
      "hls" -> {
        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
          .createMediaSource(mediaItem)
        exoPlayer.setMediaSource(hlsMediaSource)
      }
      "mp4" -> {
        exoPlayer.setMediaItem(mediaItem)
      }
      else -> {
        exoPlayer.setMediaItem(mediaItem)
      }
    }

    exoPlayer.prepare()
  }

  DisposableEffect(Unit) {
    onDispose {
      exoPlayer.release()
    }
  }

  Box(
    modifier = modifier
      .background(Color.Black)
      .clipToBounds()
      .clickable(
        interactionSource = remember { MutableInteractionSource() }, indication = null
      ) {
        isControlsVisible = !isControlsVisible
      }) {
    AndroidView(
      factory = { ctx ->
        PlayerView(ctx).apply {
          player = exoPlayer
          useController = false
        }
      }, modifier = Modifier.fillMaxSize()
    )

    // Overlay UI
    Box(modifier = Modifier.fillMaxSize()) {

      // Full screen background overlay
      AnimatedVisibility(
        visible = isControlsVisible && errorMessage == null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
        )
      }

      // Top Bar
      AnimatedVisibility(
        visible = isControlsVisible && !isDragging,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 8.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = null,
              tint = Color.White,
            )
          }
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 4.dp)
          ) {
            Text(
              text = title,
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = SmallTextStyle,
            )
            if (subtitle.isNotEmpty()) {
              Text(
                text = stringResource(id = R.string.ep_label, subtitle),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = SmallTextStyle,
              )
            }
          }
          IconButton(onClick = onReload) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = null,
              tint = Color.White,
            )
          }

          IconButton(onClick = {}) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Settings",
              tint = Color.White,
            )
          }

        }
      }

      // Error Message
      if (errorMessage != null) {
        Column(
          modifier = Modifier
            .align(Alignment.Center)
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = errorMessage, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onReload,
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
          ) {
            Text(stringResource(R.string.retry))
          }
        }
      } else {
        // Center Area: Loading Indicator & Controls
        val showLoading = (isLoading || isBuffering || playerData == null) && !isDragging

        if (showLoading) {
          CircularProgressIndicator(
            modifier = Modifier
              .align(Alignment.Center)
              .size(42.dp),
            color = Color(0xFFFFFFFF),
            strokeWidth = 3.dp
          )
        }

        // Center Controls (Play/Pause, Rewind, Forward)
        AnimatedVisibility(
          visible = isControlsVisible && !isDragging,
          enter = fadeIn(),
          exit = fadeOut(),
          modifier = Modifier.align(Alignment.Center)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
          ) {
            IconButton(
              onClick = {
                exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0))
              },
              modifier = Modifier.size(32.dp),
              enabled = playerData != null
            ) {
              Icon(
                imageVector = Icons.Default.Replay10,
                contentDescription = "Rewind 10s",
                tint = if (playerData != null) Color.White else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxSize()
              )
            }

            if (showLoading) {
              // Hide play button when loading, indicator is shown behind or at same position
              Spacer(modifier = Modifier.size(48.dp))
            } else {
              IconButton(
                onClick = {
                  if (isPlaying) {
                    exoPlayer.pause()
                  } else {
                    exoPlayer.play()
                  }
                },
                modifier = Modifier.size(48.dp)
              ) {
                Icon(
                  imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlaying) "Pause" else "Play",
                  tint = Color.White,
                  modifier = Modifier.fillMaxSize()
                )
              }
            }

            IconButton(
              onClick = {
                exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration))
              },
              modifier = Modifier.size(32.dp),
              enabled = playerData != null
            ) {
              Icon(
                imageVector = Icons.Default.Forward10,
                contentDescription = "Forward 10s",
                tint = if (playerData != null) Color.White else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }

      // Time Popup when dragging
      if (isDragging) {
        Box(
          modifier = Modifier
            .align(Alignment.Center)
            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = formatDuration(dragTime),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Bottom Controls
      AnimatedVisibility(
        visible = isControlsVisible && errorMessage == null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier.align(Alignment.BottomCenter)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
        ) {
          if (!isDragging) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${formatDuration(currentTime)} / ${formatDuration(duration)}",
                color = Color.White,
                fontSize = 13.sp
              )
            }
          }

          // Compact Seekbar
          Slider(
            value = (if (isDragging) dragTime else currentTime).toFloat(),
            onValueChange = {
              isDragging = true
              dragTime = it.toLong()
            },
            onValueChangeFinished = {
              exoPlayer.seekTo(dragTime)
              isDragging = false
            },
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

                  val activeEnd =
                    (sliderState.value / sliderState.valueRange.endInclusive) * trackWidth
                  drawRoundRect(
                    color = MainColor,
                    size = Size(activeEnd, trackH),
                    cornerRadius = CornerRadius(radius, radius)
                  )

                  // Intro Range (Blue)
                  introRange?.let { range ->
                    if (duration > 0) {
                      val start = (range.first.toFloat() / duration).coerceIn(0f, 1f) * trackWidth
                      val end = (range.last.toFloat() / duration).coerceIn(0f, 1f) * trackWidth
                      drawRect(
                        color = Color(0xFF2196F3),
                        topLeft = Offset(start, 0f),
                        size = Size((end - start).coerceAtLeast(0f), trackH)
                      )
                    }
                  }

                  // Outro Range (Blue)
                  outroRange?.let { range ->
                    if (duration > 0) {
                      val start = (range.first.toFloat() / duration).coerceIn(0f, 1f) * trackWidth
                      val end = (range.last.toFloat() / duration).coerceIn(0f, 1f) * trackWidth
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
        }
      }
    }
  }
}
