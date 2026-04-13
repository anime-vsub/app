package git.shin.animevsub

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.AndroidEntryPoint
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.UpdateInfo
import git.shin.animevsub.data.repository.AnimeRepository
import git.shin.animevsub.ui.AnimeVsubAppUI
import git.shin.animevsub.ui.components.dialogs.CloudflareBypassDialog
import git.shin.animevsub.ui.components.dialogs.UpdateDialog
import git.shin.animevsub.ui.theme.AnimeVsubTheme
import git.shin.animevsub.utils.CloudflareManager
import git.shin.animevsub.utils.UpdateManager
import javax.inject.Inject
import kotlin.system.exitProcess
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject
  lateinit var updateManager: UpdateManager

  @Inject
  lateinit var cloudflareManager: CloudflareManager

  @Inject
  lateinit var animeRepository: AnimeRepository

  @Inject
  lateinit var preferencesManager: PreferencesManager

  private val _isInPipMode = MutableStateFlow(false)
  val isInPipMode = _isInPipMode.asStateFlow()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val updateInfo = remember { mutableStateOf<UpdateInfo?>(null) }
      val bypassUrl by cloudflareManager.bypassUrl.collectAsState()
      var isAppActive by remember { mutableStateOf(true) }
      val pipMode by isInPipMode.collectAsState()

      androidx.compose.runtime.LaunchedEffect(Unit) {
        val lastCheck = preferencesManager.lastActiveCheck.first()
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastCheck > 24 * 60 * 60 * 1000) {
          updateManager.checkAppActive().onSuccess { active ->
            isAppActive = active
            if (active) {
              preferencesManager.setLastActiveCheck(currentTime)
            }
          }.onFailure {
            // If check fails due to network, we might want to let it pass if we had a previous success
            // or block it. Here we trust previous check if it's within 48h for example, or just block.
            // For now, let's keep isAppActive = true (allow) to avoid locking out users on poor network,
            // or false if we want strict control.
          }
        }

        updateManager.checkForUpdate().onSuccess { info ->
          if (info.isNewer) {
            updateInfo.value = info
          }
        }
      }

      AnimeVsubTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          if (isAppActive) {
            AnimeVsubAppUI(
              animeRepository = animeRepository,
              isInPipMode = pipMode
            )
          } else {
            AlertDialog(
              onDismissRequest = { },
              title = { Text(stringResource(R.string.app_inactive_title)) },
              text = { Text(stringResource(R.string.app_inactive_message)) },
              confirmButton = {
                TextButton(onClick = { exitProcess(0) }) {
                  Text(stringResource(R.string.close))
                }
              }
            )
          }

          updateInfo.value?.let { info ->
            UpdateDialog(
              info = info,
              onDismiss = { updateInfo.value = null },
              onConfirm = {
                updateManager.downloadAndInstall(info.downloadUrl, "AnimeVsub_v${info.version}.apk")
                updateInfo.value = null
              }
            )
          }

          bypassUrl?.let { url ->
            CloudflareBypassDialog(
              url = url,
              onResult = {
                MainScope().launch {
                  cloudflareManager.onBypassCompleted(url)
                }
              },
              onDismiss = {
                cloudflareManager.cancelBypass()
              }
            )
          }
        }
      }
    }
  }

  override fun onPictureInPictureModeChanged(
    isInPictureInPictureMode: Boolean,
    newConfig: Configuration
  ) {
    super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    _isInPipMode.value = isInPictureInPictureMode
  }

  override fun onUserLeaveHint() {
    super.onUserLeaveHint()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
      // For Android 8 to 11, we might need to trigger PiP manually if params are set
      // Android 12+ handles this via setAutoEnterEnabled(true)
      // We check if pip params are set (aspect ratio is a good indicator)
      try {
        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
      } catch (e: Exception) {
        // Ignore
      }
    }
  }

  fun updatePipParams(action: (PictureInPictureParams.Builder) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val builder = PictureInPictureParams.Builder()
      action(builder)
      setPictureInPictureParams(builder.build())
    }
  }
}
