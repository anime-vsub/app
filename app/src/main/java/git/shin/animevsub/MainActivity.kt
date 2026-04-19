package git.shin.animevsub

import android.Manifest
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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
import git.shin.animevsub.worker.NotificationSyncWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.system.exitProcess

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

  override fun attachBaseContext(newBase: Context) {
    val prefs = PreferencesManager(newBase)
    val lang = runCatching {
      kotlinx.coroutines.runBlocking {
        prefs.appLanguage.first()
      }
    }.getOrDefault("auto")

    if (lang == "auto") {
      super.attachBaseContext(newBase)
      return
    }

    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration(newBase.resources.configuration)
    config.setLocale(locale)
    val context = newBase.createConfigurationContext(config)
    super.attachBaseContext(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val context = LocalContext.current
      val updateInfo = remember { mutableStateOf<UpdateInfo?>(null) }
      val bypassUrl by cloudflareManager.bypassUrl.collectAsState()
      var isAppActive by remember { mutableStateOf(true) }
      val pipMode by isInPipMode.collectAsState()

      var hasNotificationPermission by remember {
        mutableStateOf(
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
              context,
              Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
          } else {
            true
          }
        )
      }

      val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
      ) { isGranted ->
        hasNotificationPermission = isGranted
      }

      val appLanguage by preferencesManager.appLanguage.collectAsState(initial = null)
      LaunchedEffect(appLanguage) {
        appLanguage?.let { lang ->
          val currentConfigLang = resources.configuration.locales[0].language
          val systemLang = Locale.getDefault().language
          val targetLang = if (lang == "auto") systemLang else lang

          if (targetLang != currentConfigLang) {
            recreate()
          }
        }
      }

      LaunchedEffect(Unit) {
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      }

      LaunchedEffect(Unit) {
        val lastCheck = preferencesManager.lastActiveCheck.first()
        val currentTime = System.currentTimeMillis()

        launch {
          combine(
            animeRepository.notifyInterval,
            animeRepository.enableBackgroundSync
          ) { interval, enabled ->
            interval to enabled
          }.collectLatest { (interval, enabled) ->
            if (!enabled) {
              WorkManager.getInstance(applicationContext).cancelUniqueWork("NotificationSync")
              return@collectLatest
            }

            // Minimum interval allowed by WorkManager is 15 minutes
            val syncInterval = interval.coerceAtLeast(15).toLong()

            val syncRequest = PeriodicWorkRequestBuilder<NotificationSyncWorker>(
              syncInterval, TimeUnit.MINUTES,
              5, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
              "NotificationSync",
              ExistingPeriodicWorkPolicy.UPDATE,
              syncRequest
            )
          }
        }

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
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
      // For Android 8 to 11, we might need to trigger PiP manually if params are set
      // Android 12+ handles this via setAutoEnterEnabled(true)
      // We check if pip params are set (aspect ratio is a good indicator)
      try {
        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
      } catch (e: Exception) {
        print(e)
      }
    }
  }

  fun updatePipParams(action: (PictureInPictureParams.Builder) -> Unit) {
    val builder = PictureInPictureParams.Builder()
    action(builder)
    setPictureInPictureParams(builder.build())
  }
}
