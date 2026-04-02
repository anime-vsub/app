package git.shin.animevsub.ui.screens.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun NewsScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Title bar
    Text(
      text = stringResource(R.string.news),
      color = TextPrimary,
      fontSize = 20.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    )

    // Placeholder content
    Box(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "📰",
          fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = stringResource(R.string.no_news),
          color = TextSecondary,
          fontSize = 14.sp
        )
      }
    }
  }
}
