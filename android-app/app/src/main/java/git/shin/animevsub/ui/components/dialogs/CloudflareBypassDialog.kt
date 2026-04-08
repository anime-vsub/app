package git.shin.animevsub.ui.components.dialogs

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CloudflareBypassDialog(
  url: String,
  onResult: (String?) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnBackPress = true,
      dismissOnClickOutside = false
    )
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
          factory = { context ->
            WebView(context).apply {
              settings.javaScriptEnabled = true
              settings.domStorageEnabled = true
              settings.userAgentString =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"

              webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                  view: WebView?,
                  request: WebResourceRequest?
                ): Boolean {
                  return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                  super.onPageFinished(view, url)
                  // Check if we bypassed Cloudflare
                  // Usually, if the title is no longer "Just a moment...", we are through
                  if (view?.title?.contains("Just a moment", ignoreCase = true) == false) {
                    onResult(url)
                  }
                }
              }
              loadUrl(url)
            }
          },
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
