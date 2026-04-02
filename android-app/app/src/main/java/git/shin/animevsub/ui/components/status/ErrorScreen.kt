package git.shin.animevsub.ui.components.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.TextSecondary

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
      text = error ?: stringResource(R.string.error_occurred),
      color = TextSecondary,
      fontSize = 14.sp
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = onRetry,
      colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
      shape = RoundedCornerShape(8.dp)
    ) {
      Text(stringResource(R.string.retry))
    }
  }
}
