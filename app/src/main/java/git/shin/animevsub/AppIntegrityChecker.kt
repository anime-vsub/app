package git.shin.animevsub

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.security.MessageDigest
import kotlin.system.exitProcess

object AppIntegrityChecker {

  fun checkIntegrity(context: Context) {
    if (BuildConfig.DEBUG) return

    val expectedSha256 = BuildConfig.RELEASE_CERT_SHA256
    if (expectedSha256.isEmpty()) {
      return
    }

    val actualSha256 = getSignatureSha256(context)

    if (expectedSha256.lowercase() != actualSha256?.lowercase()) {
      if (BuildConfig.DEBUG) {
        Log.e("IntegrityChecker", "App integrity check failed. Expected: \$expectedSha256, Actual: \$actualSha256")
      }
      exitProcess(0)
    }
  }

  private fun getSignatureSha256(context: Context): String? {
    try {
      val pm = context.packageManager
      val packageName = context.packageName
      val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        packageInfo.signingInfo?.apkContentsSigners
      } else {
        @Suppress("DEPRECATION")
        val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        @Suppress("DEPRECATION")
        packageInfo.signatures
      }

      if (signatures.isNullOrEmpty()) return null

      val md = MessageDigest.getInstance("SHA-256")
      val digest = md.digest(signatures[0].toByteArray())
      return digest.joinToString(":") { "%02X".format(it) }
    } catch (e: Exception) {
      print(e)
      return null
    }
  }
}
