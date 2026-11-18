package git.shin.animevsub.data.remote.api_hidden

import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.ServerInfo
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The data-source streams segments and only buffers a header trim, so the
 * "skip the first N bytes" decision made from a 128-byte probe must match what
 * the whole-payload trimmer would have produced.
 */
class SegmentDecryptUtilTest {
  private val server = ServerInfo(name = "test", extra = emptyMap())
  private val playerData = PlayerData(link = "https://example.com/playlist.m3u8", type = "hls")

  private fun payload(size: Int, pngWrapped: Boolean, seed: Int = 7): ByteArray {
    val bytes = ByteArray(size) { ((it * 31 + seed) and 0xFF).toByte() }
    if (pngWrapped) {
      bytes[0] = 0x89.toByte()
      bytes[1] = 0x50.toByte()
      bytes[2] = 0x4E.toByte()
      bytes[3] = 0x47.toByte()
    }
    return bytes
  }

  @Test
  fun `drops the png header when the segment is wrapped`() {
    val data = payload(PNG_SEGMENT_HEADER_SIZE + 64, pngWrapped = true)

    val trimmed = trimSegmentHeader(data)

    assertEquals(64, trimmed.size)
    assertArrayEquals(data.copyOfRange(PNG_SEGMENT_HEADER_SIZE, data.size), trimmed)
  }

  @Test
  fun `keeps the payload when there is no png header`() {
    val data = payload(PNG_SEGMENT_HEADER_SIZE + 64, pngWrapped = false)

    assertArrayEquals(data, trimSegmentHeader(data))
  }

  @Test
  fun `streaming skip matches the whole payload trim`() {
    val wrapped = payload(PNG_SEGMENT_HEADER_SIZE + 64, pngWrapped = true)
    val plain = payload(PNG_SEGMENT_HEADER_SIZE + 64, pngWrapped = false)

    listOf(wrapped, plain).forEach { data ->
      val probe = data.copyOfRange(0, PNG_SEGMENT_HEADER_SIZE)
      val skip = PngHeaderTrimInterceptor.leadingBytesToSkip(probe)
      val streamed = PngHeaderTrimInterceptor.intercept(server, playerData, "https://cdn/x.ts", data)
      assertArrayEquals(trimSegmentHeader(data), streamed)
      assertArrayEquals(data.copyOfRange(skip, data.size), streamed)
    }
  }

  @Test
  fun `short streams are left untouched`() {
    val data = payload(10, pngWrapped = true)

    assertArrayEquals(data, trimSegmentHeader(data))
  }
}
