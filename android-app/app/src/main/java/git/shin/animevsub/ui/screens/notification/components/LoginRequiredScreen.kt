package git.shin.animevsub.ui.screens.notification.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun LoginRequiredScreen(onNavigateToLogin: () -> Unit) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        imageVector = Icons.Outlined.NotificationsNone,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = TextGrey
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = stringResource(R.string.login_required),
        color = TextSecondary,
        fontSize = 14.sp
      )
      Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = onNavigateToLogin,
        colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(stringResource(R.string.login))
      }
    }
  }
}
