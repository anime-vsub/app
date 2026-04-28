package git.shin.animevsub

import android.Manifest
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.UpdateInfo
import git.shin.animevsub.data.repository.AnimeRepository
import git.shin.animevsub.ui.AnimeVsubAppUI
import git.shin.animevsub.ui.components.dialogs.CloudflareBypassDialog
import git.shin.animevsub.ui.components.dialogs.DonationDialog
import git.shin.animevsub.ui.components.dialogs.UpdateDialog
import git.shin.animevsub.ui.navigation.Screen
import git.shin.animevsub.ui.theme.AnimeVsubTheme
import git.shin.animevsub.utils.CloudflareManager
import git.shin.animevsub.utils.UpdateManager
import git.shin.animevsub.worker.NotificationSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  // ... rest of activity ...
  fun setAppIcon(iconName: String) {
    val pm = packageManager
    val aliases = mapOf(
      "default" to ComponentName(this, "git.shin.animevsub.MainActivityDefault"),
      "old" to ComponentName(this, "git.shin.animevsub.MainActivityOld"),
      "vibrant" to ComponentName(this, "git.shin.animevsub.MainActivityVibrant"),
      "rainbow" to ComponentName(this, "git.shin.animevsub.MainActivityRainbow"),
      "neon" to ComponentName(this, "git.shin.animevsub.MainActivityNeon"),
      "ai" to ComponentName(this, "git.shin.animevsub.MainActivityAi")
    )

    aliases.forEach { (name, component) ->
      val state = if (name == iconName) {
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
      } else {
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
      }
      pm.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP)
    }
  }

  suspend fun createCustomShortcut(uri: Uri) {
    withContext(Dispatchers.IO) {
      val inputStream = contentResolver.openInputStream(uri)
      val bitmap = BitmapFactory.decodeStream(inputStream)

      if (ShortcutManagerCompat.isRequestPinShortcutSupported(this@MainActivity)) {
        val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
          action = Intent.ACTION_MAIN
        }

        val pinShortcutInfo = ShortcutInfoCompat.Builder(this@MainActivity, "custom_icon_${System.currentTimeMillis()}")
          .setShortLabel(getString(R.string.app_name))
          .setIcon(IconCompat.createWithBitmap(bitmap))
          .setIntent(intent)
          .build()

        ShortcutManagerCompat.requestPinShortcut(this@MainActivity, pinShortcutInfo, null)
      }
    }
  }

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

  companion object {
    const val ACTION_MEDIA_CONTROL = "media_control"
    const val EXTRA_CONTROL_TYPE = "control_type"
    const val CONTROL_TYPE_PLAY = 1
    const val CONTROL_TYPE_PAUSE = 2
    const val CONTROL_TYPE_NEXT = 3
  }

  private val pipReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action != ACTION_MEDIA_CONTROL) return
      val controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)
      MainScope().launch {
        when (controlType) {
          CONTROL_TYPE_PLAY -> _pipEvent.emit(PipEvent.PLAY)
          CONTROL_TYPE_PAUSE -> _pipEvent.emit(PipEvent.PAUSE)
          CONTROL_TYPE_NEXT -> _pipEvent.emit(PipEvent.NEXT)
        }
      }
    }
  }

  enum class PipEvent { PLAY, PAUSE, NEXT }
  private val _pipEvent = MutableSharedFlow<PipEvent>()
  val pipEvent = _pipEvent.asSharedFlow()

  private var isPipPlaying = false

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

    val locale = Locale.Builder().setLanguage(lang).build()
    Locale.setDefault(locale)
    val config = Configuration(newBase.resources.configuration)
    config.setLocale(locale)
    val context = newBase.createConfigurationContext(config)
    super.attachBaseContext(context)
  }

  private val intentFlow = MutableStateFlow<Intent?>(null)

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val filter = android.content.IntentFilter(ACTION_MEDIA_CONTROL)
    ContextCompat.registerReceiver(
      this,
      pipReceiver,
      filter,
      ContextCompat.RECEIVER_EXPORTED
    )
    intentFlow.value = intent
    enableEdgeToEdge()
    setContent {
      val windowSize = calculateWindowSizeClass(this)
      val context = LocalContext.current
      val updateInfo = remember { mutableStateOf<UpdateInfo?>(null) }
      val bypassUrl by cloudflareManager.bypassUrl.collectAsState()
      var isAppActive by remember { mutableStateOf(true) }
      val pipMode by isInPipMode.collectAsState()
      var showDonationDialog by remember { mutableStateOf(false) }

      val currentIntent by intentFlow.collectAsState()

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
      val dynamicColor by preferencesManager.dynamicColor.collectAsState(initial = false)

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
        val lastDonation = preferencesManager.lastDonationAlert.first()
        val hideDonation = preferencesManager.hideDonationPopup.first()
        val currentTime = System.currentTimeMillis()
        if (!hideDonation && currentTime - lastDonation > 7 * 24 * 60 * 60 * 1000L) {
          showDonationDialog = true
        }

        val lastCheck = preferencesManager.lastActiveCheck.first()
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

      AnimeVsubTheme(dynamicColor = dynamicColor) {
        Surface(modifier = Modifier.fillMaxSize()) {
          if (isAppActive) {
            val navController = rememberNavController()

            LaunchedEffect(currentIntent) {
              currentIntent?.let { intent ->
                when (intent.action) {
                  "PLAY_ANIME" -> {
                    val animeId = intent.getStringExtra("animeId")
                    val chapterId = intent.getStringExtra("chapterId")
                    if (animeId != null) {
                      navController.navigate(Screen.AnimeDetail.createRoute(animeId, chapterId))
                      intentFlow.value = null
                    }
                  }

                  "OPEN_ANIME" -> {
                    val animeId = intent.getStringExtra("animeId")
                    if (animeId != null) {
                      navController.navigate(Screen.AnimeDetail.createRoute(animeId))
                      intentFlow.value = null
                    }
                  }
                }
              }
            }

            AnimeVsubAppUI(
              animeRepository = animeRepository,
              windowSize = windowSize,
              navController = navController,
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

          if (showDonationDialog) {
            DonationDialog(
              onDismiss = {
                showDonationDialog = false
                MainScope().launch {
                  preferencesManager.setLastDonationAlert(System.currentTimeMillis())
                }
              }
            )
          }
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    intentFlow.value = intent
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
        // Just enter PiP, system will use last set parameters
        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
      } catch (e: Exception) {
        print(e)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      unregisterReceiver(pipReceiver)
    } catch (e: Exception) {
      // Ignored
    }
  }

  fun updatePipParams(isPlaying: Boolean? = null, action: (PictureInPictureParams.Builder) -> Unit) {
    if (isPlaying != null) {
      isPipPlaying = isPlaying
    }
    val builder = PictureInPictureParams.Builder()

    val actions = mutableListOf<RemoteAction>()
    val intent = Intent(ACTION_MEDIA_CONTROL).setPackage(packageName)

    if (isPipPlaying) {
      actions.add(
        RemoteAction(
          Icon.createWithResource(this, R.drawable.ic_pause),
          "Pause",
          "Pause",
          PendingIntent.getBroadcast(
            this,
            CONTROL_TYPE_PAUSE,
            Intent(intent).putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE_PAUSE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
          )
        )
      )
    } else {
      actions.add(
        RemoteAction(
          Icon.createWithResource(this, R.drawable.ic_play),
          "Play",
          "Play",
          PendingIntent.getBroadcast(
            this,
            CONTROL_TYPE_PLAY,
            Intent(intent).putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE_PLAY),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
          )
        )
      )
    }
    builder.setActions(actions)

    // Add Next button if not playing or playing
    actions.add(
      RemoteAction(
        Icon.createWithResource(this, R.drawable.ic_next),
        "Next",
        "Next Episode",
        PendingIntent.getBroadcast(
          this,
          CONTROL_TYPE_NEXT,
          Intent(intent).putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE_NEXT),
          PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
      )
    )
    builder.setActions(actions)

    action(builder)
    setPictureInPictureParams(builder.build())
  }
}
