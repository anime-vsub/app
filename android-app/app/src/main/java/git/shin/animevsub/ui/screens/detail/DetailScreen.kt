package git.shin.animevsub.ui.screens.detail

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.*
import git.shin.animevsub.ui.theme.*
import java.util.Calendar

private val NoPaddingTextStyle = TextStyle(
  platformStyle = PlatformTextStyle(includeFontPadding = false)
)

private val DetailSmallTextStyle = NoPaddingTextStyle.copy(
  lineHeight = 14.sp
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun DetailScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String) -> Unit,
  onNavigateToCategory: (String, String) -> Unit,
  viewModel: DetailViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState()
  var showBottomSheet by remember { mutableStateOf(false) }

  // List states for scrolling
  val seasonListState = rememberLazyListState()

  // Store scroll states for each season to maintain separate scroll positions
  val chapterListStates = remember { mutableStateMapOf<String, LazyListState>() }
  val currentChapterListState = chapterListStates.getOrPut(uiState.currentSeasonId) {
    LazyListState()
  }

  // Scroll to current chapter only if it's the currently viewed season
  LaunchedEffect(uiState.currentChapIndex, uiState.currentSeasonId) {
    if (uiState.currentChapIndex >= 0 && uiState.currentSeasonId == uiState.animeId) {
      currentChapterListState.animateScrollToItem(uiState.currentChapIndex)
    }
  }

  // ExoPlayer setup
  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      playWhenReady = true
      addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
          if (playbackState == Player.STATE_ENDED && uiState.autoNext) {
            viewModel.playNext()
          }
        }
      })
    }
  }

  // Update media when URL changes
  LaunchedEffect(uiState.videoUrl) {
    uiState.videoUrl?.let { url ->
      val dataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent("Mozilla/5.0")
        .setAllowCrossProtocolRedirects(true)

      val mediaSource =
        HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url))

      exoPlayer.setMediaSource(mediaSource)
      exoPlayer.prepare()
    }
  }

  // Cleanup
  DisposableEffect(Unit) {
    onDispose {
      exoPlayer.release()
      val activity = context as? Activity
      activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
  }

  Scaffold(
    containerColor = DarkBackground
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 0.dp)
        .padding(innerPadding)
    ) {
      val detail = uiState.detail

      // Player or Poster Area (Fixed at top)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(16f / 9f)
          .background(Color.Black)
      ) {
        if (uiState.videoUrl != null) {
          AndroidView(
            factory = { ctx ->
              PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
              }
            }, modifier = Modifier.fillMaxSize()
          )

          if (uiState.isPlayerLoading) {
            CircularProgressIndicator(
              modifier = Modifier.align(Alignment.Center), color = AccentMain
            )
          }
        } else if (detail != null) {
          AsyncImage(
            model = detail.poster ?: detail.image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                )
              )
          )

          if (uiState.chapterData?.chaps?.isNotEmpty() == true) {
            IconButton(
              onClick = {
                viewModel.playChapter(
                  uiState.chapterData!!.chaps.first(), uiState.animeId
                )
              },
              modifier = Modifier
                .align(Alignment.Center)
                .size(56.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.watch_now),
                tint = Color.White,
                modifier = Modifier.size(36.dp)
              )
            }
          }
        } else {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .shimmerEffect()
          )
        }

        // Top buttons
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .align(Alignment.TopCenter),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back),
              tint = Color.White
            )
          }
        }
      }

      // Scrollable Content
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .verticalScroll(rememberScrollState())
      ) {
        if (uiState.error != null) {
          ErrorScreen(
            error = uiState.error,
            onRetry = { viewModel.retry() },
            modifier = Modifier
              .fillMaxSize()
              .padding(32.dp)
          )
        } else if (uiState.isLoading && detail == null) {
          DetailSkeleton()
        } else if (detail != null) {
          // Info Block (Clickable to open sheet)
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showBottomSheet = true }
              .padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = detail.name,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = NoPaddingTextStyle,
              )
              Icon(Icons.Default.ChevronRight, null, tint = TextGrey)
            }

            Row(
              modifier = Modifier.padding(top = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {

              Text(
                text = stringResource(R.string.views_count, formatNumber(detail.views)),
                color = TextGrey,
                fontSize = 12.sp,
                style = NoPaddingTextStyle
              )

              uiState.chapterData?.update?.let { update ->

                Text(
                  text = " • ", color = TextGrey, fontSize = 12.sp, style = NoPaddingTextStyle
                )

                Text(
                  text = formatScheduleUpdate(update),
                  color = AccentMain,
                  fontSize = 12.sp,
                  style = NoPaddingTextStyle
                )
              }
            }
          }

          Column(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
              // Author and Studio Section
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (detail.authors.isNotEmpty()) {

                  Text(
                    text = "${stringResource(R.string.author_label)} ",
                    color = TextGrey,
                    fontSize = 12.sp,
                    style = NoPaddingTextStyle,
                  )

                  Text(
                    text = detail.authors.first().name,
                    color = Color(0xFF00D639),
                    fontSize = 12.sp,
                    style = NoPaddingTextStyle,
                    modifier = Modifier.clickable {
                      onNavigateToCategory(
                        "tac-gia", detail.authors.first().id.removePrefix("/tac-gia/").trim('/')
                      )
                    })

                  Text(
                    text = " | ", color = TextGrey, fontSize = 12.sp, style = NoPaddingTextStyle
                  )
                }

                Text(
                  text = stringResource(
                    R.string.studio_prefix, detail.studio ?: stringResource(R.string.unknown)
                  ),
                  color = TextGrey,
                  fontSize = 12.sp,
                  style = NoPaddingTextStyle,
                  modifier = Modifier.clickable {
                    detail.studio?.let {
                      onNavigateToCategory("studio", it)
                    }
                  })
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Badges row
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                if (!detail.quality.isNullOrEmpty()) {
                  QualityBadge(quality = detail.quality)
                }
                if (detail.yearOf != null) {
                  Badge(text = detail.yearOf.toString())
                }
                if (!detail.duration.isNullOrEmpty()) {
                  Badge(text = stringResource(R.string.updated_to_episode, detail.duration))
                }
                if (detail.countries.isNotEmpty()) {

                  Text(
                    text = detail.countries.first().name,
                    color = Color(0xFF00D639),
                    fontSize = 12.sp,
                    style = NoPaddingTextStyle,
                    modifier = Modifier.clickable {
                      onNavigateToCategory(
                        "quoc-gia",
                        detail.countries.first().id.removePrefix("/quoc-gia/").trim('/')
                      )
                    })
                }
              }

              // Rating info (Stars on new line)
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
              ) {

                Text(
                  text = detail.rate.toString(),
                  color = TextPrimary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  style = NoPaddingTextStyle
                )
                Icon(Icons.Default.Star, null, tint = StarColor, modifier = Modifier.size(14.dp))

                Spacer(modifier = Modifier.width(8.dp))


                Text(
                  text = stringResource(R.string.rating_count, formatNumber(detail.countRate)),
                  color = TextGrey,
                  fontSize = 12.sp,
                  style = NoPaddingTextStyle,
                )
                detail.seasonOf?.let {

                  Text(
                    text = " | ", color = TextGrey, fontSize = 12.sp, style = NoPaddingTextStyle
                  )

                  Text(
                    text = it.name,
                    color = Color(0xFF00D639),
                    fontSize = 12.sp,
                    style = NoPaddingTextStyle,
                    modifier = Modifier.clickable {
                      onNavigateToCategory("season", it.id.removePrefix("/season/").trim('/'))
                    })
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Tags/Genres
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                detail.genre.forEach { genre ->

                  Text(
                    text = "#${genre.name}",
                    color = Color(0xFF00D639),
                    fontSize = 11.sp,
                    style = NoPaddingTextStyle.copy(lineHeight = 14.sp),
                    modifier = Modifier
                      .padding(vertical = 1.dp)
                      .clickable {
                        onNavigateToCategory(
                          "the-loai", genre.id.removePrefix("/the-loai/").trim('/')
                        )
                      })
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Action Buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ActionButton(
                  icon = Icons.Default.BookmarkBorder,
                  label = formatNumber(detail.follows),
                  modifier = Modifier.weight(1f)
                )
                ActionButton(
                  icon = Icons.Default.Share,
                  label = stringResource(R.string.share),
                  modifier = Modifier.weight(1f)
                )
                ActionButton(
                  icon = Icons.Default.PlaylistAdd,
                  label = stringResource(R.string.save_label),
                  modifier = Modifier.weight(1f)
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Episode Section
            if (uiState.isChaptersLoading) {
              ChapterSkeleton()
            } else if (uiState.chaptersError != null) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = uiState.chaptersError ?: "Lỗi tải tập phim",
                  color = TextSecondary,
                  fontSize = 13.sp
                )
                TextButton(onClick = { viewModel.retry() }) {
                  Text("Thử lại", color = AccentMain)
                }
              }
            } else {
              val chapterData = uiState.chapterData
              if (chapterData != null && chapterData.chaps.isNotEmpty()) {
                LazyRow(
                  state = currentChapterListState,
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                  items(chapterData.chaps.size) { index ->
                    val chap = chapterData.chaps[index]
                    val isSelected =
                      index == uiState.currentChapIndex && uiState.currentSeasonId == uiState.animeId

                    Box(
                      modifier = Modifier
                        .height(36.dp)
                        .widthIn(min = 45.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) Color(0xFF00D639).copy(alpha = 0.15f) else DarkCard)
                        .border(
                          width = if (isSelected) 1.5.dp else 1.dp,
                          color = if (isSelected) Color(0xFF00D639) else Color.Transparent,
                          shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { viewModel.playChapter(chap, uiState.currentSeasonId) },
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = chap.name,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = if (isSelected) Color(0xFF00D639) else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                  }
                }
              } else {
                Text(
                  text = stringResource(R.string.no_episodes),
                  color = TextSecondary,
                  fontSize = 13.sp,
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
              }
            }

            // Seasons
            if (detail.season.isNotEmpty()) {
              Spacer(modifier = Modifier.height(16.dp))

              // Find current season index for scrolling
              val currentSeasonIndex = detail.season.indexOfFirst {
                it.id.contains(uiState.currentSeasonId)
              }

              LaunchedEffect(currentSeasonIndex) {
                if (currentSeasonIndex >= 0) {
                  seasonListState.animateScrollToItem(currentSeasonIndex)
                }
              }

              LazyRow(
                state = seasonListState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
              ) {
                items(detail.season) { season ->
                  val seasonId = season.id
                  val isCurrent = seasonId == uiState.currentSeasonId

                  Box(
                    modifier = Modifier
                      .widthIn(min = 100.dp)
                      .height(36.dp)
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (isCurrent) Color(0xFF00D639).copy(alpha = 0.15f) else DarkCard)
                      .border(
                        width = if (isCurrent) 1.5.dp else 1.dp,
                        color = if (isCurrent) Color(0xFF00D639) else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                      )
                      .clickable {
                        viewModel.loadChaptersOnly(seasonId)
                      }, contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = season.name,
                      modifier = Modifier.padding(horizontal = 12.dp),
                      color = if (isCurrent) Color(0xFF00D639) else TextSecondary,
                      fontSize = 12.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }
            }
          }

          // Related
          if (detail.related.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = stringResource(R.string.recommended_for_you),
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            GridAnimeList(
              items = detail.related, onItemClick = { anime -> onNavigateToDetail(anime.animeId) })
          }
        }

        Spacer(modifier = Modifier.height(50.dp))
      }
    }

    // Detail Bottom Sheet
    if (showBottomSheet) {
      ModalBottomSheet(
        onDismissRequest = { showBottomSheet = false },
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextGrey) }) {
        val detailSheet = uiState.detail ?: return@ModalBottomSheet
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
              model = detailSheet.image,
              contentDescription = null,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .width(120.dp)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {

              Text(
                text = detailSheet.name,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )

              // Info section
              Column(modifier = Modifier.padding(top = 8.dp)) {
                InfoRow(
                  label = stringResource(R.string.language_label), value = detailSheet.language
                )
                InfoRow(
                  label = stringResource(R.string.country_label),
                  value = detailSheet.countries.firstOrNull()?.name,
                  onClick = {
                    detailSheet.countries.firstOrNull()?.let {
                      onNavigateToCategory(
                        "quoc-gia", it.id.removePrefix("/quoc-gia/").trim('/')
                      )
                    }
                  })
                InfoRow(
                  label = stringResource(R.string.year_label),
                  value = detailSheet.yearOf?.toString(),
                  onClick = {
                    detailSheet.yearOf?.let {
                      onNavigateToCategory(
                        "nam-phat-hanh", it.toString()
                      )
                    }
                  })
                InfoRow(
                  label = stringResource(R.string.duration_label), value = detailSheet.duration
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          if (!detailSheet.othername.isNullOrEmpty()) {

            Text(
              text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = TextSecondary)) {
                  append("${stringResource(R.string.other_name)}: ")
                }
                withStyle(style = SpanStyle(color = TextPrimary)) {
                  append(detailSheet.othername!!)
                }
              },
              fontSize = 14.sp,
              style = NoPaddingTextStyle,
              modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
          }

          // Tags
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            detailSheet.genre.forEach { genre ->

              Text(
                text = "#${genre.name}",
                color = Color(0xFF00D639),
                fontSize = 12.sp,
                modifier = Modifier
                  .background(DarkCard, RoundedCornerShape(4.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
                  .clickable {
                    onNavigateToCategory(
                      "the-loai", genre.id.removePrefix("/the-loai/").trim('/')
                    )
                  })
            }
          }

          Spacer(modifier = Modifier.height(16.dp))


          Text(
            text = stringResource(R.string.description),
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = detailSheet.description,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
          )

          if (detailSheet.trailer != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
              text = stringResource(R.string.trailer_title),
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
            ) {
              AndroidView(
                factory = { ctx ->
                  val webView = android.webkit.WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    webViewClient = android.webkit.WebViewClient()
                  }
                  webView.loadUrl(detailSheet.trailer!!)
                  webView
                }, modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DetailSkeleton() {
  Column(modifier = Modifier.padding(16.dp)) {
    // Title and Views
    Box(
      modifier = Modifier
        .fillMaxWidth(0.7f)
        .height(24.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Box(
      modifier = Modifier
        .width(100.dp)
        .height(14.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Author/Studio
    Row {
      Box(
        modifier = Modifier
          .width(120.dp)
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
      Spacer(modifier = Modifier.width(8.dp))
      Box(
        modifier = Modifier
          .width(80.dp)
          .height(14.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Badges
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      repeat(3) {
        Box(
          modifier = Modifier
            .width(50.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Action buttons
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
      repeat(3) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .shimmerEffect()
          )
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .width(40.dp)
              .height(10.dp)
              .clip(RoundedCornerShape(2.dp))
              .shimmerEffect()
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    ChapterSkeleton()
  }
}

@Composable
private fun ChapterSkeleton() {
  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(horizontal = 16.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(6) {
      Box(
        modifier = Modifier
          .size(width = 50.dp, height = 36.dp)
          .clip(RoundedCornerShape(4.dp))
          .shimmerEffect()
      )
    }
  }
}

fun Modifier.shimmerEffect(): Modifier = composed {
  var size by remember { mutableStateOf(IntSize.Zero) }
  val transition = rememberInfiniteTransition(label = "shimmer")
  val startOffsetX by transition.animateFloat(
    initialValue = -2 * size.width.toFloat(),
    targetValue = 2 * size.width.toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(1000)
    ),
    label = "shimmerOffsetX"
  )

  background(
    brush = Brush.linearGradient(
      colors = listOf(
        Color(0xFF1E2D4A),
        Color(0xFF2C3D5E),
        Color(0xFF1E2D4A),
      ),
      start = Offset(startOffsetX, 0f),
      end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
    )
  ).onGloballyPositioned {
    size = it.size
  }
}

@Composable
private fun InfoRow(
  label: String, value: String?, fontSize: TextUnit = 12.sp, onClick: (() -> Unit)? = null
) {
  if (!value.isNullOrEmpty()) {

    Text(
      text = buildAnnotatedString {
        withStyle(style = SpanStyle(color = TextSecondary)) {
          append("$label: ")
        }
        withStyle(style = SpanStyle(color = if (onClick != null) Color(0xFF00D639) else TextPrimary)) {
          append(value)
        }
      },
      fontSize = fontSize,
      style = DetailSmallTextStyle,
      modifier = Modifier
        .padding(vertical = 2.dp)
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier))
  }
}

@Composable
private fun Badge(text: String) {
  Box(
    modifier = Modifier
      .background(DarkCard, RoundedCornerShape(4.dp))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {

    Text(
      text = text, color = TextGrey, fontSize = 11.sp, style = NoPaddingTextStyle
    )
  }
}

@Composable
private fun ActionButton(
  icon: ImageVector, label: String, modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable { /* Action */ }
      .padding(vertical = 8.dp)) {
    Icon(icon, null, tint = TextPrimary, modifier = Modifier.size(22.dp))
    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = label,
      color = TextSecondary,
      fontSize = 11.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
private fun formatNumber(num: Int): String {
  val million = stringResource(R.string.million_suffix)
  val thousand = stringResource(R.string.thousand_suffix)
  return when {
    num >= 1_000_000 -> String.format("%.1f%s", num / 1_000_000.0, million)
    num >= 1_000 -> String.format("%.1f%s", num / 1_000.0, thousand)
    else -> num.toString()
  }
}

@Composable
private fun formatScheduleUpdate(update: Triple<Int, Int, Int>): String {
  val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
  val updateDayOfWeek =
    if (update.first == 0) 1 else update.first + 1 // Convert 0-6 to Calendar's 1-7

  val time = String.format("%02d:%02d", update.second, update.third)

  val dayText = if (updateDayOfWeek == currentDay) {
    stringResource(R.string.today_text)
  } else {
    if (updateDayOfWeek == Calendar.SUNDAY) {
      stringResource(R.string.sunday_text)
    } else {
      stringResource(R.string.day_of_week_format, updateDayOfWeek)
    }
  }

  val weekText = if (updateDayOfWeek == currentDay) {
    ""
  } else if (updateDayOfWeek > currentDay) {
    stringResource(R.string.this_week_text)
  } else {
    stringResource(R.string.next_week_text)
  }

  return stringResource(R.string.schedule_update_format, time, dayText, weekText)
}
