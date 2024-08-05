package com.getcapacitor

import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.webkit.JavaScriptReplyProxy
import androidx.webkit.WebMessageCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewCompat.WebMessageListener
import androidx.webkit.WebViewFeature
import org.apache.cordova.PluginManager

/**
 * MessageHandler handles messages from the WebView, dispatching them
 * to plugins.
 */
class MessageHandler(
    private val bridge: Bridge,
    private val webView: WebView,
    private val cordovaPluginManager: PluginManager
) {
    private var javaScriptReplyProxy: JavaScriptReplyProxy? = null

    init {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER) && !bridge.config.isUsingLegacyBridge) {
            val capListener =
                WebMessageListener { view: WebView?, message: WebMessageCompat, sourceOrigin: Uri?, isMainFrame: Boolean, replyProxy: JavaScriptReplyProxy? ->
                    if (isMainFrame) {
                        postMessage(message.data)
                        javaScriptReplyProxy = replyProxy
                    } else {
                        Logger.Companion.warn("Plugin execution is allowed in Main Frame only")
                    }
                }
            try {
                WebViewCompat.addWebMessageListener(
                    webView,
                    "androidBridge",
                    bridge.getAllowedOriginRules(),
                    capListener
                )
            } catch (ex: Exception) {
                webView.addJavascriptInterface(this, "androidBridge")
            }
        } else {
            webView.addJavascriptInterface(this, "androidBridge")
        }
    }

    /**
     * The main message handler that will be called from JavaScript
     * to send a message to the native bridge.
     * @param jsonStr
     */
    @JavascriptInterface
    @Suppress("unused")
    fun postMessage(jsonStr: String?) {
        try {
            val postData = JSObject(jsonStr)

            val type = postData.getString("type")

            val typeIsNotNull = type != null
            val isCordovaPlugin = typeIsNotNull && type == "cordova"
            val isJavaScriptError = typeIsNotNull && type == "js.error"

            val callbackId = postData.getString("callbackId")

            if (isCordovaPlugin) {
                val service = postData.getString("service")
                val action = postData.getString("action")
                val actionArgs = postData.getString("actionArgs")

                Logger.Companion.verbose(
                    Logger.Companion.tags("Plugin"),
                    "To native (Cordova plugin): callbackId: " +
                            callbackId +
                            ", service: " +
                            service +
                            ", action: " +
                            action +
                            ", actionArgs: " +
                            actionArgs
                )

                this.callCordovaPluginMethod(callbackId, service, action, actionArgs)
            } else if (isJavaScriptError) {
                Logger.Companion.error("JavaScript Error: $jsonStr")
            } else {
                val pluginId = postData.getString("pluginId")
                val methodName = postData.getString("methodName")
                val methodData = postData.getJSObject("options", JSObject())

                Logger.Companion.verbose(
                    Logger.Companion.tags("Plugin"),
                    "To native (Capacitor plugin): callbackId: $callbackId, pluginId: $pluginId, methodName: $methodName"
                )

                this.callPluginMethod(callbackId, pluginId, methodName, methodData)
            }
        } catch (ex: Exception) {
            Logger.Companion.error("Post message error:", ex)
        }
    }

    fun sendResponseMessage(
        call: PluginCall,
        successResult: PluginResult?,
        errorResult: PluginResult?
    ) {
        try {
            val data = PluginResult()
            data.put("save", call.isKeptAlive)
            data.put("callbackId", call.callbackId)
            data.put("pluginId", call.pluginId)
            data.put("methodName", call.methodName)

            val pluginResultInError = errorResult != null
            if (pluginResultInError) {
                data.put("success", false)
                data.put("error", errorResult)
                Logger.Companion.debug("Sending plugin error: $data")
            } else {
                data.put("success", true)
                if (successResult != null) {
                    data.put("data", successResult)
                }
            }

            val isValidCallbackId = call.callbackId != PluginCall.Companion.CALLBACK_ID_DANGLING
            if (isValidCallbackId) {
                if (bridge.config.isUsingLegacyBridge) {
                    legacySendResponseMessage(data)
                } else if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER) && javaScriptReplyProxy != null) {
                    javaScriptReplyProxy.postMessage(data.toString())
                } else {
                    legacySendResponseMessage(data)
                }
            } else {
                bridge.app.fireRestoredResult(data)
            }
        } catch (ex: Exception) {
            Logger.Companion.error("sendResponseMessage: error: $ex")
        }
        if (!call.isKeptAlive) {
            call.release(bridge)
        }
    }

    private fun legacySendResponseMessage(data: PluginResult) {
        val runScript = "window.Capacitor.fromNative($data)"
        val webView = this.webView
        webView.post { webView.evaluateJavascript(runScript, null) }
    }

    private fun callPluginMethod(
        callbackId: String?,
        pluginId: String?,
        methodName: String?,
        methodData: JSObject?
    ) {
        val call = PluginCall(this, pluginId, callbackId, methodName, methodData)
        bridge.callPluginMethod(pluginId!!, methodName!!, call)
    }

    private fun callCordovaPluginMethod(
        callbackId: String?,
        service: String?,
        action: String?,
        actionArgs: String?
    ) {
        bridge.execute {
            cordovaPluginManager.exec(service, action, callbackId, actionArgs)
        }
    }
}
