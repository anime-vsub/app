package git.shin.animevsub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import git.shin.animevsub.data.model.UpdateInfo
import git.shin.animevsub.ui.AnimeVsubAppUI
import git.shin.animevsub.ui.components.dialogs.CloudflareBypassDialog
import git.shin.animevsub.ui.components.dialogs.UpdateDialog
import git.shin.animevsub.ui.theme.AnimeVsubTheme
import git.shin.animevsub.utils.CloudflareManager
import git.shin.animevsub.utils.UpdateManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject
  lateinit var updateManager: UpdateManager

  @Inject
  lateinit var cloudflareManager: CloudflareManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val updateInfo = remember { mutableStateOf<UpdateInfo?>(null) }
      val bypassUrl by cloudflareManager.bypassUrl.collectAsState()

      LaunchedEffect(Unit) {
        updateManager.checkForUpdate().onSuccess { info ->
          if (info.isNewer) {
            updateInfo.value = info
          }
        }
      }

      AnimeVsubTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          AnimeVsubAppUI()

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
}
