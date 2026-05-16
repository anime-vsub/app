package git.shin.animevsub

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance

class FirebaseInitializer : Initializer<Unit> {
  override fun create(context: Context) {
    val crashlytics = FirebaseCrashlytics.getInstance()
    crashlytics.isCrashlyticsCollectionEnabled = true
    crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
    crashlytics.setCustomKey("version_code", BuildConfig.VERSION_CODE)

    FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
