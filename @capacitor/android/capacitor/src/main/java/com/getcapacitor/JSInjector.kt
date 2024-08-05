package com.getcapacitor

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets

/**
 * JSInject is responsible for returning Capacitor's core
 * runtime JS and any plugin JS back into HTML page responses
 * to the client.
 */
internal class JSInjector(
    private val globalJS: String,
    private val bridgeJS: String,
    private val pluginJS: String,
    private val cordovaJS: String,
    private val cordovaPluginsJS: String,
    private val cordovaPluginsFileJS: String,
    private val localUrlJS: String
) {
    val scriptString: String
        /**
         * Generates injectable JS content.
         * This may be used in other forms of injecting that aren't using an InputStream.
         * @return
         */
        get() = ("""
    $globalJS

    $localUrlJS

    $bridgeJS

    $pluginJS

    $cordovaJS

    $cordovaPluginsFileJS

    $cordovaPluginsJS
    """.trimIndent()
                )

    /**
     * Given an InputStream from the web server, prepend it with
     * our JS stream
     * @param responseStream
     * @return
     */
    fun getInjectedStream(responseStream: InputStream?): InputStream {
        val js = "<script type=\"text/javascript\">" + scriptString + "</script>"
        var html = this.readAssetStream(responseStream)

        // Insert the js string at the position after <head> or before </head> using StringBuilder
        val modifiedHtml = StringBuilder(html)
        if (html.contains("<head>")) {
            modifiedHtml.insert(
                html.indexOf("<head>") + "<head>".length, """

     $js

     """.trimIndent()
            )
            html = modifiedHtml.toString()
        } else if (html.contains("</head>")) {
            modifiedHtml.insert(
                html.indexOf("</head>"), """

     $js

     """.trimIndent()
            )
            html = modifiedHtml.toString()
        } else {
            Logger.Companion.error("Unable to inject Capacitor, Plugins won't work")
        }
        return ByteArrayInputStream(html.toByteArray(StandardCharsets.UTF_8))
    }

    private fun readAssetStream(stream: InputStream?): String {
        try {
            val bufferSize = 1024
            val buffer = CharArray(bufferSize)
            val out = StringBuilder()
            val `in`: Reader = InputStreamReader(stream, StandardCharsets.UTF_8)
            while (true) {
                val rsz = `in`.read(buffer, 0, buffer.size)
                if (rsz < 0) break
                out.append(buffer, 0, rsz)
            }
            return out.toString()
        } catch (e: Exception) {
            Logger.Companion.error("Unable to process HTML asset file. This is a fatal error", e)
        }

        return ""
    }
}
