package git.shin.animevsub.utils

import git.shin.animevsub.utils.StreamStallDetector.Action
import org.junit.Assert.assertEquals
import org.junit.Test

class StreamStallDetectorTest {
  private val timeout = 20_000L

  private fun detector() = StreamStallDetector(
    stallTimeoutMs = timeout,
    maxReloads = 2
  )

  @Test
  fun `does not reload while the position still advances`() {
    val detector = detector()
    var position = 10_000L

    repeat(10) { step ->
      val now = step * 1_000L
      position += 1_000L
      assertEquals(Action.NONE, detector.sample(position, now, stalled = true))
    }
  }

  @Test
  fun `reloads after the timeout without progress`() {
    val detector = detector()

    assertEquals(Action.NONE, detector.sample(10_000L, 0L, stalled = true))
    assertEquals(Action.NONE, detector.sample(10_000L, timeout - 1, stalled = true))
    assertEquals(Action.RELOAD, detector.sample(10_000L, timeout, stalled = true))
  }

  @Test
  fun `gives up after the reload budget is exhausted`() {
    val detector = detector()

    assertEquals(Action.NONE, detector.sample(10_000L, 0L, stalled = true))
    assertEquals(Action.RELOAD, detector.sample(10_000L, timeout, stalled = true))
    assertEquals(Action.NONE, detector.sample(10_000L, timeout * 2 - 1, stalled = true))
    assertEquals(Action.RELOAD, detector.sample(10_000L, timeout * 2, stalled = true))
    assertEquals(Action.NONE, detector.sample(10_000L, timeout * 3 - 1, stalled = true))
    assertEquals(Action.GIVE_UP, detector.sample(10_000L, timeout * 3, stalled = true))
    assertEquals(Action.NONE, detector.sample(10_000L, timeout * 4, stalled = true))
  }

  @Test
  fun `successful playback restores the reload budget`() {
    val detector = detector()

    assertEquals(Action.NONE, detector.sample(10_000L, 0L, stalled = true))
    assertEquals(Action.RELOAD, detector.sample(10_000L, timeout, stalled = true))

    var position = 10_000L
    var now = timeout
    repeat(10) {
      now += 1_000L
      position += 1_000L
      assertEquals(Action.NONE, detector.sample(position, now, stalled = false))
    }

    // Past the stable-progress window the budget is full again.
    assertEquals(Action.RELOAD, detector.sample(position, now + timeout, stalled = true))
  }

  @Test
  fun `a paused player never triggers a reload`() {
    val detector = detector()

    assertEquals(Action.NONE, detector.sample(10_000L, 0L, stalled = false))
    assertEquals(Action.NONE, detector.sample(10_000L, timeout * 5, stalled = false))
  }

  @Test
  fun `stall time is discarded when playback resumes`() {
    val detector = detector()

    assertEquals(Action.NONE, detector.sample(10_000L, 0L, stalled = false))
    assertEquals(Action.NONE, detector.sample(10_000L, timeout - 1, stalled = false))
    // Resuming resets the window, so the following stall starts from scratch.
    assertEquals(Action.NONE, detector.sample(10_000L, timeout, stalled = false))
    assertEquals(Action.NONE, detector.sample(10_000L, timeout * 2 - 1, stalled = true))
    assertEquals(Action.RELOAD, detector.sample(10_000L, timeout * 2, stalled = true))
  }

  @Test
  fun `reloads a stream that never reaches its first frame`() {
    val detector = detector()

    assertEquals(Action.NONE, detector.sample(0L, 0L, stalled = true))
    assertEquals(Action.RELOAD, detector.sample(0L, timeout, stalled = true))
  }

  @Test
  fun `an expired url error reloads immediately`() {
    val detector = detector()

    assertEquals(Action.RELOAD, detector.streamError(120_000L, 0L))
    // The reload resets the player to zero before resuming at the same
    // position; that must not hand out a second fresh budget.
    assertEquals(Action.NONE, detector.sample(0L, 1_000L, stalled = false))
    assertEquals(Action.NONE, detector.sample(120_000L, 2_000L, stalled = false))
    assertEquals(Action.RELOAD, detector.streamError(120_000L, 3_000L))
    assertEquals(Action.GIVE_UP, detector.streamError(120_000L, 4_000L))
  }

  @Test
  fun `reset forgets the accumulated failures`() {
    val detector = detector()

    assertEquals(Action.RELOAD, detector.streamError(30_000L, 0L))
    assertEquals(Action.RELOAD, detector.streamError(30_000L, 1L))
    assertEquals(Action.GIVE_UP, detector.streamError(30_000L, 2L))

    detector.reset()

    assertEquals(Action.RELOAD, detector.streamError(30_000L, 3L))
  }
}
