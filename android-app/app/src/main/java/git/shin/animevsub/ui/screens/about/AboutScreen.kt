package git.shin.animevsub.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
  onNavigateBack: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.about),
            color = TextPrimary
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back),
              tint = TextPrimary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
      )
    },
    containerColor = DarkBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(32.dp))

      // App icon placeholder
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(AccentMain),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "AV",
          color = TextPrimary,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = stringResource(R.string.app_name),
        color = TextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(R.string.version, "2.0.0"),
        color = TextSecondary,
        fontSize = 14.sp
      )

      Spacer(modifier = Modifier.height(32.dp))

      // About card
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkCard)
          .padding(20.dp)
      ) {
        Text(
          text = stringResource(R.string.about_description),
          color = TextSecondary,
          fontSize = 14.sp,
          lineHeight = 22.sp,
          textAlign = TextAlign.Center
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Info cards
      InfoCard(
        title = stringResource(R.string.developer),
        value = stringResource(R.string.developer_name)
      )

      InfoCard(
        title = stringResource(R.string.source_code),
        value = "github.com/anime-vsub/app"
      )

      InfoCard(
        title = stringResource(R.string.license),
        value = stringResource(R.string.mit_license)
      )

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = stringResource(R.string.made_with_love),
        color = TextGrey,
        fontSize = 12.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Composable
private fun InfoCard(
  title: String,
  value: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(DarkCard)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      color = TextGrey,
      fontSize = 13.sp
    )
    Text(
      text = value,
      color = TextPrimary,
      fontSize = 13.sp
    )
  }
}
