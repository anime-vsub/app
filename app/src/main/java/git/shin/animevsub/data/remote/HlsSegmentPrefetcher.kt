package git.shin.animevsub.data.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class HlsSegmentPrefetcher(
  private val httpClient: OkHttpClient,
  private val maxConcurrentPrefetches: Int = 2
) {
  private val scope = CoroutineScope(Dispatchers.IO)
  private val pendingUrls = mutableMapOf<String, Job?>()
  private var currentPlaylistUrl: String? = null
  private var isPrefetching = false
  private val playlistSegments = mutableMapOf<String, MutableList<String>>()
  private val semaphore = Semaphore(maxConcurrentPrefetches)

  fun prefetch(playlistUrl: String, url: String, requestProperties: Map<String, String> = emptyMap()) {
    if (PrefetchedRedirectCache.isNoRedirect(playlistUrl)) {
      return
    }
    if (PrefetchedRedirectCache.get(url) != null || pendingUrls.containsKey(url)) {
      return
    }

    val job = scope.launch {
      semaphore.acquire()
      try {
        resolveRedirect(playlistUrl, url, requestProperties)
      } finally {
        semaphore.release()
      }
    }
    pendingUrls[url] = job
    playlistSegments.getOrPut(playlistUrl) { mutableListOf() }.add(url)
  }

  suspend fun prefetchMultiple(playlistUrl: String, urls: List<String>, requestProperties: Map<String, String> = emptyMap()) {
    if (PrefetchedRedirectCache.isNoRedirect(playlistUrl)) {
      return
    }
    isPrefetching = true
    urls.forEach { url ->
      prefetch(playlistUrl, url, requestProperties)
    }
    pendingUrls.values.forEach { it?.join() }
    isPrefetching = false
  }

  suspend fun prefetchFromPlaylist(playlistUrl: String, segmentUrls: List<String>, requestProperties: Map<String, String> = emptyMap()) {
    if (PrefetchedRedirectCache.isNoRedirect(playlistUrl)) {
      return
    }
    currentPlaylistUrl = playlistUrl

    segmentUrls.forEach { segmentUrl ->
      prefetch(playlistUrl, segmentUrl, requestProperties)
    }

    pendingUrls.values.forEach { it?.join() }
  }

  suspend fun getResolvedUrl(url: String): String? = withContext(Dispatchers.IO) {
    pendingUrls[url]?.join()
    PrefetchedRedirectCache.get(url)
  }

  fun isResolved(url: String): Boolean = PrefetchedRedirectCache.get(url) != null

  fun getResolvedUrlSync(url: String): String? = PrefetchedRedirectCache.get(url)

  private suspend fun resolveRedirect(playlistUrl: String, url: String, requestProperties: Map<String, String>) {
    try {
      val requestBuilder = Request.Builder().url(url)
      requestProperties.forEach { (key, value) ->
        requestBuilder.addHeader(key, value)
      }

      val request = requestBuilder.head().build()
      val response = httpClient.newCall(request).execute()

      response.request.url.toString()
      val locationHeader = response.header("Location")

      if (!response.isRedirect || locationHeader == null) {
        response.close()
        markPlaylistNoRedirect(playlistUrl)
        return
      }

      val resolvedUrl = resolveRedirectRecursive(locationHeader, requestProperties)

      response.close()
      PrefetchedRedirectCache.put(url, resolvedUrl)
      pendingUrls.remove(url)
    } catch (e: IOException) {
      pendingUrls.remove(url)
    }
  }

  private fun markPlaylistNoRedirect(playlistUrl: String) {
    PrefetchedRedirectCache.markNoRedirect(playlistUrl)
    pendingUrls.keys.toList().forEach { url ->
      if (playlistSegments[playlistUrl]?.contains(url) == true) {
        pendingUrls[url]?.cancel()
        pendingUrls.remove(url)
      }
    }
    playlistSegments.remove(playlistUrl)
  }

  private suspend fun resolveRedirectRecursive(url: String, requestProperties: Map<String, String>): String = try {
    val requestBuilder = Request.Builder().url(url)
    requestProperties.forEach { (key, value) ->
      requestBuilder.addHeader(key, value)
    }

    val request = requestBuilder.head().build()
    val response = httpClient.newCall(request).execute()

    val finalUrl = response.request.url.toString()
    val locationHeader = response.header("Location")

    val resolvedUrl = if (response.isRedirect && locationHeader != null) {
      resolveRedirectRecursive(locationHeader, requestProperties)
    } else {
      finalUrl
    }

    response.close()
    resolvedUrl
  } catch (e: IOException) {
    url
  }

  fun clearCache() {
    pendingUrls.values.forEach { it?.cancel() }
    pendingUrls.clear()
    playlistSegments.clear()
    PrefetchedRedirectCache.clear()
    currentPlaylistUrl = null
    isPrefetching = false
  }

  fun removeUrl(url: String) {
    pendingUrls[url]?.cancel()
    pendingUrls.remove(url)
    PrefetchedRedirectCache.remove(url)
  }

  fun isPlaylistNoRedirect(playlistUrl: String): Boolean = PrefetchedRedirectCache.isNoRedirect(playlistUrl)

  val pendingCount: Int get() = pendingUrls.size
  val isPrefetchingPlaylist: Boolean get() = isPrefetching
  val playlistUrl: String? get() = currentPlaylistUrl
}
