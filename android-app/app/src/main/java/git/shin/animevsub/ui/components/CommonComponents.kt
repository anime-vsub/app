package git.shin.animevsub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.data.model.AnimeCard
import git.shin.animevsub.ui.theme.*

@Composable
fun SectionHeader(
    title: String,
    onViewAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(if (onViewAll != null) Modifier.clickable(onClick = onViewAll) else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        if (onViewAll != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextGrey,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun HorizontalAnimeList(
    items: List<AnimeCard>,
    onItemClick: (AnimeCard) -> Unit,
    modifier: Modifier = Modifier,
    showTrending: Boolean = false
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { index ->
            val item = items[index]
            AnimeCardItem(
                anime = item,
                onClick = { onItemClick(item) },
                trendingIndex = if (showTrending) index + 1 else null
            )
        }
    }
}

@Composable
fun GridAnimeList(
    items: List<AnimeCard>,
    onItemClick: (AnimeCard) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        items.forEach { item ->
            AnimeGridCard(
                anime = item,
                onClick = { onItemClick(item) }
            )
            if (item != items.last()) {
                Divider(
                    color = DarkCard,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😔",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error ?: "An error occurred",
            color = TextSecondary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AccentMain)
    }
}

@Composable
fun QualityBadge(
    quality: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = quality,
        color = androidx.compose.ui.graphics.Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(
                AccentMain.copy(alpha = 0.85f),
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
