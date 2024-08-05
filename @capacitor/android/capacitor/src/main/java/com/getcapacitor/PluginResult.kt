package com.getcapacitor

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * Wraps a result for web from calling a native plugin.
 */
class PluginResult @JvmOverloads constructor(private val json: JSObject = JSObject()) {
    fun put(name: String, value: Boolean): PluginResult {
        return this.jsonPut(name, value)
    }

    fun put(name: String, value: Double): PluginResult {
        return this.jsonPut(name, value)
    }

    fun put(name: String, value: Int): PluginResult {
        return this.jsonPut(name, value)
    }

    fun put(name: String, value: Long): PluginResult {
        return this.jsonPut(name, value)
    }

    /**
     * Format a date as an ISO string
     */
    fun put(name: String, value: Date?): PluginResult {
        val tz = TimeZone.getTimeZone("UTC")
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        df.timeZone = tz
        return this.jsonPut(name, df.format(value))
    }

    fun put(name: String, value: Any?): PluginResult {
        return this.jsonPut(name, value!!)
    }

    fun put(name: String, value: PluginResult?): PluginResult {
        return this.jsonPut(name, value!!.json)
    }

    fun jsonPut(name: String, value: Any): PluginResult {
        try {
            json.put(name, value)
        } catch (ex: Exception) {
            Logger.Companion.error(Logger.Companion.tags("Plugin"), "", ex)
        }
        return this
    }

    override fun toString(): String {
        return json.toString()
    }

    val wrappedResult: JSObject
        /**
         * Return plugin metadata and information about the result, if it succeeded the data, or error information if it didn't.
         * This is used for appRestoredResult, as it's technically a raw data response from a plugin.
         * @return the raw data response from the plugin.
         */
        get() {
            val ret = JSObject()
            ret.put("pluginId", json.getString("pluginId"))
            ret.put("methodName", json.getString("methodName"))
            ret.put("success", json.getBoolean("success", false)!!)
            ret.put("data", json.getJSObject("data")!!)
            ret.put("error", json.getJSObject("error")!!)
            return ret
        }
}
