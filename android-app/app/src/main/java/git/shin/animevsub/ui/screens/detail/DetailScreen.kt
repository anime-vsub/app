package git.shin.animevsub.ui.screens.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.ui.components.*
import git.shin.animevsub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (String, String, String, String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> LoadingScreen()
        uiState.error != null && uiState.detail == null -> {
            ErrorScreen(error = uiState.error, onRetry = { viewModel.retry() })
        }
        uiState.detail != null -> {
            val detail = uiState.detail!!
            val chapterData = uiState.chapterData

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Poster header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                ) {
                    AsyncImage(
                        model = detail.poster ?: detail.image,
                        contentDescription = detail.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        DarkBackground.copy(alpha = 0.3f),
                                        DarkBackground.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    // Back button
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Title and info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Thumbnail
                            AsyncImage(
                                model = detail.image,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(90.dp)
                                    .aspectRatio(2f / 3f)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = detail.name,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (!detail.othername.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = detail.othername,
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = StarColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${detail.rate}/10",
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                    if (!detail.quality.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.width(12.dp))
                                        QualityBadge(quality = detail.quality)
                                    }
                                }
                            }
                        }
                    }
                }

                // Watch button
                if (detail.pathToView != null && chapterData != null && chapterData.chaps.isNotEmpty()) {
                    val firstChap = chapterData.chaps.first()
                    Button(
                        onClick = {
                            onNavigateToPlayer(
                                uiState.animeId,
                                firstChap.id,
                                firstChap.play,
                                firstChap.hash
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.watch_now),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Info rows
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (!detail.status.isNullOrEmpty()) {
                        InfoRow(stringResource(R.string.status_label), detail.status)
                    }
                    if (detail.genre.isNotEmpty()) {
                        InfoRow(
                            stringResource(R.string.genre_label),
                            detail.genre.joinToString(", ") { it.name }
                        )
                    }
                    if (!detail.studio.isNullOrEmpty()) {
                        InfoRow(stringResource(R.string.studio_label), detail.studio)
                    }
                    if (detail.countries.isNotEmpty()) {
                        InfoRow(
                            stringResource(R.string.country_label),
                            detail.countries.joinToString(", ") { it.name }
                        )
                    }
                    if (!detail.duration.isNullOrEmpty()) {
                        InfoRow(stringResource(R.string.duration_label), detail.duration)
                    }
                    if (detail.yearOf != null) {
                        InfoRow(stringResource(R.string.year_label), detail.yearOf.toString())
                    }
                    InfoRow(stringResource(R.string.views_label), formatNumber(detail.views))
                    InfoRow(stringResource(R.string.followers_label), formatNumber(detail.follows))
                }

                // Description
                if (detail.description.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = stringResource(R.string.description),
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = detail.description,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            maxLines = if (expanded) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    }
                }

                // Episodes
                if (chapterData != null && chapterData.chaps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${stringResource(R.string.episodes)} (${chapterData.chaps.size})",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ChapterGrid(
                        chapters = chapterData.chaps,
                        animeId = uiState.animeId,
                        onChapterClick = { chap ->
                            onNavigateToPlayer(
                                uiState.animeId,
                                chap.id,
                                chap.play,
                                chap.hash
                            )
                        }
                    )
                }

                // Related anime
                if (detail.related.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(title = stringResource(R.string.related))
                    HorizontalAnimeList(
                        items = detail.related,
                        onItemClick = { anime ->
                            onNavigateToDetail(anime.animeId)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            color = TextGrey,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ChapterGrid(
    chapters: List<ChapterInfo>,
    animeId: String,
    onChapterClick: (ChapterInfo) -> Unit
) {
    val rows = (chapters.size + 3) / 4
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until 4) {
                    val index = row * 4 + col
                    if (index < chapters.size) {
                        val chap = chapters[index]
                        OutlinedButton(
                            onClick = { onChapterClick(chap) },
                            border = BorderStroke(1.dp, AccentMain.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = chap.name,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private fun formatNumber(num: Int): String {
    return when {
        num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000.0)
        num >= 1_000 -> String.format("%.1fK", num / 1_000.0)
        else -> num.toString()
    }
}
