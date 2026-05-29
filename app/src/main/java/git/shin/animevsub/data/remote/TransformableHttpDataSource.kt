package git.shin.animevsub.data.remote

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.ServerInfo
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

@UnstableApi
class TransformableHttpDataSource(
  private val delegate: DefaultHttpDataSource,
  private val server: ServerInfo,
  private val playerData: PlayerData,
  private val urlInterceptor: SegmentUrlInterceptor?,
  private val dataInterceptor: SegmentDataInterceptor?
) : HttpDataSource by delegate {

  private var dataStream: ByteArrayInputStream? = null
  private var isDelegateOpened = false

  override fun open(dataSpec: DataSpec): Long {
    var uri = dataSpec.uri.toString()
    if (urlInterceptor != null) {
      uri = urlInterceptor.intercept(server, playerData, uri)
    }

    val resolvedSpec = dataSpec.buildUpon()
      .setUri(Uri.parse(uri))
      .build()

    val delegateLength = delegate.open(resolvedSpec)
    isDelegateOpened = true

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

  override fun read(buffer: ByteArray, offset: Int, length: Int): Int = if (dataStream != null) {
    dataStream!!.read(buffer, offset, length)
  } else {
    delegate.read(buffer, offset, length)
  }

  override fun close() {
    try {
      dataStream?.close()
    } catch (_: IOException) {
    }
    dataStream = null
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
  private val defaultRequestProperties: MutableMap<String, String> = mutableMapOf()
) : HttpDataSource.Factory {

  private val defaultFactory = DefaultHttpDataSource.Factory()

  init {
    defaultFactory.setDefaultRequestProperties(defaultRequestProperties)
  }

  override fun createDataSource(): HttpDataSource {
    val defaultDataSource = defaultFactory.createDataSource() as DefaultHttpDataSource
    return TransformableHttpDataSource(
      delegate = defaultDataSource,
      server = server,
      playerData = playerData,
      urlInterceptor = urlInterceptor,
      dataInterceptor = dataInterceptor
    )
  }

  override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): HttpDataSource.Factory {
    this.defaultRequestProperties.clear()
    this.defaultRequestProperties.putAll(defaultRequestProperties)
    defaultFactory.setDefaultRequestProperties(defaultRequestProperties)
    return this
  }
}
