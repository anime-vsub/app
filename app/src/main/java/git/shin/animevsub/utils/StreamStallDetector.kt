package git.shin.animevsub.utils

/**
 * Detects playback that stopped making progress and decides when the stream
 * URL should be fetched again.
 *
 * The source hands out short-lived signed URLs. Once they expire, a stale HLS
 * playlist or media segment answers with 401/403/404. ExoPlayer reacts either
 * by reporting an IO error or by retrying the same dead URL forever, which the
 * user sees as an endless spinner. Fetching the player link again produces
 * usable URLs, so playback can resume where it stopped.
 *
 * Reloading is only attempted a few times: if fresh URLs fail immediately as
 * well, the problem is not expiry and the user has to be told instead of
 * retrying in a loop.
 *
 * This class has no Android dependencies so the policy can be unit tested.
 * Instances are meant to be used from a single thread.
 */
class StreamStallDetector(
  private val stallTimeoutMs: Long = DEFAULT_STALL_TIMEOUT_MS,
  private val maxReloads: Int = DEFAULT_MAX_RELOADS
) {
  enum class Action {
    NONE,

    /** Fetch the player link again and resume at the current position. */
    RELOAD,

    /** The stream is broken beyond expiry; surface an error to the user. */
    GIVE_UP
  }

  private var lastPosition = NO_POSITION
  private var lastProgressAt = 0L
  private var recoveryPosition = 0L
  private var reloads = 0
  private var gaveUp = false

  /** Forgets the current stream, e.g. after the user picked another episode. */
  fun reset() {
    lastPosition = NO_POSITION
    lastProgressAt = 0L
    recoveryPosition = 0L
    reloads = 0
    gaveUp = false
  }

  /**
   * Feeds the current playback position.
   *
   * @param stalled true while the player is buffering and supposed to be
   *   playing, i.e. the user is waiting for data.
   */
  fun sample(positionMs: Long, nowMs: Long, stalled: Boolean): Action {
    val previous = lastPosition
    lastPosition = positionMs

    if (previous == NO_POSITION) {
      lastProgressAt = nowMs
      return Action.NONE
    }

    when {
      positionMs > previous -> {
        lastProgressAt = nowMs
        // Real progress after a reload proves the new URLs work; give the
        // next expiry a full budget again.
        if (reloads > 0 && positionMs >= recoveryPosition + STABLE_PROGRESS_MS) {
          reloads = 0
          gaveUp = false
        }
      }
      positionMs < previous -> lastProgressAt = nowMs
    }

    if (!stalled) {
      // Paused, idle or still playing fine; the stall window must not keep
      // running.
      lastProgressAt = nowMs
      return Action.NONE
    }

    if (nowMs - lastProgressAt < stallTimeoutMs) return Action.NONE
    return recover(nowMs, positionMs)
  }

  /**
   * Reports an IO error caused by an expired URL. Unlike [sample] this has no
   * grace period: the stream is already known to be dead.
   */
  fun streamError(positionMs: Long, nowMs: Long): Action {
    lastPosition = positionMs
    return recover(nowMs, positionMs)
  }

  private fun recover(nowMs: Long, positionMs: Long): Action {
    lastProgressAt = nowMs
    if (reloads >= maxReloads) {
      if (gaveUp) return Action.NONE
      gaveUp = true
      return Action.GIVE_UP
    }
    reloads++
    recoveryPosition = positionMs
    return Action.RELOAD
  }

  companion object {
    const val DEFAULT_STALL_TIMEOUT_MS = 20_000L
    const val DEFAULT_MAX_RELOADS = 2

    /** Playback must advance this far past the resume point to count as fixed. */
    const val STABLE_PROGRESS_MS = 5_000L

    private const val NO_POSITION = -1L
  }
}
