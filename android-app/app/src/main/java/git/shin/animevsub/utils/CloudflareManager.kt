package git.shin.animevsub.utils

import android.webkit.CookieManager
import git.shin.animevsub.data.local.ApiStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudflareManager @Inject constructor(
  private val storage: ApiStorage
) {
  private val _bypassUrl = MutableStateFlow<String?>(null)
  val bypassUrl = _bypassUrl.asStateFlow()

  private val mutex = Mutex()
  private var currentDeferred: CompletableDeferred<Boolean>? = null

  suspend fun startBypass(url: String): Boolean {
    val deferred = mutex.withLock {
      if (_bypassUrl.value != null) {
        currentDeferred
      } else {
        val newDeferred = CompletableDeferred<Boolean>()
        currentDeferred = newDeferred
        _bypassUrl.value = url
        newDeferred
      }
    } ?: return false

    val success = deferred.await()

    mutex.withLock {
      if (currentDeferred == deferred) {
        _bypassUrl.value = null
        currentDeferred = null
      }
    }
    return success
  }

  suspend fun onBypassCompleted(url: String) {
    val cookies = CookieManager.getInstance().getCookie(url)
    if (cookies != null) {
      mergeCookies(cookies)
      currentDeferred?.complete(true)
    } else {
      currentDeferred?.complete(false)
    }
  }

  /**
   * Merge new cookies into the store.
   * @param newCookies Can be a string like "a=1; b=2" (from WebView)
   * or a list of "Set-Cookie" headers from the server.
   */
  suspend fun mergeCookies(newCookies: Any?) {
    if (newCookies == null) return

    val currentCookies = storage.get("user_cookie") ?: ""
    val cookieMap = mutableMapOf<String, String>()

    // Parse current cookies
    parseCookieString(currentCookies, cookieMap)

    when (newCookies) {
      is String -> {
        // Parse from string (usually WebView.getCookie)
        parseCookieString(newCookies, cookieMap)
      }

      is List<*> -> {
        // Parse from Set-Cookie headers list
        newCookies.filterIsInstance<String>().forEach { header ->
          // Set-Cookie: name=value; Path=/; HttpOnly -> only get name=value
          val cookiePart = header.split(";")[0]
          val parts = cookiePart.split("=", limit = 2)
          if (parts.size == 2) {
            cookieMap[parts[0].trim()] = parts[1].trim()
          }
        }
      }
    }

    val merged = cookieMap.map { "${it.key}=${it.value}" }.joinToString("; ")
    storage.set("user_cookie", merged)
  }

  private fun parseCookieString(s: String, map: MutableMap<String, String>) {
    s.split(";").forEach {
      val parts = it.split("=", limit = 2)
      if (parts.size == 2) {
        map[parts[0].trim()] = parts[1].trim()
      }
    }
  }

  fun cancelBypass() {
    currentDeferred?.complete(false)
  }

  suspend fun fetch(
    client: OkHttpClient,
    request: Request,
    retryCount: Int = 0
  ): Response = withContext(Dispatchers.IO) {
    val builder = request.newBuilder()
    val currentCookie = storage.get("user_cookie")
    if (!currentCookie.isNullOrEmpty() && request.header("Cookie") == null) {
      builder.header("Cookie", currentCookie)
    }

    val response = client.newCall(builder.build()).execute()
    mergeCookies(response.headers("Set-Cookie"))

    // Check for Cloudflare Challenge by peeking the body to avoid consuming the original stream
    val peekBody = response.peekBody(1024 * 100).string() // Check within the first 100KB

    if (isCloudflareChallenge(response, peekBody)) {
      if (retryCount < 1) {
        response.close()
        if (startBypass(request.url.toString())) {
          return@withContext fetch(client, request, retryCount + 1)
        }
      }
    }

    response
  }

  private fun isCloudflareChallenge(response: Response, body: String): Boolean {
    val hasCfHeaders = response.code in 403..503 || body.contains("cf-challenge") || body.contains("ray-id")
    val hasKeywords = body.contains("<title>Just a moment...</title>") ||
                      body.contains("Xác Minh An Toàn") || // Safe Verification
                      body.contains("cf-browser-verification")

    return hasCfHeaders && hasKeywords
  }
}
