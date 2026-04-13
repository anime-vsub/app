package git.shin.animevsub.utils

import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import javax.inject.Inject
import javax.inject.Singleton
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

@Singleton
class CloudflareManager @Inject constructor(
  @param:dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) {
  private val _bypassUrl = MutableStateFlow<String?>(null)
  val bypassUrl = _bypassUrl.asStateFlow()

  private val mutex = Mutex()
  private var currentDeferred: CompletableDeferred<Boolean>? = null

  private val userAgent =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36"

  suspend fun startBypass(url: String): Boolean {
    // Try headless first
    if (tryHeadlessBypass(url)) {
      return true
    }

    // Fallback to dialog if headless fails
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

  private suspend fun tryHeadlessBypass(url: String): Boolean = withContext(Dispatchers.Main) {
    val deferred = CompletableDeferred<Boolean>()
    val handler = Handler(Looper.getMainLooper())

    val webView = WebView(context).apply {
      settings.javaScriptEnabled = true
      settings.domStorageEnabled = true
      settings.userAgentString = userAgent

      webViewClient = object : WebViewClient() {
        private var checkJob: Runnable? = null

        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
          scheduleCheck(view)
        }

        private fun scheduleCheck(view: WebView?) {
          checkJob?.let { handler.removeCallbacks(it) }
          val runnable = object : Runnable {
            override fun run() {
              view?.evaluateJavascript(
                "(function() { " +
                  "return document.title.includes('Just a moment') || " +
                  "document.body.innerText.includes('cf-challenge') || " +
                  "document.body.innerText.includes('ray-id'); " +
                  "})()"
              ) { result ->
                if (result == "false") {
                  deferred.complete(true)
                } else {
                  // Still challenging, wait more or it might be stuck
                  handler.postDelayed(this, 2000)
                }
              }
            }
          }
          checkJob = runnable
          handler.postDelayed(runnable, 2000)
        }

        override fun shouldOverrideUrlLoading(
          view: WebView?,
          request: WebResourceRequest?
        ): Boolean = false
      }
    }

    webView.loadUrl(url)

    // Timeout for headless
    handler.postDelayed({
      if (!deferred.isCompleted) {
        deferred.complete(false)
      }
    }, 15000)

    val result = try {
      deferred.await()
    } catch (e: Exception) {
      false
    } finally {
      webView.stopLoading()
      webView.destroy()
    }
    result
  }

  suspend fun onBypassCompleted(url: String) {
    print(url)
    currentDeferred?.complete(true)
  }

  fun cancelBypass() {
    currentDeferred?.complete(false)
  }

  suspend fun clearCookies(url: String) = withContext(Dispatchers.Main) {
    val cookieManager = CookieManager.getInstance()
    val cookieString = cookieManager.getCookie(url)
    if (cookieString != null) {
      val cookies = cookieString.split(";")
      for (cookie in cookies) {
        val cookieName = cookie.split("=").firstOrNull()?.trim() ?: continue
        cookieManager.setCookie(url, "$cookieName=; Expires=Thu, 01 Jan 1970 00:00:00 GMT")
      }
      cookieManager.flush()
    }
  }

  suspend fun fetch(
    client: OkHttpClient,
    request: Request,
    retryCount: Int = 0
  ): Response = withContext(Dispatchers.IO) {
    val response = client.newCall(request).execute()

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
    val hasCfHeaders =
      response.code in 403..503 && (body.contains("cf-challenge") || body.contains("ray-id"))
    val hasKeywords = body.contains("<title>Just a moment...</title>") ||
      body.contains("Xác Minh An Toàn") ||
      // Safe Verification
      body.contains("cf-browser-verification") ||
      body.contains("Lỗi Server")

    // Detection for empty title with JS redirect (Anti-bot JS challenge)
    val isJsRedirect = (
      body.contains("window.location") ||
        body.contains("location.replace") ||
        body.contains("location.href")
      ) &&
      (body.contains("<title></title>") || !body.contains("<title>"))

    return hasCfHeaders || hasKeywords || isJsRedirect
  }
}
