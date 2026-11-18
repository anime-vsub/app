package git.shin.animevsub.data.remote

import git.shin.animevsub.data.model.PlayerData
import git.shin.animevsub.data.model.ServerInfo

fun interface SegmentDataInterceptor {
  fun intercept(server: ServerInfo, playerData: PlayerData, segmentUrl: String, data: ByteArray): ByteArray
}

/**
 * A [SegmentDataInterceptor] whose rewrite only depends on the first bytes of a
 * segment (for example dropping a wrapping header).
 *
 * Implement this instead of the plain interface whenever possible: the plain
 * form forces [TransformableHttpDataSource] to download the whole segment into
 * memory before ExoPlayer can read byte zero, which delays the first frame and
 * makes seeking feel slow. With this interface the segment is streamed.
 */
interface StreamingSegmentInterceptor : SegmentDataInterceptor {
  /**
   * Number of leading bytes to drop, decided from the [probe] prefix of the
   * segment (at most [PROBE_SIZE] bytes, possibly fewer at the end of a file).
   */
  fun leadingBytesToSkip(probe: ByteArray): Int

  companion object {
    const val PROBE_SIZE = 128
  }
}
