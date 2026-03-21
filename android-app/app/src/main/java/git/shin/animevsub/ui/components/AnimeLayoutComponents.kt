package git.shin.animevsub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.data.model.AnimeCard
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HorizontalAnimeList(
    items: List<AnimeCard>,
    onItemClick: (AnimeCard) -> Unit,
    modifier: Modifier = Modifier,
    showTrending: Boolean = false,
    showRating: Boolean = false,
    showTimeline: Boolean = false,
    state: LazyListState = rememberLazyListState()
) {
    val scope = rememberCoroutineScope()
    val now = LocalDateTime.now()
    val locale = LocalConfiguration.current.locales[0]

    LazyRow(
        state = state,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { index ->
            val anime = items[index]
            val card = @Composable {
                AnimeCardItem(
                    anime = anime,
                    onClick = { onItemClick(anime) },
                    trendingIndex = if (showTrending) index + 1 else null,
                    showRating = showRating,
                    modifier = Modifier.width(110.dp)
                )
            }

            if (showTimeline) {
                val timeRelease = try {
                    anime.timeRelease?.let {
                      LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(it),
                        ZoneId.systemDefault()
                      )
                    }
                } catch (e: Exception) { null }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Time information - Set min height to avoid shifting
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(110.dp)
                            .height(35.dp) // Fixed height to standardize row height
                            .clickable { scope.launch { state.animateScrollToItem(index) } }
                    ) {
                        if (timeRelease != null) {
                            val isToday = timeRelease.toLocalDate().isEqual(now.toLocalDate())
                            val isTomorrow = timeRelease.toLocalDate().isEqual(now.toLocalDate().plusDays(1))

                            if (isToday || isTomorrow) {
                                Text(text = timeRelease.format(DateTimeFormatter.ofPattern("HH:mm")), fontSize = 12.sp, color = Color.White)
                                Text(text = stringResource(if (isToday) R.string.today else R.string.tomorrow), fontSize = 10.sp, color = Color.Gray)
                            } else {
                                val pattern = if (timeRelease.year == now.year) "MM-dd" else "yyyy-MM-dd"
                                Text(text = timeRelease.format(DateTimeFormatter.ofPattern(pattern)), fontSize = 12.sp, color = Color.White)
                                Text(text = timeRelease.format(DateTimeFormatter.ofPattern("EEEE", locale)), fontSize = 10.sp, color = Color.Gray)
                            }
                        } else {
                            Text(text = stringResource(id = R.string.upcoming), fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Dot timeline with horizontal line
                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        // Horizontal line to the left of the dot
//                        if (index > 0) {
//                            Box(modifier = Modifier.width(55.dp).height(2.dp).background(Color.Gray))
//                        } else {
//                            Spacer(modifier = Modifier.width(55.dp))
//                        }

                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Gray))

                        // Horizontal line to the right of the dot
//                        if (index < items.size - 1) {
//                            Box(modifier = Modifier.width(55.dp).height(2.dp).background(Color.Gray))
//                        } else {
//                            Spacer(modifier = Modifier.width(50.dp))
//                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // The Card
                    card()
                }
            } else {
                card()
            }
        }
    }
}

@Composable
fun GridAnimeList(
    items: List<AnimeCard>,
    onItemClick: (AnimeCard) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
    ) {
        val chunkedItems = items.chunked(3)
        chunkedItems.forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    AnimeCardItem(
                        anime = item,
                        onClick = { onItemClick(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if row is not full
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (rowIndex < chunkedItems.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
