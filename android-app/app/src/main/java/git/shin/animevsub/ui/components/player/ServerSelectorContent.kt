package git.shin.animevsub.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.data.model.ServerInfo
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextPrimary
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ServerSelectorContent(
    servers: List<ServerInfo>,
    currentServer: ServerInfo?,
    onServerClick: (ServerInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(servers) { server ->
            val isSelected = server == currentServer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MainColor.copy(alpha = 0.15f) else DarkCard)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) MainColor else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onServerClick(server) },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = server.name,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = if (isSelected) MainColor else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
