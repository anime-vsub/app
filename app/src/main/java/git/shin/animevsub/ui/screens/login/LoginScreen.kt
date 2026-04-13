package git.shin.animevsub.ui.screens.login

import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.ErrorColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  onNavigateBack: () -> Unit,
  viewModel: LoginViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  var webView by remember { mutableStateOf<WebView?>(null) }
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }
  var progress by remember { mutableStateOf(0) }
  var pageTitle by remember { mutableStateOf("") }
  val loadingText = stringResource(R.string.loading)
  val loginText = stringResource(R.string.login)

  LaunchedEffect(Unit) {
    pageTitle = loadingText
  }

  // Navigate back on success
  LaunchedEffect(uiState.isSuccess) {
    if (uiState.isSuccess) {
      onNavigateBack()
    }
  }

  Scaffold(
    modifier = Modifier.statusBarsPadding(),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                text = webView?.url ?: uiState.loginUrl,
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
        tonalElevation = 8.dp
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
              onNavigateBack()
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

            Button(
              onClick = { viewModel.checkLoginStatus() },
              colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
              enabled = !uiState.isLoading,
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
              modifier = Modifier.height(36.dp)
            ) {
              if (uiState.isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = Color.White,
                  strokeWidth = 2.dp
                )
              } else {
                Text(stringResource(R.string.done), fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
          }
        }
      }
    },
    containerColor = DarkBackground
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      AndroidView(
        factory = { context ->
          WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.userAgentString =
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36"

            webViewClient = object : WebViewClient() {
              override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                canGoBack = view?.canGoBack() ?: false
                canGoForward = view?.canGoForward() ?: false
                pageTitle = view?.title ?: loginText
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
                pageTitle = title ?: loginText
              }
            }

            loadUrl(uiState.loginUrl)
            webView = this
          }
        },
        modifier = Modifier.fillMaxSize()
      )

      if (uiState.error != null) {
        Surface(
          color = ErrorColor.copy(alpha = 0.95f),
          modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth(),
          shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
        ) {
          Text(
            text = uiState.error!!,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}
