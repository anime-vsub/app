/*
Copyright 2015 Google Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.getcapacitor

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.getcapacitor.plugin.util.HttpRequestHandler
import com.getcapacitor.plugin.util.HttpRequestHandler.HttpURLConnectionBuilder
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * Helper class meant to be used with the android.webkit.WebView class to enable
 * hosting assets,
 * resources and other data on 'virtual' https:// URL.
 * Hosting assets and resources on https:// URLs is desirable as it is
 * compatible with the
 * Same-Origin policy.
 *
 *
 * This class is intended to be used from within the
 * [android.webkit.WebViewClient.shouldInterceptRequest]
 * and
 * [android.webkit.WebViewClient.shouldInterceptRequest]
 * methods.
 */
class WebViewLocalServer internal constructor(
    context: Context,
    private val bridge: Bridge,
    private val jsInjector: JSInjector,
    private val authorities: ArrayList<String>,
    // Whether to route all requests to paths without extensions back to
    // `index.html`
    private val html5mode: Boolean
) {
    var basePath: String? = null
        private set

    private val uriMatcher = UriMatcher(null)
    private val protocolHandler = AndroidProtocolHandler(context.applicationContext)
    private var isAsset = false

    /**
     * A handler that produces responses for paths on the virtual asset server.
     *
     *
     * Methods of this handler will be invoked on a background thread and care must
     * be taken to
     * correctly synchronize access to any shared state.
     *
     *
     * On Android KitKat and above these methods may be called on more than one
     * thread. This thread
     * may be different than the thread on which the shouldInterceptRequest method
     * was invoke.
     * This means that on Android KitKat and above it is possible to block in this
     * method without
     * blocking other resources from loading. The number of threads used to
     * parallelize loading
     * is an internal implementation detail of the WebView and may change between
     * updates which
     * means that the amount of time spend blocking in this method should be kept to
     * an absolute
     * minimum.
     */
    abstract class PathHandler @JvmOverloads constructor(
        val encoding: String? = null,
        val charset: String? = null,
        val statusCode: Int = 200,
        val reasonPhrase: String = "OK",
        responseHeaders: MutableMap<String, String>? = null
    ) {
        protected var mimeType: String? = null
        val responseHeaders: MutableMap<String, String>

        init {
            val tempResponseHeaders = responseHeaders ?: HashMap()
            tempResponseHeaders["Cache-Control"] = "no-cache"
            this.responseHeaders = tempResponseHeaders
        }

        fun handle(request: WebResourceRequest): InputStream? {
            return handle(request.url)
        }

        abstract fun handle(url: Uri): InputStream?
    }

    /**
     * Attempt to retrieve the WebResourceResponse associated with the given
     * `request`.
     * This method should be invoked from within
     * [android.webkit.WebViewClient.shouldInterceptRequest].
     *
     * @param request the request to process.
     * @return a response if the request URL had a matching handler, null if no
     * handler was found.
     */
    fun shouldInterceptRequest(request: WebResourceRequest): WebResourceResponse? {
        val loadingUrl = request.url

        if (loadingUrl.toString().endsWith("#image")) {
            val headers: MutableMap<String, String> = HashMap(request.requestHeaders)
            headers.remove("x-requested-with")

            try {
                val url = URL(loadingUrl.toString())
                val connection = url.openConnection() as HttpURLConnection

                for ((key, value) in headers) {
                    connection.setRequestProperty(key, value)
                }

                var contentType = connection.contentType
                if (contentType == null) {
                    contentType = "image/unknown"
                }

                val inputStream = connection.inputStream

                return WebResourceResponse(contentType, "UTF-8", inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (loadingUrl.toString().endsWith("#resolve")) {
            val headers: MutableMap<String, String> = HashMap(request.requestHeaders)
            headers.remove("x-requested-with")

            try {
                val url = URL(loadingUrl.toString())
                val connection = url.openConnection() as HttpURLConnection

                for ((key, value) in headers) {
                    connection.setRequestProperty(key, value)
                }

                connection.instanceFollowRedirects = false
                connection.connect()

                val locationHeader = connection.getHeaderField("Location")
                val resolvedUrl =
                    if ((locationHeader != null)) locationHeader else loadingUrl.toString()

                val response = WebResourceResponse(
                    "text/plain", "UTF-8",
                    ByteArrayInputStream(resolvedUrl.toByteArray(StandardCharsets.UTF_8))
                )

                val responseHeaders: MutableMap<String?, String> = HashMap()
                for ((key, value) in connection.headerFields) {
                    if (key != null && value != null && !value.isEmpty()) {
                        responseHeaders[key] = value[0]
                    }
                }

                responseHeaders["x-location"] = resolvedUrl
                responseHeaders["Access-Control-Allow-Origin"] = "*"
                responseHeaders["Access-Control-Allow-Methods"] = "GET, POST, OPTIONS"
                response.responseHeaders = responseHeaders

                return response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (loadingUrl.toString().endsWith("#animevsub-vsub_extra")) {
            val headers: MutableMap<String, String> = HashMap(request.requestHeaders)
            headers.remove("x-requested-with")
            headers["referer"] = "https://animevietsub.tv"

            try {
                val url = URL(loadingUrl.toString())
                val connection = url.openConnection() as HttpURLConnection

                for ((key, value) in headers) {
                    connection.setRequestProperty(key, value)
                }

                connection.instanceFollowRedirects = false
                connection.connect()

                val locationHeader = connection.getHeaderField("Location")
                val resolvedUrl =
                    if ((locationHeader != null)) locationHeader else loadingUrl.toString()

                val responseHeaders: MutableMap<String, String> = HashMap()
                // for (Map.Entry<String, List<String>> entry :
                // connection.getHeaderFields().entrySet()) {
                // if (entry.getKey() != null && entry.getValue() != null &&
                // !entry.getValue().isEmpty()) {
                // responseHeaders.put(entry.getKey(), entry.getValue().get(0));
                // }
                // }
                //
                responseHeaders["Access-Control-Allow-Origin"] = "*"
                responseHeaders["Access-Control-Allow-Methods"] =
                    "PUT, GET, HEAD, POST, DELETE, OPTIONS"

                // responseHeaders.put("Location", resolvedUrl);
                responseHeaders["W-Location"] = resolvedUrl

                val response = WebResourceResponse(
                    "text/plain", "UTF-8",
                    ByteArrayInputStream(resolvedUrl.toByteArray(StandardCharsets.UTF_8))
                )

                response.responseHeaders = responseHeaders

                return response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (null != loadingUrl.path &&
            (loadingUrl.path!!.startsWith(Bridge.CAPACITOR_HTTP_INTERCEPTOR_START) ||
                    loadingUrl.path!!.startsWith(Bridge.CAPACITOR_HTTPS_INTERCEPTOR_START))
        ) {
            Logger.Companion.debug("Handling CapacitorHttp request: $loadingUrl")
            try {
                return handleCapacitorHttpRequest(request)
            } catch (e: Exception) {
                Logger.Companion.error(e.localizedMessage)
                return null
            }
        }

        var handler: PathHandler
        synchronized(uriMatcher) {
            handler = uriMatcher.match(request.url) as PathHandler
        }
        if (handler == null) {
            return null
        }

        if (isLocalFile(loadingUrl) || isMainUrl(loadingUrl) || !isAllowedUrl(loadingUrl) || isErrorUrl(
                loadingUrl
            )
        ) {
            Logger.Companion.debug("Handling local request: " + request.url.toString())
            return handleLocalRequest(request, handler)
        } else {
            return handleProxyRequest(request, handler)
        }
    }

    private fun isLocalFile(uri: Uri): Boolean {
        val path = uri.path
        return path!!.startsWith(capacitorContentStart) || path.startsWith(capacitorFileStart)
    }

    private fun isErrorUrl(uri: Uri): Boolean {
        val url = uri.toString()
        return url == bridge.errorUrl
    }

    private fun isMainUrl(loadingUrl: Uri): Boolean {
        return (bridge.serverUrl == null && loadingUrl.host.equals(bridge.host, ignoreCase = true))
    }

    private fun isAllowedUrl(loadingUrl: Uri): Boolean {
        return !(bridge.serverUrl == null && !bridge.appAllowNavigationMask!!.matches(loadingUrl.host))
    }

    private fun getReasonPhraseFromResponseCode(code: Int): String {
        return when (code) {
            100 -> "Continue"
            101 -> "Switching Protocols"
            200 -> "OK"
            201 -> "Created"
            202 -> "Accepted"
            203 -> "Non-Authoritative Information"
            204 -> "No Content"
            205 -> "Reset Content"
            206 -> "Partial Content"
            300 -> "Multiple Choices"
            301 -> "Moved Permanently"
            302 -> "Found"
            303 -> "See Other"
            304 -> "Not Modified"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Not Found"
            405 -> "Method Not Allowed"
            406 -> "Not Acceptable"
            407 -> "Proxy Authentication Required"
            408 -> "Request Timeout"
            409 -> "Conflict"
            410 -> "Gone"
            500 -> "Internal Server Error"
            501 -> "Not Implemented"
            502 -> "Bad Gateway"
            503 -> "Service Unavailable"
            504 -> "Gateway Timeout"
            505 -> "HTTP Version Not Supported"
            else -> "Unknown"
        }
    }

    @Throws(IOException::class)
    private fun handleCapacitorHttpRequest(request: WebResourceRequest): WebResourceResponse {
        val isHttps = (request.url.path != null
                && request.url.path!!.startsWith(Bridge.CAPACITOR_HTTPS_INTERCEPTOR_START))

        var urlString: String? = request
            .url
            .toString()
            .replace(bridge.localUrl!!, if (isHttps) "https:/" else "http:/")
            .replace(Bridge.CAPACITOR_HTTP_INTERCEPTOR_START, "")
            .replace(Bridge.CAPACITOR_HTTPS_INTERCEPTOR_START, "")
        urlString = URLDecoder.decode(urlString, "UTF-8")
        val url = URL(urlString)
        val headers = JSObject()

        for ((key, value) in request.requestHeaders) {
            headers.put(key, value)
        }

        val connectionBuilder = HttpURLConnectionBuilder()
            .setUrl(url)
            .setMethod(request.method)
            .setHeaders(headers)
            .openConnection()

        val connection = connectionBuilder.build()

        if (!HttpRequestHandler.isDomainExcludedFromSSL(bridge, url)) {
            connection.setSSLSocketFactory(bridge)
        }

        connection.connect()

        var mimeType: String? = null
        var encoding: String? = null
        val responseHeaders: MutableMap<String, String> = LinkedHashMap()
        for ((key, value1) in connection.headerFields) {
            val builder = StringBuilder()
            for (value in value1) {
                builder.append(value)
                builder.append(", ")
            }
            builder.setLength(builder.length - 2)

            if ("Content-Type".equals(key, ignoreCase = true)) {
                val contentTypeParts =
                    builder.toString().split(";".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                mimeType = contentTypeParts[0].trim { it <= ' ' }
                if (contentTypeParts.size > 1) {
                    val encodingParts =
                        contentTypeParts[1].split("=".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    if (encodingParts.size > 1) {
                        encoding = encodingParts[1].trim { it <= ' ' }
                    }
                }
            } else {
                responseHeaders[key] = builder.toString()
            }
        }

        var inputStream = connection.errorStream
        if (inputStream == null) {
            inputStream = connection.inputStream
        }

        if (null == mimeType) {
            mimeType = getMimeType(request.url.path, inputStream)
        }

        val responseCode = connection.responseCode
        val reasonPhrase = getReasonPhraseFromResponseCode(responseCode)

        return WebResourceResponse(
            mimeType,
            encoding,
            responseCode,
            reasonPhrase,
            responseHeaders,
            inputStream
        )
    }

    private fun handleLocalRequest(
        request: WebResourceRequest,
        handler: PathHandler
    ): WebResourceResponse? {
        val path = request.url.path

        if (request.requestHeaders["Range"] != null) {
            val responseStream: InputStream = LollipopLazyInputStream(handler, request)
            val mimeType = getMimeType(path, responseStream)
            val tempResponseHeaders = handler.responseHeaders
            var statusCode = 206
            try {
                val totalRange = responseStream.available()
                val rangeString = request.requestHeaders["Range"]
                val parts =
                    rangeString!!.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val streamParts =
                    parts[1].split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val fromRange = streamParts[0]
                var range = totalRange - 1
                if (streamParts.size > 1) {
                    range = streamParts[1].toInt()
                }
                tempResponseHeaders["Accept-Ranges"] = "bytes"
                tempResponseHeaders["Content-Range"] = "bytes $fromRange-$range/$totalRange"
            } catch (e: IOException) {
                statusCode = 404
            }
            return WebResourceResponse(
                mimeType,
                handler.encoding,
                statusCode,
                handler.reasonPhrase,
                tempResponseHeaders,
                responseStream
            )
        }

        if (isLocalFile(request.url) || isErrorUrl(request.url)) {
            val responseStream: InputStream = LollipopLazyInputStream(handler, request)
            val mimeType = getMimeType(request.url.path, responseStream)
            val statusCode = getStatusCode(responseStream, handler.statusCode)
            return WebResourceResponse(
                mimeType,
                handler.encoding,
                statusCode,
                handler.reasonPhrase,
                handler.responseHeaders,
                responseStream
            )
        }

        if (path == "/cordova.js") {
            return WebResourceResponse(
                "application/javascript",
                handler.encoding,
                handler.statusCode,
                handler.reasonPhrase,
                handler.responseHeaders,
                null
            )
        }

        if (path == "/" || (!request.url.lastPathSegment!!.contains(".") && html5mode)) {
            var responseStream: InputStream?
            try {
                var startPath = this.basePath + "/index.html"
                if (bridge.routeProcessor != null) {
                    val processedRoute =
                        bridge.routeProcessor!!.process(this.basePath, "/index.html")
                    startPath = processedRoute.path
                    isAsset = processedRoute!!.isAsset
                }

                responseStream = if (isAsset) {
                    protocolHandler.openAsset(startPath)
                } else {
                    protocolHandler.openFile(startPath)
                }
            } catch (e: IOException) {
                Logger.Companion.error("Unable to open index.html", e)
                return null
            }

            responseStream = jsInjector.getInjectedStream(responseStream)

            val statusCode = getStatusCode(responseStream, handler.statusCode)
            return WebResourceResponse(
                "text/html",
                handler.encoding,
                statusCode,
                handler.reasonPhrase,
                handler.responseHeaders,
                responseStream
            )
        }

        if ("/favicon.ico".equals(path, ignoreCase = true)) {
            try {
                return WebResourceResponse("image/png", null, null)
            } catch (e: Exception) {
                Logger.Companion.error("favicon handling failed", e)
            }
        }

        val periodIndex = path!!.lastIndexOf(".")
        if (periodIndex >= 0) {
            val ext = path.substring(path.lastIndexOf("."))

            var responseStream: InputStream? = LollipopLazyInputStream(handler, request)

            // TODO: Conjure up a bit more subtlety than this
            if (ext == ".html") {
                responseStream = jsInjector.getInjectedStream(responseStream)
            }

            val mimeType = getMimeType(path, responseStream)
            val statusCode = getStatusCode(responseStream, handler.statusCode)
            return WebResourceResponse(
                mimeType,
                handler.encoding,
                statusCode,
                handler.reasonPhrase,
                handler.responseHeaders,
                responseStream
            )
        }

        return null
    }

    /**
     * Instead of reading files from the filesystem/assets, proxy through to the URL
     * and let an external server handle it.
     *
     * @param request
     * @param handler
     * @return
     */
    private fun handleProxyRequest(
        request: WebResourceRequest,
        handler: PathHandler
    ): WebResourceResponse? {
        val method = request.method
        if (method == "GET") {
            try {
                val url = request.url.toString()
                val headers = request.requestHeaders
                var isHtmlText = false
                for ((key, value) in headers) {
                    if (key.equals("Accept", ignoreCase = true)
                        && value.lowercase(Locale.getDefault()).contains("text/html")
                    ) {
                        isHtmlText = true
                        break
                    }
                }
                if (isHtmlText) {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    for ((key, value) in headers) {
                        conn.setRequestProperty(key, value)
                    }
                    val getCookie = CookieManager.getInstance().getCookie(url)
                    if (getCookie != null) {
                        conn.setRequestProperty("Cookie", getCookie)
                    }
                    conn.requestMethod = method
                    conn.readTimeout = 30 * 1000
                    conn.connectTimeout = 30 * 1000
                    if (request.url.userInfo != null) {
                        val userInfoBytes =
                            request.url.userInfo!!.toByteArray(StandardCharsets.UTF_8)
                        val base64 = Base64.encodeToString(userInfoBytes, Base64.NO_WRAP)
                        conn.setRequestProperty("Authorization", "Basic $base64")
                    }

                    val cookies = conn.headerFields["Set-Cookie"]
                    if (cookies != null) {
                        for (cookie in cookies) {
                            CookieManager.getInstance().setCookie(url, cookie)
                        }
                    }
                    var responseStream = conn.inputStream
                    responseStream = jsInjector.getInjectedStream(responseStream)
                    return WebResourceResponse(
                        "text/html",
                        handler.encoding,
                        handler.statusCode,
                        handler.reasonPhrase,
                        handler.responseHeaders,
                        responseStream
                    )
                }
            } catch (ex: Exception) {
                bridge.handleAppUrlLoadError(ex)
            }
        }
        return null
    }

    private fun getMimeType(path: String?, stream: InputStream?): String? {
        var mimeType: String? = null
        try {
            mimeType = URLConnection.guessContentTypeFromName(path) // Does not recognize *.js
            if (mimeType != null && path!!.endsWith(".js") && mimeType == "image/x-icon") {
                Logger.Companion.debug("We shouldn't be here")
            }
            if (mimeType == null) {
                mimeType = if (path!!.endsWith(".js") || path.endsWith(".mjs")) {
                    // Make sure JS files get the proper mimetype to support ES modules
                    "application/javascript"
                } else if (path.endsWith(".wasm")) {
                    "application/wasm"
                } else {
                    URLConnection.guessContentTypeFromStream(stream)
                }
            }
        } catch (ex: Exception) {
            Logger.Companion.error("Unable to get mime type$path", ex)
        }
        return mimeType
    }

    private fun getStatusCode(stream: InputStream?, defaultCode: Int): Int {
        var finalStatusCode = defaultCode
        try {
            if (stream!!.available() == -1) {
                finalStatusCode = 404
            }
        } catch (e: IOException) {
            finalStatusCode = 500
        }
        return finalStatusCode
    }

    /**
     * Registers a handler for the given `uri`. The `handler`
     * will be invoked
     * every time the `shouldInterceptRequest` method of the instance is
     * called with
     * a matching `uri`.
     *
     * @param uri     the uri to use the handler for. The scheme and authority
     * (domain) will be matched
     * exactly. The path may contain a '*' element which will match a
     * single element of
     * a path (so a handler registered for /a/ * will be invoked for
     * /a/b and /a/c.html
     * but not for /a/b/b) or the '**' element which will match any
     * number of path
     * elements.
     * @param handler the handler to use for the uri.
     */
    fun register(uri: Uri, handler: PathHandler?) {
        synchronized(uriMatcher) {
            uriMatcher.addURI(uri.scheme, uri.authority, uri.path, handler)
        }
    }

    /**
     * Hosts the application's assets on an https:// URL. Assets from the local path
     * `assetPath/...` will be available under
     * `https://{uuid}.androidplatform.net/assets/...`.
     *
     * @param assetPath the local path in the application's asset folder which will
     * be made
     * available by the server (for example "/www").
     * @return prefixes under which the assets are hosted.
     */
    fun hostAssets(assetPath: String?) {
        this.isAsset = true
        this.basePath = assetPath
        createHostingDetails()
    }

    /**
     * Hosts the application's files on an https:// URL. Files from the basePath
     * `basePath/...` will be available under
     * `https://{uuid}.androidplatform.net/...`.
     *
     * @param basePath the local path in the application's data folder which will be
     * made
     * available by the server (for example "/www").
     * @return prefixes under which the assets are hosted.
     */
    fun hostFiles(basePath: String?) {
        this.isAsset = false
        this.basePath = basePath
        createHostingDetails()
    }

    private fun createHostingDetails() {
        val assetPath = this.basePath

        require(assetPath!!.indexOf('*') == -1) { "assetPath cannot contain the '*' character." }

        val handler: PathHandler = object : PathHandler() {
            override fun handle(url: Uri): InputStream? {
                var stream: InputStream? = null
                var path = url.path

                // Pass path to routeProcessor if present
                val routeProcessor = bridge.routeProcessor
                var ignoreAssetPath = false
                if (routeProcessor != null) {
                    val processedRoute = bridge.routeProcessor!!.process("", path)
                    path = processedRoute.path
                    isAsset = processedRoute!!.isAsset
                    ignoreAssetPath = processedRoute.isIgnoreAssetPath
                }

                try {
                    if (path!!.startsWith(capacitorContentStart)) {
                        stream = protocolHandler.openContentUrl(url)
                    } else if (path.startsWith(capacitorFileStart)) {
                        stream = protocolHandler.openFile(path)
                    } else if (!isAsset) {
                        if (routeProcessor == null) {
                            path = basePath + url.path
                        }

                        stream = protocolHandler.openFile(path)
                    } else if (ignoreAssetPath) {
                        stream = protocolHandler.openAsset(path)
                    } else {
                        stream = protocolHandler.openAsset(assetPath + path)
                    }
                } catch (e: IOException) {
                    Logger.Companion.error("Unable to open asset URL: $url")
                    return null
                }

                return stream
            }
        }

        for (authority in authorities) {
            registerUriForScheme(Bridge.CAPACITOR_HTTP_SCHEME, handler, authority)
            registerUriForScheme(Bridge.CAPACITOR_HTTPS_SCHEME, handler, authority)

            val customScheme = bridge.scheme
            if (customScheme != Bridge.CAPACITOR_HTTP_SCHEME
                && customScheme != Bridge.CAPACITOR_HTTPS_SCHEME
            ) {
                registerUriForScheme(customScheme, handler, authority)
            }
        }
    }

    private fun registerUriForScheme(scheme: String, handler: PathHandler, authority: String) {
        val uriBuilder = Uri.Builder()
        uriBuilder.scheme(scheme)
        uriBuilder.authority(authority)
        uriBuilder.path("")
        val uriPrefix = uriBuilder.build()

        register(Uri.withAppendedPath(uriPrefix, "/"), handler)
        register(Uri.withAppendedPath(uriPrefix, "**"), handler)
    }

    /**
     * The KitKat WebView reads the InputStream on a separate threadpool. We can use
     * that to
     * parallelize loading.
     */
    private abstract class LazyInputStream(protected val handler: PathHandler) : InputStream() {
        private var `is`: InputStream? = null

        private val inputStream: InputStream?
            get() {
                if (`is` == null) {
                    `is` = handle()
                }
                return `is`
            }

        protected abstract fun handle(): InputStream?

        @Throws(IOException::class)
        override fun available(): Int {
            val `is` = inputStream
            return if ((`is` != null)) `is`.available() else -1
        }

        @Throws(IOException::class)
        override fun read(): Int {
            val `is` = inputStream
            return if ((`is` != null)) `is`.read() else -1
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int {
            val `is` = inputStream
            return if ((`is` != null)) `is`.read(b) else -1
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            val `is` = inputStream
            return if ((`is` != null)) `is`.read(b, off, len) else -1
        }

        @Throws(IOException::class)
        override fun skip(n: Long): Long {
            val `is` = inputStream
            return if ((`is` != null)) `is`.skip(n) else 0
        }
    }

    // For L and above.
    private class LollipopLazyInputStream(
        handler: PathHandler,
        private val request: WebResourceRequest
    ) : LazyInputStream(handler) {
        private val `is`: InputStream? = null

        override fun handle(): InputStream? {
            return handler.handle(request)
        }
    }

    companion object {
        private const val capacitorFileStart = Bridge.CAPACITOR_FILE_START
        private const val capacitorContentStart = Bridge.CAPACITOR_CONTENT_START
        private fun parseAndVerifyUrl(url: String?): Uri? {
            if (url == null) {
                return null
            }
            val uri = Uri.parse(url)
            if (uri == null) {
                Logger.Companion.error("Malformed URL: $url")
                return null
            }
            val path = uri.path
            if (path == null || path.isEmpty()) {
                Logger.Companion.error("URL does not have a path: $url")
                return null
            }
            return uri
        }
    }
}
