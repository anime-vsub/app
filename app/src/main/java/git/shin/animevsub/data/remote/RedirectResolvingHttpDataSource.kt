package git.shin.animevsub.data.remote

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@UnstableApi
class RedirectResolvingHttpDataSource(
  private val httpClient: OkHttpClient,
  private val defaultHttpDataSource: DefaultHttpDataSource
) : HttpDataSource by defaultHttpDataSource {

  private val redirectCache = mutableMapOf<String, String>()

  override fun open(dataSpec: DataSpec): Long {
    val url = dataSpec.uri.toString()
    val resolvedUrl = resolveRedirectWithPrefetch(url)

    val resolvedUri = Uri.parse(resolvedUrl)
    val resolvedDataSpec = dataSpec.buildUpon()
      .setUri(resolvedUri)
      .build()

    return defaultHttpDataSource.open(resolvedDataSpec)
  }

  private fun resolveRedirectWithPrefetch(url: String): String {
    PrefetchedRedirectCache.get(url)?.let { return it }

    redirectCache[url]?.let { return it }

    return try {
      val requestBuilder = Request.Builder().url(url)
      val request = requestBuilder.head().build()
      val response = httpClient.newCall(request).execute()

      val finalUrl = response.request.url.toString()
      val locationHeader = response.header("Location")

      val resolvedUrl = if (response.isRedirect && locationHeader != null) {
        runBlocking { resolveRedirect(locationHeader) }
      } else {
        finalUrl
      }

      response.close()
      redirectCache[url] = resolvedUrl
      PrefetchedRedirectCache.put(url, resolvedUrl)
      resolvedUrl
    } catch (e: IOException) {
      url
    }
  }

  private suspend fun resolveRedirect(url: String): String {
    PrefetchedRedirectCache.get(url)?.let { return it }
    redirectCache[url]?.let { return it }

    val resolvedUrl = try {
      val requestBuilder = Request.Builder().url(url)
      val request = requestBuilder.head().build()
      val response = httpClient.newCall(request).execute()

      val finalUrl = response.request.url.toString()
      val locationHeader = response.header("Location")

      val result = if (response.isRedirect && locationHeader != null) {
        resolveRedirect(locationHeader)
      } else {
        finalUrl
      }

      response.close()
      redirectCache[url] = result
      result
    } catch (e: IOException) {
      url
    }
    return resolvedUrl
  }

  override fun close() {
    defaultHttpDataSource.close()
  }

  fun clearCache() {
    redirectCache.clear()
  }
}

@UnstableApi
class RedirectResolvingDataSourceFactory(
  private val httpClient: OkHttpClient,
  private val defaultRequestProperties: MutableMap<String, String> = mutableMapOf()
) : HttpDataSource.Factory {

  private val defaultFactory = DefaultHttpDataSource.Factory()

  init {
    defaultFactory.setDefaultRequestProperties(defaultRequestProperties)
  }

  override fun createDataSource(): HttpDataSource {
    val defaultDataSource = defaultFactory.createDataSource() as DefaultHttpDataSource
    return RedirectResolvingHttpDataSource(httpClient, defaultDataSource)
  }

  override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): HttpDataSource.Factory {
    this.defaultRequestProperties.clear()
    this.defaultRequestProperties.putAll(defaultRequestProperties)
    defaultFactory.setDefaultRequestProperties(defaultRequestProperties)
    return this
  }
}

object PrefetchedRedirectCache {
  private val cache = mutableMapOf<String, String>()
  private val noRedirectPlaylists = mutableSetOf<String>()
  private val maxSize = 100

  fun get(url: String): String? = synchronized(cache) { cache[url] }

  fun put(url: String, resolvedUrl: String) {
    synchronized(cache) {
      if (cache.size >= maxSize) {
        val keysToRemove = cache.keys.toList().take(cache.size / 2)
        keysToRemove.forEach { cache.remove(it) }
      }
      cache[url] = resolvedUrl
    }
  }

  fun markNoRedirect(playlistUrl: String) {
    synchronized(noRedirectPlaylists) {
      noRedirectPlaylists.add(playlistUrl)
    }
  }

  fun isNoRedirect(playlistUrl: String): Boolean = synchronized(noRedirectPlaylists) {
    noRedirectPlaylists.contains(playlistUrl)
  }

  fun clear() {
    synchronized(cache) { cache.clear() }
    synchronized(noRedirectPlaylists) { noRedirectPlaylists.clear() }
  }

  fun remove(url: String) {
    synchronized(cache) { cache.remove(url) }
  }
}
