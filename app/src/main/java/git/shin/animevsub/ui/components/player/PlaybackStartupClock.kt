package git.shin.animevsub.ui.components.player

import android.os.SystemClock
import android.util.Log

/**
 * Measures how long a stream takes to reach its first frame.
 *
 * Kept in its own class instead of inline logging inside [VideoPlayer]: that
 * composable is already large enough that adding statements to it can trip the
 * ART bytecode verifier on debug builds.
 *
 * Read the numbers with: `adb logcat -s VideoPlayer`
 */
internal object PlaybackStartupClock {
  private const val TAG = "VideoPlayer"

  @Volatile
  private var startedAt = 0L

  fun onPrepare(type: String, host: String?, isContent: Boolean) {
    startedAt = SystemClock.elapsedRealtime()
    Log.d(TAG, "prepare type=$type host=$host isContent=$isContent")
  }

  fun onStateReady() {
    log("ready", null)
  }

  fun onFirstFrame() {
    log("first frame", null)
  }

  fun onError(error: Throwable) {
    log("error", error)
  }

  private fun log(event: String, error: Throwable?) {
    val started = startedAt
    if (started <= 0L) return
    val elapsed = SystemClock.elapsedRealtime() - started
    if (error == null) {
      Log.d(TAG, "$event after ${elapsed}ms")
    } else {
      Log.w(TAG, "$event after ${elapsed}ms: ${error.message}", error)
    }
  }
}
