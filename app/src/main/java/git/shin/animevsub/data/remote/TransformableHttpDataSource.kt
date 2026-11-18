package git.shin.animevsub.data.remote

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.ServerInfo
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import okhttp3.Call

@UnstableApi
class TransformableHttpDataSource(
  private val delegate: HttpDataSource,
  private val server: ServerInfo,
  private val playerData: PlayerData,
  private val urlInterceptor: SegmentUrlInterceptor?,
  private val dataInterceptor: SegmentDataInterceptor?
) : HttpDataSource by delegate {

  private var dataStream: InputStream? = null
  private var isDelegateOpened = false

  // Bytes of the streamed segment that were already read for the probe but not
  // yet handed to the player.
  private var pendingProbe = ByteArray(0)
  private var pendingProbeOffset = 0

  override fun open(dataSpec: DataSpec): Long {
    pendingProbe = ByteArray(0)
    pendingProbeOffset = 0

    var uri = dataSpec.uri.toString()
    if (urlInterceptor != null) {
      uri = urlInterceptor.intercept(server, playerData, uri)
    }

    val resolvedSpec = dataSpec.buildUpon()
      .setUri(Uri.parse(uri))
      .build()

    val delegateLength = delegate.open(resolvedSpec)
    isDelegateOpened = true

    val streamingInterceptor = dataInterceptor as? StreamingSegmentInterceptor
    if (streamingInterceptor != null) {
      // Only the start of the segment is inspected, so the rest can be streamed
      // straight through instead of waiting for the whole segment.
      val probe = readUpTo(StreamingSegmentInterceptor.PROBE_SIZE)
      val skip = streamingInterceptor.leadingBytesToSkip(probe).coerceIn(0, probe.size)
      pendingProbe = if (skip > 0) probe.copyOfRange(skip, probe.size) else probe
      pendingProbeOffset = 0
      return if (delegateLength >= 0) {
        (delegateLength - skip).coerceAtLeast(0L)
      } else {
        delegateLength
      }
    }

    if (dataInterceptor != null) {
      val fullData = readAllFromDelegate()
      val transformed = dataInterceptor.intercept(server, playerData, uri, fullData)
      dataStream = ByteArrayInputStream(transformed)
      return transformed.size.toLong()
    }

    return delegateLength
  }

  private fun readAllFromDelegate(): ByteArray {
    val buffer = ByteArray(8192)
    val output = ByteArrayOutputStream()
    output.use { os ->
      while (true) {
        val bytesRead = delegate.read(buffer, 0, buffer.size)
        if (bytesRead == -1) break
        os.write(buffer, 0, bytesRead)
      }
      return os.toByteArray()
    }
  }

  /** Reads at most [size] bytes from the delegate, tolerating short streams. */
  private fun readUpTo(size: Int): ByteArray {
    val buffer = ByteArray(size)
    var total = 0
    while (total < size) {
      val bytesRead = delegate.read(buffer, total, size - total)
      if (bytesRead == -1) break
      total += bytesRead
    }
    return if (total == size) buffer else buffer.copyOf(total)
  }

  override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
    dataStream?.let { return it.read(buffer, offset, length) }
    if (pendingProbeOffset < pendingProbe.size) {
      val count = minOf(length, pendingProbe.size - pendingProbeOffset)
      System.arraycopy(pendingProbe, pendingProbeOffset, buffer, offset, count)
      pendingProbeOffset += count
      return count
    }
    return delegate.read(buffer, offset, length)
  }

  override fun close() {
    try {
      dataStream?.close()
    } catch (_: IOException) {
    }
    dataStream = null
    pendingProbe = ByteArray(0)
    pendingProbeOffset = 0
    if (isDelegateOpened) {
      isDelegateOpened = false
      delegate.close()
    }
  }
}

@UnstableApi
class TransformableDataSourceFactory(
  private val server: ServerInfo,
  private val playerData: PlayerData,
  private val urlInterceptor: SegmentUrlInterceptor?,
  private val dataInterceptor: SegmentDataInterceptor?,
  private val defaultRequestProperties: MutableMap<String, String> = mutableMapOf(),
  callFactory: Call.Factory? = null
) : HttpDataSource.Factory {

  private val innerFactory: HttpDataSource.Factory = if (callFactory != null) {
    OkHttpDataSource.Factory(callFactory).apply {
      setDefaultRequestProperties(defaultRequestProperties)
    }
  } else {
    DefaultHttpDataSource.Factory().apply {
      setDefaultRequestProperties(defaultRequestProperties)
    }
  }

  override fun createDataSource(): HttpDataSource = TransformableHttpDataSource(
    delegate = innerFactory.createDataSource(),
    server = server,
    playerData = playerData,
    urlInterceptor = urlInterceptor,
    dataInterceptor = dataInterceptor
  )

  override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): HttpDataSource.Factory {
    this.defaultRequestProperties.clear()
    this.defaultRequestProperties.putAll(defaultRequestProperties)
    innerFactory.setDefaultRequestProperties(defaultRequestProperties)
    return this
  }
}
