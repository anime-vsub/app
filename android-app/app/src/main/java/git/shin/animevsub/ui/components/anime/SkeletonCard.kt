package git.shin.animevsub.ui.components.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import git.shin.animevsub.ui.theme.DarkCard

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier.width(110.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkCard)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkCard)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkCard)
        )
    }
}
