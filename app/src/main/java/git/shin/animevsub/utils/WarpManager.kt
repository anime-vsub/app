package git.shin.animevsub.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.data.local.PreferencesManager
import java.net.InetSocketAddress
import java.net.Proxy
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private const val STATUS_POLL_MS = 3_000L

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
  private var appliedMode: String? = null

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
    mutex.withLock {
      val current = appliedMode
      runCatching { tunnel?.stop() }
      runCatching { tunnel?.resetCredentials() }
      appliedMode = null
      _state.value = State(mode = current ?: PreferencesManager.WARP_MODE_OFF)
    }
    applyMode(_state.value.mode)
  }

  private suspend fun applyMode(raw: String) {
    mutex.withLock {
      val mode = if (raw in SUPPORTED_MODES) raw else PreferencesManager.WARP_MODE_OFF
      if (mode == appliedMode) return@withLock
      appliedMode = mode

      val native = ensureTunnel()
      if (native == null) {
        _state.value = State(
          mode = mode,
          available = false,
          detail = "WARP is not available in this build",
          error = loadError
        )
        return@withLock
      }

      runCatching { native.stop() }

      if (mode == PreferencesManager.WARP_MODE_OFF) {
        _state.value = State(mode = mode)
        return@withLock
      }

      _state.value = State(mode = mode, starting = true, detail = "Connecting to Cloudflare…")
      withContext(Dispatchers.IO) {
        runCatching { native.start(mode.toWarpMode()) }
          .onSuccess { port ->
            _state.value = State(
              mode = mode,
              connected = true,
              port = port.toInt(),
              detail = "Tunnel ready"
            )
          }
          .onFailure { error ->
            _state.value = State(
              mode = mode,
              detail = "Cannot reach Cloudflare with this transport",
              error = error.message ?: error.javaClass.simpleName
            )
          }
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
