package git.shin.animevsub.ui.screens.about

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.BuildConfig
import git.shin.animevsub.R
import git.shin.animevsub.ui.components.dialogs.DonationDialog
import git.shin.animevsub.ui.components.dialogs.UpdateDialog
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
  onNavigateBack: () -> Unit,
  viewModel: AboutViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  var showDonationDialog by remember { mutableStateOf(false) }
  var iconClickCount by remember { mutableIntStateOf(0) }
  var showPasswordDialog by remember { mutableStateOf(false) }

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
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

      // App icon
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(DarkCard)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {
            if (uiState.isDeveloperMode) {
              iconClickCount++
              if (iconClickCount >= 7) {
                Toast.makeText(context, R.string.developer_mode_already_enabled, Toast.LENGTH_SHORT).show()
                iconClickCount = 0
              }
            } else {
              iconClickCount++
              if (iconClickCount >= 7) {
                showPasswordDialog = true
                iconClickCount = 0
              } else if (iconClickCount >= 4) {
                val stepsLeft = 7 - iconClickCount
                Toast.makeText(
                  context,
                  context.getString(R.string.developer_step_count, stepsLeft),
                  Toast.LENGTH_SHORT
                ).show()
              }
            }
          },
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = R.mipmap.ic_launcher,
          contentDescription = stringResource(R.string.app_name),
          modifier = Modifier.size(64.dp)
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
        text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
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
        value = "GNU-GPL v3"
      )
      Spacer(modifier = Modifier.height(24.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = { showDonationDialog = true },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.size(8.dp))
          Text(text = stringResource(R.string.donation_title), color = TextPrimary)
        }

        if (uiState.isCheckingUpdate) {
          CircularProgressIndicator(
            color = AccentMain,
            modifier = Modifier.size(24.dp)
          )
        } else {
          Button(
            onClick = { viewModel.checkUpdate() },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(text = stringResource(R.string.check_update), color = TextPrimary)
          }
        }
      }

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

  // Update Dialog
  uiState.updateInfo?.let { info ->
    if (info.isNewer) {
      UpdateDialog(
        info = info,
        onDismiss = { viewModel.dismissUpdate() },
        onConfirm = {
          viewModel.downloadUpdate(info)
          viewModel.dismissUpdate()
        }
      )
    }
  }

  // Donation Dialog
  if (showDonationDialog) {
    DonationDialog(
      onDismiss = { showDonationDialog = false }
    )
  }

  // Developer Password Dialog
  if (showPasswordDialog) {
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = { showPasswordDialog = false },
      title = { Text(stringResource(R.string.unlock_developer_options)) },
      text = {
        Column {
          Text(stringResource(R.string.enter_password_to_continue), fontSize = 14.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(16.dp))
          TextField(
            value = password,
            onValueChange = {
              password = it
              isError = false
            },
            placeholder = { Text(stringResource(R.string.password_hint)) },
            singleLine = true,
            isError = isError,
            colors = TextFieldDefaults.colors(
              focusedContainerColor = DarkCard,
              unfocusedContainerColor = DarkCard,
              errorContainerColor = DarkCard
            ),
            modifier = Modifier.fillMaxWidth()
          )
          if (isError) {
            Text(stringResource(R.string.wrong_password), color = Color.Red, fontSize = 12.sp)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (viewModel.enableDeveloperMode(password)) {
              showPasswordDialog = false
              Toast.makeText(context, R.string.developer_mode_enabled, Toast.LENGTH_SHORT).show()
            } else {
              isError = true
            }
          }
        ) {
          Text(stringResource(R.string.confirm))
        }
      },
      dismissButton = {
        TextButton(onClick = { showPasswordDialog = false }) {
          Text(stringResource(R.string.cancel))
        }
      }
    )
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
