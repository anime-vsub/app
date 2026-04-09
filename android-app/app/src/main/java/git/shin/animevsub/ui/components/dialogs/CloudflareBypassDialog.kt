package git.shin.animevsub.ui.components.dialogs

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import git.shin.animevsub.R

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CloudflareBypassDialog(
  url: String,
  onResult: (String?) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }

  val webView = remember {
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
          canGoBack = view?.canGoBack() ?: false
          canGoForward = view?.canGoForward() ?: false

          // Check if we bypassed Cloudflare
          if (view?.title?.contains("Just a moment", ignoreCase = true) == false) {
            onResult(url)
          }
        }
      }
      loadUrl(url)
    }
  }

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
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
      ) {
        AndroidView(
          factory = { webView },
          modifier = Modifier.weight(1f)
        )

        HorizontalDivider()

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        ) {
          IconButton(onClick = { onResult(webView.url) }) {
            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
          }
          IconButton(
            onClick = { webView.goBack() },
            enabled = canGoBack
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
          IconButton(
            onClick = { webView.goForward() },
            enabled = canGoForward
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = stringResource(R.string.forward)
            )
          }
          IconButton(onClick = { webView.reload() }) {
            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.reload))
          }
        }
      }
    }
  }
}
