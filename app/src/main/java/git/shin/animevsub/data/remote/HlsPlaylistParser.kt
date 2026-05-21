package git.shin.animevsub.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.regex.Pattern

class HlsPlaylistParser(private val httpClient: OkHttpClient) {

  data class PlaylistInfo(
    val baseUrl: String,
    val segmentUrls: List<String>,
    val isMasterPlaylist: Boolean = false,
    val variants: List<VariantInfo> = emptyList()
  )

  data class VariantInfo(
    val url: String,
    val bandwidth: Int,
    val resolution: String?
  )

  suspend fun parsePlaylist(playlistUrl: String, headers: Map<String, String>): PlaylistInfo? {
    return try {
      val requestBuilder = Request.Builder().url(playlistUrl)
      headers.forEach { (key, value) -> requestBuilder.header(key, value) }

      val response = httpClient.newCall(requestBuilder.build()).execute()
      if (!response.isSuccessful) return null

      val content = response.body?.string() ?: return null
      response.close()

      val baseUrl = getBaseUrl(playlistUrl)
      parseM3u8Content(content, baseUrl)
    } catch (e: IOException) {
      null
    }
  }

  private fun parseM3u8Content(content: String, baseUrl: String): PlaylistInfo {
    val lines = content.lines().filter { it.isNotBlank() }
    val segmentUrls = mutableListOf<String>()
    val variants = mutableListOf<VariantInfo>()

    var isMaster = false
    var currentEXTINF: String? = null

    for (line in lines) {
      when {
        line.startsWith("#EXTM3U") -> isMaster = true
        line.startsWith("#EXTINF:") -> {
          currentEXTINF = line.removePrefix("#EXTINF:")
        }

        line.startsWith("#") -> {
          currentEXTINF = null
        }

        !line.startsWith("#") && line.isNotBlank() -> {
          val segmentUrl = resolveUrl(line.trim(), baseUrl)
          if (isMaster) {
            val bandwidth = extractBandwidth(lines, lines.indexOf(line))
            val resolution = extractResolution(lines, lines.indexOf(line))
            variants.add(VariantInfo(segmentUrl, bandwidth, resolution))
          } else {
            segmentUrls.add(segmentUrl)
          }
          currentEXTINF = null
        }
      }
    }

    return PlaylistInfo(
      baseUrl = baseUrl,
      segmentUrls = segmentUrls,
      isMasterPlaylist = isMaster,
      variants = variants
    )
  }

  private fun getBaseUrl(url: String): String {
    val lastSlash = url.lastIndexOf('/')
    return if (lastSlash > 0) url.substring(0, lastSlash + 1) else url
  }

  private fun resolveUrl(url: String, baseUrl: String): String = when {
    url.startsWith("http://") || url.startsWith("https://") -> url
    url.startsWith("/") -> {
      val base = baseUrl.removeSuffix("/")
      val firstSlash = base.indexOf("://") + 3
      val domainEnd = base.indexOf("/", firstSlash)
      if (domainEnd > 0) {
        base.substring(0, domainEnd) + url
      } else {
        base + url
      }
    }

    else -> baseUrl + url
  }

  private fun extractBandwidth(lines: List<String>, index: Int): Int {
    for (i in (index - 1) downTo 0) {
      val line = lines[i]
      if (line.contains("BANDWIDTH=")) {
        val pattern = Pattern.compile("BANDWIDTH=(\\d+)")
        val matcher = pattern.matcher(line)
        if (matcher.find()) {
          return matcher.group(1)?.toIntOrNull() ?: 0
        }
      }
    }
    return 0
  }

  private fun extractResolution(lines: List<String>, index: Int): String? {
    for (i in (index - 1) downTo 0) {
      val line = lines[i]
      if (line.contains("RESOLUTION=")) {
        val pattern = Pattern.compile("RESOLUTION=([^,]+)")
        val matcher = pattern.matcher(line)
        if (matcher.find()) {
          return matcher.group(1)
        }
      }
    }
    return null
  }

  fun extractSegmentUrlsFromContent(m3u8Content: String, playlistUrl: String): List<String> {
    val baseUrl = getBaseUrl(playlistUrl)
    return parseM3u8Content(m3u8Content, baseUrl).segmentUrls
  }
}
