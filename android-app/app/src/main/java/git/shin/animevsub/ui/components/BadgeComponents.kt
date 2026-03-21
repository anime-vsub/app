package git.shin.animevsub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.*

@Composable
fun QualityBadge(
    quality: String,
    modifier: Modifier = Modifier,
    isCarousel: Boolean = false
) {
    val backgroundModifier = if (isCarousel) {
        Modifier.background(
            AccentMain.copy(alpha = 0.85f),
            RoundedCornerShape(4.dp)
        )
    } else {
        Modifier.background(
            Brush.horizontalGradient(listOf(Color(0xFF00D639), Color(0xFF00C234))),
            RoundedCornerShape(4.dp)
        )
    }

    Text(
        text = quality,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .then(backgroundModifier)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
