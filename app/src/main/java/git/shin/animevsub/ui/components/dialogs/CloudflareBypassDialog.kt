package git.shin.animevsub.ui.components.dialogs

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudflareBypassDialog(
  url: String,
  onResult: (String?) -> Unit,
  onDismiss: () -> Unit
) {
  var webView by remember { mutableStateOf<WebView?>(null) }
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }
  var progress by remember { mutableIntStateOf(0) }
  val loadingText = stringResource(R.string.loading)
  var pageTitle by remember { mutableStateOf(loadingText) }

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
      color = DarkBackground
    ) {
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
          Column {
            TopAppBar(
              modifier = Modifier.height(56.dp),
              title = {
                Column {
                  Text(
                    text = pageTitle,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                  )
                  Text(
                    text = webView?.url ?: url,
                    color = TextGrey,
                    fontSize = 10.sp,
                    maxLines = 1
                  )
                }
              },
              actions = {
                IconButton(onClick = {
                  if (progress < 100) {
                    webView?.stopLoading()
                  } else {
                    webView?.reload()
                  }
                }) {
                  Icon(
                    imageVector = if (progress < 100) Icons.Default.Close else Icons.Default.Refresh,
                    contentDescription = if (progress < 100) {
                      stringResource(R.string.stop)
                    } else {
                      stringResource(
                        R.string.reload
                      )
                    },
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                  )
                }
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
            if (progress < 100) {
              LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(2.dp),
                color = AccentMain,
                trackColor = Color.Transparent
              )
            } else {
              HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 0.5.dp)
            }
          }
        },
        bottomBar = {
          Surface(
            color = DarkBackground,
            tonalElevation = 8.dp,
            modifier = Modifier.navigationBarsPadding()
          ) {
            Column {
              HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 0.5.dp)
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(52.dp)
                  .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                IconButton(onClick = {
                  webView?.stopLoading()
                  onDismiss()
                }) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                  )
                }

                IconButton(
                  onClick = { webView?.goBack() },
                  enabled = canGoBack
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = if (canGoBack) TextPrimary else TextGrey,
                    modifier = Modifier.size(24.dp)
                  )
                }

                IconButton(
                  onClick = { webView?.goForward() },
                  enabled = canGoForward
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.forward),
                    tint = if (canGoForward) TextPrimary else TextGrey,
                    modifier = Modifier.size(24.dp)
                  )
                }

                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        },
        containerColor = DarkBackground
      ) { padding ->
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
          AndroidView(
            factory = { ctx ->
              WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString =
                  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36"

                webViewClient = object : WebViewClient() {
                  override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    canGoBack = view?.canGoBack() ?: false
                    canGoForward = view?.canGoForward() ?: false
                    pageTitle = view?.title ?: ""

                    // Check if bypassed
                    val title = view?.title
                    if (!title.isNullOrEmpty() &&
                      !title.contains("Just a moment", ignoreCase = true) &&
                      !title.contains("Cloudflare", ignoreCase = true)
                    ) {
                      onResult(url)
                    }
                  }

                  override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                  ): Boolean = false
                }

                webChromeClient = object : WebChromeClient() {
                  override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progress = newProgress
                  }

                  override fun onReceivedTitle(view: WebView?, title: String?) {
                    pageTitle = title ?: ""
                  }
                }

                loadUrl(url)
                webView = this
              }
            },
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}
