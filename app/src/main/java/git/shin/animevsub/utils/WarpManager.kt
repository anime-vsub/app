package git.shin.animevsub.utils

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.data.local.PreferencesManager
import java.net.InetSocketAddress
import java.net.Proxy
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import uniffi.warp_native.WarpMode
import uniffi.warp_native.WarpTunnel

/**
 * Owns the embedded Cloudflare WARP tunnel.
 *
 * The Rust side brings a WARP transport up and exposes it as a SOCKS5 server
 * on `127.0.0.1`; [forPlayback] returns an OkHttp client that sends the video
 * traffic through it. Everything else (Supabase, Firebase, the catalogue API)
 * keeps using the untouched client, so no `VpnService` and no VPN permission
 * are involved.
 */
@Singleton
class WarpManager
@Inject
constructor(
  @param:ApplicationContext private val context: Context,
  private val prefs: PreferencesManager
) {
  companion object {
    private const val TAG = "WarpManager"
    private const val STATUS_POLL_MS = 3_000L
    private const val RETRY_MIN_MS = 2_000L
    private const val RETRY_MAX_MS = 60_000L

    /**
     * UDP/443 is blocked on many networks (corporate Wi-Fi, some mobile
     * carriers), which makes MASQUE HTTP/3 fail while HTTP/2 works on the same
     * endpoint. After the first H3 failure, retry over H2 instead of looping
     * on a transport that cannot work there.
     */
    private const val H3_FALLBACK_AFTER = 1

    private val SUPPORTED_MODES = setOf(
      PreferencesManager.WARP_MODE_OFF,
      PreferencesManager.WARP_MODE_WIREGUARD,
      PreferencesManager.WARP_MODE_MASQUE_H3,
      PreferencesManager.WARP_MODE_MASQUE_H2
    )
  }

  data class State(
    val mode: String = PreferencesManager.WARP_MODE_OFF,
    val available: Boolean = true,
    val starting: Boolean = false,
    val connected: Boolean = false,
    val port: Int = 0,
    val detail: String = "",
    val error: String? = null
  )

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val mutex = Mutex()

  private val _state = MutableStateFlow(State())
  val state: StateFlow<State> = _state.asStateFlow()

  private var tunnel: WarpTunnel? = null
  private var loadError: String? = null
  private var desiredMode: String = PreferencesManager.WARP_MODE_OFF
  private var retryJob: Job? = null

  init {
    scope.launch {
      prefs.warpMode
        .distinctUntilChanged()
        .collect { mode -> applyMode(mode) }
    }
    scope.launch {
      while (true) {
        delay(STATUS_POLL_MS)
        refreshStatus()
      }
    }
  }

  /** True when the current session should route playback through WARP. */
  fun isEnabled(): Boolean = _state.value.mode != PreferencesManager.WARP_MODE_OFF

  /**
   * Returns [base] wrapped in a SOCKS proxy when the tunnel is usable, and the
   * untouched client otherwise so playback never breaks just because WARP is
   * unreachable on this network.
   */
  fun forPlayback(base: OkHttpClient): OkHttpClient {
    val snapshot = _state.value
    if (!snapshot.connected || snapshot.port <= 0) return base
    return base
      .newBuilder()
      .proxy(
        Proxy(
          Proxy.Type.SOCKS,
          InetSocketAddress.createUnresolved("127.0.0.1", snapshot.port)
        )
      )
      .build()
  }

  /** Drops the cached WARP registrations and reconnects from scratch. */
  suspend fun resetCredentials() {
    val mode = mutex.withLock {
      retryJob?.cancel()
      retryJob = null
      val current = desiredMode
      runCatching { tunnel?.stop() }
      runCatching { tunnel?.resetCredentials() }
      _state.value = State(mode = current)
      current
    }
    if (mode != PreferencesManager.WARP_MODE_OFF) attemptConnect(mode, RETRY_MIN_MS)
  }

  private suspend fun applyMode(raw: String) {
    val mode = if (raw in SUPPORTED_MODES) raw else PreferencesManager.WARP_MODE_OFF
    val shouldConnect = mutex.withLock {
      if (mode == desiredMode) return@withLock false
      desiredMode = mode
      retryJob?.cancel()
      retryJob = null

      val native = ensureTunnel()
      if (native == null) {
        _state.value = State(
          mode = mode,
          available = false,
          detail = "WARP is not available in this build",
          error = loadError
        )
        return@withLock false
      }

      runCatching { native.stop() }

      if (mode == PreferencesManager.WARP_MODE_OFF) {
        _state.value = State(mode = mode)
        false
      } else {
        true
      }
    }
    if (shouldConnect) attemptConnect(mode, RETRY_MIN_MS)
  }

  /**
   * A WARP handshake can fail transiently (UDP path flapping, Cloudflare rate
   * limiting, the device registration API being slow) while the network is
   * actually fine. Give up only when the user changes the mode, and retry with
   * exponential backoff in the meantime.
   */
  private suspend fun attemptConnect(mode: String, backoffMs: Long, failures: Int = 0) {
    val native = tunnel ?: return
    var failed: Throwable? = null
    val connectMode =
      if (mode == PreferencesManager.WARP_MODE_MASQUE_H3 && failures >= H3_FALLBACK_AFTER) {
        PreferencesManager.WARP_MODE_MASQUE_H2
      } else {
        mode
      }

    mutex.withLock {
      if (desiredMode != mode) return
      // A concurrent attempt (e.g. a pending retry racing a reset) may have
      // already brought the tunnel up.
      if (_state.value.connected) return
      _state.value = State(mode = mode, starting = true, detail = "Connecting to Cloudflare…")

      val result = withContext(Dispatchers.IO) {
        runCatching { native.start(connectMode.toWarpMode()) }
      }

      if (desiredMode != mode) {
        withContext(Dispatchers.IO) { runCatching { native.stop() } }
        return
      }

      result
        .onSuccess { port ->
          _state.value = State(
            mode = mode,
            connected = true,
            port = port.toInt(),
            detail = if (connectMode == mode) {
              "Tunnel ready"
            } else {
              "MASQUE HTTP/3 is blocked here, using HTTP/2"
            }
          )
        }
        .onFailure { error ->
          Log.w(TAG, "WARP $connectMode start failed, retrying in ${backoffMs}ms", error)
          _state.value = State(
            mode = mode,
            starting = true,
            detail = "Cannot reach Cloudflare with this transport, retrying…",
            error = error.message ?: error.javaClass.simpleName
          )
          failed = error
        }
    }

    if (failed != null) {
      retryJob?.cancel()
      retryJob = scope.launch {
        delay(backoffMs)
        attemptConnect(mode, (backoffMs * 2).coerceAtMost(RETRY_MAX_MS), failures + 1)
      }
    }
  }

  private suspend fun refreshStatus() {
    val native = tunnel ?: return
    val snapshot = _state.value
    if (snapshot.starting || snapshot.mode == PreferencesManager.WARP_MODE_OFF) return

    runCatching {
      withContext(Dispatchers.IO) { native.status() }
    }.onSuccess { status ->
      _state.update { current ->
        if (current.starting || current.mode == PreferencesManager.WARP_MODE_OFF) {
          current
        } else {
          current.copy(
            connected = status.connected && status.running,
            port = if (status.running) status.port.toInt() else 0,
            detail = status.detail.ifBlank { current.detail },
            error = status.lastError
          )
        }
      }
    }
  }

  private fun ensureTunnel(): WarpTunnel? {
    tunnel?.let { return it }
    return try {
      WarpTunnel(context.filesDir.absolutePath).also { tunnel = it }
    } catch (error: Throwable) {
      loadError = error.message ?: error.javaClass.simpleName
      null
    }
  }

  private fun String.toWarpMode(): WarpMode = when (this) {
    PreferencesManager.WARP_MODE_WIREGUARD -> WarpMode.WIRE_GUARD
    PreferencesManager.WARP_MODE_MASQUE_H3 -> WarpMode.MASQUE_HTTP3
    PreferencesManager.WARP_MODE_MASQUE_H2 -> WarpMode.MASQUE_HTTP2
    else -> WarpMode.OFF
  }
}
