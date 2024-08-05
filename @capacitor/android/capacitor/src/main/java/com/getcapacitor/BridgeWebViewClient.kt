package com.getcapacitor

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

open class BridgeWebViewClient(private val bridge: Bridge) : WebViewClient() {
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return bridge.localServer!!.shouldInterceptRequest(request)
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url
        return bridge.launchIntent(url)
    }

    @Deprecated("")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return bridge.launchIntent(Uri.parse(url))
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        val webViewListeners = bridge.getWebViewListeners()

        if (webViewListeners != null && view.progress == 100) {
            for (listener in bridge.getWebViewListeners()) {
                listener.onPageLoaded(view)
            }
        }
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        super.onReceivedError(view, request, error)

        val webViewListeners = bridge.getWebViewListeners()
        if (webViewListeners != null) {
            for (listener in bridge.getWebViewListeners()) {
                listener.onReceivedError(view)
            }
        }

        val errorPath = bridge.errorUrl
        if (errorPath != null && request.isForMainFrame) {
            view.loadUrl(errorPath)
        }
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
        super.onPageStarted(view, url, favicon)
        bridge.reset()
        val webViewListeners = bridge.getWebViewListeners()

        if (webViewListeners != null) {
            for (listener in bridge.getWebViewListeners()) {
                listener.onPageStarted(view)
            }
        }
    }

    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse
    ) {
        super.onReceivedHttpError(view, request, errorResponse)

        val webViewListeners = bridge.getWebViewListeners()
        if (webViewListeners != null) {
            for (listener in bridge.getWebViewListeners()) {
                listener.onReceivedHttpError(view)
            }
        }

        val errorPath = bridge.errorUrl
        if (errorPath != null && request.isForMainFrame) {
            view.loadUrl(errorPath)
        }
    }

    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
        super.onRenderProcessGone(view, detail)
        var result = false

        val webViewListeners = bridge.getWebViewListeners()
        if (webViewListeners != null) {
            for (listener in bridge.getWebViewListeners()) {
                result = listener.onRenderProcessGone(view, detail) || result
            }
        }

        return result
    }
}
