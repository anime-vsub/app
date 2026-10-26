package git.shin.animevsub.utils

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Suspending variant of [Call.execute] that cancels the underlying HTTP call
 * when the coroutine is cancelled (e.g. by `withTimeoutOrNull`).
 */
suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
  continuation.invokeOnCancellation { cancel() }
  enqueue(object : Callback {
    override fun onResponse(call: Call, response: Response) {
      continuation.resume(response) { _, _, _ -> response.close() }
    }

    override fun onFailure(call: Call, e: IOException) {
      if (continuation.isCancelled) return
      continuation.resumeWithException(e)
    }
  })
}
