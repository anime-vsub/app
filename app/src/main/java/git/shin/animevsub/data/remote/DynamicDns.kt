package git.shin.animevsub.data.remote

import git.shin.animevsub.data.local.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress

class DynamicDns(
  private val prefs: PreferencesManager,
  private val bootstrapClient: OkHttpClient
) : Dns {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  @Volatile
  private var delegate: Dns = Dns.SYSTEM

  init {
    val initialMode = runCatching { runBlockingMode() }.getOrDefault(DEFAULT_MODE)
    val initialUrl = runCatching { runBlockingCustomUrl() }.getOrDefault(DEFAULT_CUSTOM_URL)
    delegate = build(initialMode, initialUrl)

    scope.launch {
      combine(prefs.dnsMode, prefs.customDnsUrl) { mode, url -> mode to url }
        .distinctUntilChanged()
        .collect { (mode, url) ->
          delegate = build(mode, url)
        }
    }
  }

  private fun runBlockingMode(): String = kotlinx.coroutines.runBlocking { prefs.dnsMode.first() }

  private fun runBlockingCustomUrl(): String = kotlinx.coroutines.runBlocking { prefs.customDnsUrl.first() }

  private fun build(mode: String, customUrl: String): Dns = try {
    when (mode) {
      MODE_GOOGLE -> DnsOverHttps.Builder()
        .client(bootstrapClient)
        .url(DOH_GOOGLE.toHttpUrl())
        .bootstrapDnsHosts(
          InetAddress.getByName("8.8.8.8"),
          InetAddress.getByName("8.8.4.4")
        )
        .build()

      MODE_CLOUDFLARE -> DnsOverHttps.Builder()
        .client(bootstrapClient)
        .url(DOH_CLOUDFLARE.toHttpUrl())
        .bootstrapDnsHosts(
          InetAddress.getByName("1.1.1.1"),
          InetAddress.getByName("1.0.0.1")
        )
        .build()

      MODE_QUAD9 -> DnsOverHttps.Builder()
        .client(bootstrapClient)
        .url(DOH_QUAD9.toHttpUrl())
        .bootstrapDnsHosts(
          InetAddress.getByName("9.9.9.9"),
          InetAddress.getByName("149.112.112.112")
        )
        .build()

      MODE_CUSTOM -> {
        val parsed = customUrl.trim().toHttpUrlOrNull()
        if (parsed == null) {
          Dns.SYSTEM
        } else {
          DnsOverHttps.Builder()
            .client(bootstrapClient)
            .url(parsed)
            .bootstrapDnsHosts(
              InetAddress.getByName("1.1.1.1"),
              InetAddress.getByName("8.8.8.8")
            )
            .build()
        }
      }

      else -> Dns.SYSTEM
    }
  } catch (e: Exception) {
    Dns.SYSTEM
  }

  override fun lookup(hostname: String): List<InetAddress> = try {
    delegate.lookup(hostname)
  } catch (e: Exception) {
    Dns.SYSTEM.lookup(hostname)
  }

  companion object {
    const val DEFAULT_MODE = PreferencesManager.DEFAULT_DNS_MODE
    const val DEFAULT_CUSTOM_URL = PreferencesManager.DEFAULT_CUSTOM_DNS_URL
    const val MODE_SYSTEM = PreferencesManager.DNS_MODE_SYSTEM
    const val MODE_GOOGLE = PreferencesManager.DNS_MODE_GOOGLE
    const val MODE_CLOUDFLARE = PreferencesManager.DNS_MODE_CLOUDFLARE
    const val MODE_QUAD9 = PreferencesManager.DNS_MODE_QUAD9
    const val MODE_CUSTOM = PreferencesManager.DNS_MODE_CUSTOM

    private const val DOH_GOOGLE = "https://dns.google/dns-query"
    private const val DOH_CLOUDFLARE = "https://cloudflare-dns.com/dns-query"
    private const val DOH_QUAD9 = "https://dns.quad9.net/dns-query"
  }
}
