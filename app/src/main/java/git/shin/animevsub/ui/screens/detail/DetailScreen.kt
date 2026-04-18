package git.shin.animevsub.ui.screens.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.util.Rational
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.MainActivity
import git.shin.animevsub.R
import git.shin.animevsub.data.model.SelectedFilter
import git.shin.animevsub.ui.components.badge.Badge
import git.shin.animevsub.ui.components.badge.QualityBadge
import git.shin.animevsub.ui.components.common.ActionButton
import git.shin.animevsub.ui.components.detail.CommentSection
import git.shin.animevsub.ui.components.list.GridAnimeList
import git.shin.animevsub.ui.components.player.EpisodeItem
import git.shin.animevsub.ui.components.player.VideoPlayer
import git.shin.animevsub.ui.components.playlist.AddToPlaylistBottomSheet
import git.shin.animevsub.ui.components.status.ErrorScreen
import git.shin.animevsub.ui.styles.NoPaddingTextStyle
import git.shin.animevsub.ui.styles.SmallTextStyle
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.StarColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.formatNumber
import git.shin.animevsub.ui.utils.formatScheduleUpdate
import git.shin.animevsub.ui.utils.shimmerEffect
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDetail: (String, String?) -> Unit,
  onNavigateToCategory: (List<SelectedFilter>) -> Unit,
  onNavigateToLogin: () -> Unit,
  viewModel: DetailViewModel = hiltViewModel(),
  isInPipMode: Boolean = false
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }
  val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val chapterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val scaffoldState = rememberBottomSheetScaffoldState()
  var isPlayerPlaying by remember { mutableStateOf(false) }
  var showDetailSheet by remember { mutableStateOf(false) }
  var showChapterSheet by remember { mutableStateOf(false) }
  var showAddToPlaylistSheet by remember { mutableStateOf(false) }
  var isFullScreen by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
  val isLandscapeUI = isLandscape && !isFullScreen && !isInPipMode
  val screenWidth = configuration.screenWidthDp.dp
  val videoHeight = if (isLandscapeUI) configuration.screenHeightDp.dp else screenWidth * 9 / 16
  val sheetHeight =
    if (isLandscapeUI) configuration.screenHeightDp.dp else configuration.screenHeightDp.dp - videoHeight

  LaunchedEffect(uiState.playerData, isFullScreen, isPlayerPlaying) {
    val activity = context as? MainActivity ?: return@LaunchedEffect
    if (uiState.playerData != null && isPlayerPlaying) {
      activity.updatePipParams { builder ->
        builder.setAspectRatio(Rational(16, 9))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          builder.setAutoEnterEnabled(true)
        }
      }
    } else {
      activity.updatePipParams { builder ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          builder.setAutoEnterEnabled(false)
        }
      }
    }
  }

  val followSuccessMsg = stringResource(R.string.followed)
  val unfollowSuccessMsg = stringResource(R.string.unfollowed)
  val followErrorMsg = stringResource(R.string.follow_error)

  val reportSuccessMsg = stringResource(R.string.report_success)
  val reportErrorMsg = stringResource(R.string.report_error)

  val loginRequiredMsg = stringResource(R.string.login_required)

  LaunchedEffect(Unit) {
    viewModel.uiEffect.collect { effect ->
      when (effect) {
        is DetailViewModel.DetailUiEffect.ShowSnackbar -> {
          val message = when (effect.message) {
            "FOLLOW_SUCCESS" -> followSuccessMsg
            "UNFOLLOW_SUCCESS" -> unfollowSuccessMsg
            "FOLLOW_ERROR" -> followErrorMsg
            "REPORT_SUCCESS" -> reportSuccessMsg
            "REPORT_ERROR" -> reportErrorMsg
            else -> effect.message
          }
          snackbarHostState.showSnackbar(message)
        }

        is DetailViewModel.DetailUiEffect.RequireLogin -> {
          snackbarHostState.showSnackbar(loginRequiredMsg)
          onNavigateToLogin()
        }

        is DetailViewModel.DetailUiEffect.OpenPlaylistSheet -> {
          showAddToPlaylistSheet = true
        }
      }
    }
  }

  // List states for scrollingon
  val seasonListState = rememberLazyListState()

  // Store scroll states for each season to maintain separate scroll positions
  val chapterListStates = remember { mutableStateMapOf<String, LazyListState>() }
  val currentChapterListState = chapterListStates.getOrPut(uiState.activeDisplaySeasonId) {
    LazyListState()
  }

  // Scroll to current chapter only if it's the currently viewed season
  LaunchedEffect(uiState.currentChapIndex, uiState.activeDisplaySeasonId) {
    if (uiState.currentChapIndex >= 0 && uiState.currentSeasonId == uiState.animeId) {
      val activeSeason = uiState.displaySeasons.find { it.id == uiState.activeDisplaySeasonId }
      if (activeSeason?.range != null) {
        if (uiState.currentChapIndex in activeSeason.range) {
          val scrollIndex = uiState.currentChapIndex - activeSeason.range.first
          currentChapterListState.animateScrollToItem(scrollIndex)
        }
      } else {
        currentChapterListState.animateScrollToItem(uiState.currentChapIndex)
      }
    }
  }

  // Cleanup orientation on dispose
  DisposableEffect(Unit) {
    onDispose {
      val activity = context as? Activity
      activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
  }

  LaunchedEffect(uiState.animeId, uiState.detail) {
    if (uiState.detail != null) {
      viewModel.loadComments()
    }
  }

  LaunchedEffect(isFullScreen) {
    if (isFullScreen) {
      showDetailSheet = false
      showChapterSheet = false
      showAddToPlaylistSheet = false
      scaffoldState.bottomSheetState.partialExpand()
    }
  }

  BottomSheetScaffold(
    scaffoldState = scaffoldState,
    sheetContent = {
      if (!isLandscapeUI && !isFullScreen) {
        CommentSection(
          comments = uiState.comments,
          totalComments = uiState.totalComments,
          isLoading = uiState.isCommentsLoading,
          hasMore = uiState.hasMoreComments,
          onLoadMore = { viewModel.loadMoreComments() },
          onVote = { id, vote -> viewModel.voteComment(id, vote) },
          onReply = { parentId, content -> viewModel.postComment(content, parentId = parentId) },
          onEdit = { id, content -> viewModel.editComment(id, content) },
          onTrigger = { trigger -> viewModel.onCommentTrigger(trigger) },
          currentUserId = uiState.currentUser?.username.hashCode(),
          replies = uiState.replies,
          repliesHasMore = uiState.repliesHasMore,
          onLoadReplies = { id, append -> viewModel.loadReplies(id, append) },
          onPostComment = { content -> viewModel.postComment(content) },
          isPosting = uiState.isPostingComment,
          currentUserAvatar = uiState.currentUser?.avatar,
          sort = uiState.commentSort,
          sortOptions = uiState.commentSortOptions,
          onSortChange = { viewModel.updateCommentSort(it) },
          modifier = Modifier
            .fillMaxWidth()
            .height(sheetHeight)
        )
      }
    },
    sheetPeekHeight = 0.dp,
    sheetContainerColor = DarkSurface,
    sheetDragHandle = {
      if (!isLandscapeUI && !isFullScreen) {
        git.shin.animevsub.ui.components.detail.BottomSheetDragHandle()
      }
    },
    containerColor = DarkBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    PullToRefreshBox(
      isRefreshing = uiState.isRefreshing,
      onRefresh = { if (!isFullScreen) viewModel.refresh() },
      modifier = Modifier
        .fillMaxSize()
        .padding(top = if (isFullScreen) 0.dp else innerPadding.calculateTopPadding())
    ) {
      val playerArea = remember {
        movableContentOf { modifier: Modifier, state: DetailUiState ->
          val detail = state.detail
          Box(
            modifier = modifier
              .background(Color.Black)
          ) {
            if (detail != null && state.currentChapter?.id == "0" && !detail.trailer.isNullOrEmpty()) {
              // Trailer Embed mode
              AndroidView(
                factory = { ctx ->
                  android.webkit.WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    webViewClient = android.webkit.WebViewClient()
                    loadUrl(detail.trailer)
                  }
                },
                modifier = Modifier.fillMaxSize()
              )
              IconButton(
                onClick = onNavigateBack
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = null,
                  tint = Color.White
                )
              }
            } else {
              // Player mode (handles loading detail, loading link, and errors)
              val currentChap = state.chapterData?.chaps?.getOrNull(state.currentChapIndex)
              VideoPlayer(
                playerData = state.playerData,
                poster = detail?.poster ?: detail?.image,
                title = detail?.name ?: "",
                subtitle = currentChap?.name?.let {
                  if (state.episodeNameFromApi != null) "$it - ${state.episodeNameFromApi}" else it
                } ?: "",
                isLoading = state.isPlayerLoading || state.isLoading,
                errorMessage = state.playerError,
                introRange = state.introRange,
                outroRange = state.outroRange,
                autoNextEnabled = state.autoNext,
                hasNextEpisode = state.currentChapIndex + 1 < (
                  state.chapterData?.chaps?.size
                    ?: 0
                  ),
                onBack = onNavigateBack,
                onReload = { viewModel.retryPlayer() },
                onNextEpisode = { viewModel.playNext() },
                onVideoEnded = {
                  if (state.autoNext) {
                    viewModel.playNext()
                  }
                },
                servers = state.servers,
                currentServer = state.currentServer,
                onServerSelected = { viewModel.selectServer(it) },
                displaySeasons = state.displaySeasons,
                activeDisplaySeasonId = state.activeDisplaySeasonId,
                onSeasonSelected = { viewModel.setActiveDisplaySeason(it) },
                episodes = state.chapterData?.chaps ?: emptyList(),
                currentEpisode = state.currentChapter,
                onEpisodeSelected = { chap, seasonId -> viewModel.playChapter(chap, seasonId) },
                initialPosition = state.lastProgress,
                onProgressUpdate = { cur, dur -> viewModel.updateHistory(cur, dur) },
                chapterProgress = state.chapterProgress,
                isFullScreen = isFullScreen,
                onFullScreenChange = { isFullScreen = it },
                isInPipMode = isInPipMode,
                onPlayingStateChange = { isPlayerPlaying = it },
                syncMode = state.syncMode,
                onSyncModeChange = { viewModel.setSyncMode(it) },
                isEpisodesLoading = state.isChaptersLoading,
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }

      val scrollableContent = @Composable { modifier: Modifier ->
        val detail = uiState.detail
        Column(
          modifier = modifier
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
                .clickable { showDetailSheet = true }
                .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
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
                  style = NoPaddingTextStyle
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
                  fontSize = 14.sp,
                  style = NoPaddingTextStyle
                )

                uiState.chapterData?.update?.let { update ->
                  Text(
                    text = " • ",
                    color = TextGrey,
                    fontSize = 14.sp,
                    style = NoPaddingTextStyle
                  )

                  Text(
                    text = formatScheduleUpdate(update),
                    color = AccentMain,
                    fontSize = 14.sp,
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
                      text = stringResource(R.string.author_label) + " ",
                      color = TextGrey,
                      fontSize = 14.sp,
                      style = NoPaddingTextStyle
                    )

                    Text(
                      text = detail.authors.first().name,
                      color = if (detail.authors.first().filters.isNotEmpty()) MainColor else TextPrimary,
                      fontSize = 14.sp,
                      style = NoPaddingTextStyle,
                      modifier = Modifier.clickable(enabled = detail.authors.first().filters.isNotEmpty()) {
                        onNavigateToCategory(detail.authors.first().filters)
                      }
                    )

                    Text(
                      text = " | ",
                      color = TextGrey,
                      fontSize = 14.sp,
                      style = NoPaddingTextStyle
                    )
                  }

                  Text(
                    text = stringResource(
                      R.string.studio_prefix,
                      detail.studio?.name ?: stringResource(R.string.unknown)
                    ),
                    color = if (detail.studio != null && detail.studio.filters.isNotEmpty()) MainColor else TextGrey,
                    fontSize = 14.sp,
                    style = NoPaddingTextStyle,
                    modifier = Modifier.clickable(enabled = detail.studio != null && detail.studio.filters.isNotEmpty()) {
                      detail.studio?.let {
                        onNavigateToCategory(it.filters)
                      }
                    }
                  )
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
                    Badge(
                      text = detail.yearOf.name,
                      textStyle = NoPaddingTextStyle,
                      modifier = Modifier.clickable(enabled = detail.yearOf.filters.isNotEmpty()) {
                        onNavigateToCategory(detail.yearOf.filters)
                      }
                    )
                  }
                  if (!detail.duration.isNullOrEmpty()) {
                    Badge(
                      text = stringResource(R.string.updated_to_episode, detail.duration),
                      textStyle = NoPaddingTextStyle
                    )
                  }
                  if (detail.countries.isNotEmpty()) {
                    Text(
                      text = detail.countries.first().name,
                      color = if (detail.countries.first().filters.isNotEmpty()) MainColor else TextPrimary,
                      fontSize = 14.sp,
                      style = NoPaddingTextStyle,
                      modifier = Modifier.clickable(enabled = detail.countries.first().filters.isNotEmpty()) {
                        onNavigateToCategory(detail.countries.first().filters)
                      }
                    )
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    style = NoPaddingTextStyle
                  )
                  Icon(Icons.Default.Star, null, tint = StarColor, modifier = Modifier.size(14.dp))

                  Spacer(modifier = Modifier.width(8.dp))

                  Text(
                    text = stringResource(R.string.rating_count, formatNumber(detail.countRate)),
                    color = TextGrey,
                    fontSize = 14.sp,
                    style = NoPaddingTextStyle
                  )
                  detail.seasonOf?.let {
                    Text(
                      text = " | ",
                      color = TextGrey,
                      fontSize = 14.sp,
                      style = NoPaddingTextStyle
                    )

                    Text(
                      text = it.name,
                      color = if (it.filters.isNotEmpty()) MainColor else TextPrimary,
                      fontSize = 14.sp,
                      style = NoPaddingTextStyle,
                      modifier = Modifier.clickable(enabled = it.filters.isNotEmpty()) {
                        onNavigateToCategory(it.filters)
                      }
                    )
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
                      color = if (genre.filters.isNotEmpty()) MainColor else TextSecondary,
                      fontSize = 14.sp,
                      style = SmallTextStyle,
                      modifier = Modifier
                        .padding(vertical = 1.dp)
                        .clickable(enabled = genre.filters.isNotEmpty()) {
                          onNavigateToCategory(genre.filters)
                        }
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                LazyRow(
                  modifier = Modifier
                    .fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                  item {
                    ActionButton(
                      icon = if (uiState.isFollowed) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                      label = formatNumber(detail.follows + (if (uiState.isFollowed) 1 else 0)),
                      iconTint = if (uiState.isFollowed) StarColor else TextPrimary,
                      onClick = { viewModel.toggleFollow() }
                    )
                  }
                  item {
                    ActionButton(
                      icon = Icons.Default.Share,
                      label = stringResource(R.string.share),
                      onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                          type = "text/plain"
                          val shareMessage = context.getString(
                            R.string.share_message,
                            detail.name,
                            "https://github.com/anime-vsub/app"
                          )
                          putExtra(Intent.EXTRA_TEXT, shareMessage)
                        }
                        context.startActivity(
                          Intent.createChooser(
                            shareIntent,
                            context.getString(R.string.share_title)
                          )
                        )
                      }
                    )
                  }
                  item {
                    ActionButton(
                      icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                      label = stringResource(R.string.save_label),
                      onClick = { viewModel.onSaveClick() }
                    )
                  }
                }
              }

              // Server Section
              if (uiState.servers.size >= 2) {
                Text(
                  text = stringResource(R.string.server_label),
                  color = TextPrimary,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                  items(uiState.servers) { server ->
                    val isSelected = server == uiState.currentServer
                    val boxModifier = Modifier
                      .height(36.dp)
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (isSelected) MainColor.copy(alpha = 0.15f) else DarkCard)
                      .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) MainColor else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                      )
                      .clickable { viewModel.selectServer(server) }

                    Box(
                      modifier = boxModifier,
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = server.name,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = if (isSelected) MainColor else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                  }
                }
              }

              // Episode Section Header
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  modifier = Modifier
                    .weight(1f)
                    .clickable { showChapterSheet = true },
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = stringResource(R.string.episodes),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextGrey,
                    modifier = Modifier.size(20.dp)
                  )
                }

                IconButton(onClick = { viewModel.toggleSyncMode() }) {
                  Icon(
                    imageVector = when (uiState.syncMode) {
                      1 -> Icons.Default.Upload
                      2 -> Icons.Default.SyncDisabled
                      else -> Icons.Default.Sync
                    },
                    contentDescription = "Sync Mode",
                    tint = when (uiState.syncMode) {
                      1 -> Color(0xFF4CAF50)
                      2 -> Color(0xFFF44336)
                      else -> MainColor
                    },
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              // Episode List (Horizontal)
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
                    text = uiState.chaptersError ?: stringResource(R.string.error_loading_episodes),
                    color = TextSecondary,
                    fontSize = 13.sp
                  )
                  TextButton(onClick = { viewModel.retry() }) {
                    Text(stringResource(R.string.retry), color = AccentMain)
                  }
                }
              } else {
                val chapterData = uiState.chapterData
                if (chapterData != null && chapterData.chaps.isNotEmpty()) {
                  val activeSeason =
                    uiState.displaySeasons.find { it.id == uiState.activeDisplaySeasonId }
                  val displayChaps = if (activeSeason?.range != null) {
                    chapterData.chaps.slice(activeSeason.range.filter { it < chapterData.chaps.size })
                  } else {
                    chapterData.chaps
                  }

                  LazyRow(
                    state = currentChapterListState,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                  ) {
                    items(displayChaps.size) { index ->
                      val chap = displayChaps[index]
                      val originalIndex =
                        if (activeSeason?.range != null) activeSeason.range.first + index else index
                      val isSelected =
                        originalIndex == uiState.currentChapIndex &&
                          uiState.currentSeasonId == (
                            activeSeason?.realId
                              ?: uiState.currentSeasonId
                            )

                      val progress = uiState.chapterProgress[chap.id]

                      EpisodeItem(
                        chap = chap,
                        isSelected = isSelected,
                        progress = progress,
                        onClick = {
                          viewModel.playChapter(
                            chap,
                            activeSeason?.realId ?: uiState.currentSeasonId
                          )
                        }
                      )
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
              if (uiState.displaySeasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                // Find current season index for scrolling
                val currentSeasonIndex = uiState.displaySeasons.indexOfFirst {
                  it.id == uiState.activeDisplaySeasonId
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
                  items(uiState.displaySeasons) { season ->
                    val isCurrent = season.id == uiState.activeDisplaySeasonId

                    Box(
                      modifier = Modifier
                        .widthIn(min = 100.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isCurrent) MainColor.copy(alpha = 0.15f) else DarkCard)
                        .border(
                          width = if (isCurrent) 1.5.dp else 1.dp,
                          color = if (isCurrent) MainColor else Color.Transparent,
                          shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                          viewModel.setActiveDisplaySeason(season.id)
                        },
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = season.name,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = if (isCurrent) MainColor else TextSecondary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                  }
                }
              }
            }

            // Comment Preview (YouTube-style)
            if (!isLandscapeUI) {
              Spacer(modifier = Modifier.height(16.dp))
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant)
                  .clickable {
                    scope.launch {
                      scaffoldState.bottomSheetState.expand()
                    }
                  }
                  .padding(12.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = stringResource(R.string.comments_title),
                      color = TextPrimary,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "${uiState.totalComments}",
                      color = TextSecondary,
                      fontSize = 14.sp
                    )
                  }
                  Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                  )
                }

                val previewComment =
                  uiState.comments.firstOrNull { !it.isPinned && !it.isGlobalPinned }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  AsyncImage(
                    model = previewComment?.userAvatar ?: uiState.currentUser?.avatar,
                    contentDescription = null,
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape),
                    contentScale = ContentScale.Crop
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = previewComment?.content ?: stringResource(R.string.comment_hint),
                    color = if (previewComment != null) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
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
                items = detail.related,
                columns = if (isLandscapeUI) 2 else 3,
                onItemClick = { anime -> onNavigateToDetail(anime.animeId, anime.lastEpisode?.id) }
              )
            }
          }

          if (isLandscapeUI) {
            CommentSection(
              comments = uiState.comments,
              totalComments = uiState.totalComments,
              isLoading = uiState.isCommentsLoading,
              hasMore = uiState.hasMoreComments,
              onLoadMore = { viewModel.loadMoreComments() },
              onVote = { id, vote -> viewModel.voteComment(id, vote) },
              onReply = { parentId, content ->
                viewModel.postComment(
                  content,
                  parentId = parentId
                )
              },
              onEdit = { id, content -> viewModel.editComment(id, content) },
              onTrigger = { trigger -> viewModel.onCommentTrigger(trigger) },
              currentUserId = uiState.currentUser?.username.hashCode(),
              replies = uiState.replies,
              repliesHasMore = uiState.repliesHasMore,
              onLoadReplies = { id, append -> viewModel.loadReplies(id, append) },
              onPostComment = { content -> viewModel.postComment(content) },
              isPosting = uiState.isPostingComment,
              currentUserAvatar = uiState.currentUser?.avatar,
              sort = uiState.commentSort,
              sortOptions = uiState.commentSortOptions,
              onSortChange = { viewModel.updateCommentSort(it) },
              modifier = Modifier
                .fillMaxWidth()
            )
          }

          Spacer(modifier = Modifier.height(50.dp))
        }
      }

      if (isFullScreen) {
        playerArea(Modifier.fillMaxSize(), uiState)
      } else if (isLandscapeUI) {
        Row(modifier = Modifier.fillMaxSize()) {
          playerArea(
            Modifier
              .weight(0.6f)
              .fillMaxHeight(),
            uiState
          )
          scrollableContent(
            Modifier
              .weight(0.4f)
              .fillMaxHeight()
          )
        }
      } else {
        Column(modifier = Modifier.fillMaxSize()) {
          playerArea(
            Modifier
              .fillMaxWidth()
              .aspectRatio(16f / 9f),
            uiState
          )
          scrollableContent(
            Modifier
              .weight(1f)
              .fillMaxWidth()
          )
        }
      }
    }

    // Detail Bottom Sheet
    if (showDetailSheet && uiState.detail != null) {
      DetailBottomSheet(
        detail = uiState.detail!!,
        sheetState = detailSheetState,
        onDismissRequest = { showDetailSheet = false },
        onNavigateToCategory = { categoryLink ->
          onNavigateToCategory(categoryLink.filters)
        }
      )
    }

    // Chapter Grid Bottom Sheet
    if (showChapterSheet) {
      ChapterBottomSheet(
        uiState = uiState,
        sheetState = chapterSheetState,
        onDismissRequest = { showChapterSheet = false },
        onSeasonClick = { viewModel.setActiveDisplaySeason(it) },
        onChapterClick = { chap, seasonId -> viewModel.playChapter(chap, seasonId) },
        onSyncModeToggle = { viewModel.toggleSyncMode() }
      )
    }

    if (showAddToPlaylistSheet) {
      AddToPlaylistBottomSheet(
        animeId = uiState.currentSeasonId,
        onDismissRequest = { showAddToPlaylistSheet = false },
        onTogglePlaylist = { playlistId, checked ->
          if (checked) {
            viewModel.addToPlaylist(playlistId)
          } else {
            viewModel.removeFromPlaylist(playlistId)
          }
        }
      )
    }
  }
}

@Composable
private fun DetailSkeleton() {
  Column(modifier = Modifier.padding(16.dp)) {
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

    Spacer(modifier = Modifier.height(24.dp))

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
