package com.getcapacitor

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.WebView

class CapacitorWebView(context: Context?, attrs: AttributeSet?) : WebView(
    context!!, attrs
) {
    private var capInputConnection: BaseInputConnection? = null
    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge?) {
        this.bridge = bridge
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val config = if (bridge != null) {
            bridge!!.config
        } else {
            CapConfig.Companion.loadDefault(context)
        }

        val captureInput = config.isInputCaptured
        if (captureInput) {
            if (capInputConnection == null) {
                capInputConnection = BaseInputConnection(this, false)
            }
            return capInputConnection!!
        }
        return super.onCreateInputConnection(outAttrs)
    }

    @Suppress("deprecation")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_MULTIPLE) {
            evaluateJavascript(
                "document.activeElement.value = document.activeElement.value + '" + event.characters + "';",
                null
            )
            return false
        }
        return super.dispatchKeyEvent(event)
    }
}
