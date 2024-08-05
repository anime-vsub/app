package com.getcapacitor

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Wraps a call from the web layer to native
 */
class PluginCall(
    private val msgHandler: MessageHandler,
    val pluginId: String?,
    val callbackId: String?,
    @JvmField val methodName: String?,
    @JvmField val data: JSObject?
) {
    /**
     * Gets the keepAlive value of the plugin call
     * @return true if the plugin call is kept alive
     */
    var isKeptAlive: Boolean = false
        private set

    /**
     * Indicates that this PluginCall was released, and should no longer be used
     */
    @get:Deprecated("")
    @Deprecated("")
    var isReleased: Boolean = false
        private set

    fun successCallback(successResult: PluginResult?) {
        if (CALLBACK_ID_DANGLING == this.callbackId) {
            // don't send back response if the callbackId was "-1"
            return
        }

        msgHandler.sendResponseMessage(this, successResult, null)
    }


    @Deprecated(" Use {@link #resolve(JSObject data)}")
    fun success(data: JSObject) {
        val result = PluginResult(data)
        msgHandler.sendResponseMessage(this, result, null)
    }


    @Deprecated(" Use {@link #resolve()}")
    fun success() {
        this.resolve(JSObject())
    }

    fun resolve(data: JSObject) {
        val result = PluginResult(data)
        msgHandler.sendResponseMessage(this, result, null)
    }

    fun resolve() {
        msgHandler.sendResponseMessage(this, null, null)
    }

    fun errorCallback(msg: String?) {
        val errorResult = PluginResult()

        try {
            errorResult.put("message", msg)
        } catch (jsonEx: Exception) {
            Logger.Companion.error(Logger.Companion.tags("Plugin"), jsonEx.toString(), null)
        }

        msgHandler.sendResponseMessage(this, null, errorResult)
    }


    @Deprecated(" Use {@link #reject(String msg, Exception ex)}")
    fun error(msg: String?, ex: Exception?) {
        reject(msg, ex)
    }


    @Deprecated(" Use {@link #reject(String msg, String code, Exception ex)}")
    fun error(msg: String?, code: String?, ex: Exception?) {
        reject(msg, code, ex)
    }


    @Deprecated(" Use {@link #reject(String msg)}")
    fun error(msg: String?) {
        reject(msg)
    }

    @JvmOverloads
    fun reject(msg: String?, code: String? = null, ex: Exception? = null, data: JSObject? = null) {
        val errorResult = PluginResult()

        if (ex != null) {
            Logger.Companion.error(Logger.Companion.tags("Plugin"), msg, ex)
        }

        try {
            errorResult.put("message", msg)
            errorResult.put("code", code)
            if (null != data) {
                errorResult.put("data", data)
            }
        } catch (jsonEx: Exception) {
            Logger.Companion.error(Logger.Companion.tags("Plugin"), jsonEx.message, jsonEx)
        }

        msgHandler.sendResponseMessage(this, null, errorResult)
    }

    fun reject(msg: String?, ex: Exception?, data: JSObject?) {
        reject(msg, null, ex, data)
    }

    fun reject(msg: String?, code: String?, data: JSObject?) {
        reject(msg, code, null, data)
    }

    fun reject(msg: String?, data: JSObject?) {
        reject(msg, null, null, data)
    }

    fun reject(msg: String?, ex: Exception?) {
        reject(msg, null, ex, null)
    }

    @JvmOverloads
    fun unimplemented(msg: String? = "not implemented") {
        reject(msg, "UNIMPLEMENTED", null, null)
    }

    @JvmOverloads
    fun unavailable(msg: String? = "not available") {
        reject(msg, "UNAVAILABLE", null, null)
    }

    fun getString(name: String?): String? {
        return this.getString(name, null)
    }

    fun getString(name: String?, defaultValue: String?): String? {
        val value = data!!.opt(name) ?: return defaultValue

        if (value is String) {
            return value
        }
        return defaultValue
    }

    fun getInt(name: String?): Int? {
        return this.getInt(name, null)
    }

    fun getInt(name: String?, defaultValue: Int?): Int? {
        val value = data!!.opt(name) ?: return defaultValue

        if (value is Int) {
            return value
        }
        return defaultValue
    }

    fun getLong(name: String?): Long? {
        return this.getLong(name, null)
    }

    fun getLong(name: String?, defaultValue: Long?): Long? {
        val value = data!!.opt(name) ?: return defaultValue

        if (value is Long) {
            return value
        }
        return defaultValue
    }

    fun getFloat(name: String?): Float? {
        return this.getFloat(name, null)
    }

    fun getFloat(name: String?, defaultValue: Float?): Float? {
        val value = data!!.opt(name) ?: return defaultValue

        if (value is Float) {
            return value
        }
        if (value is Double) {
            return value.toFloat()
        }
        if (value is Int) {
            return value.toFloat()
        }
        return defaultValue
    }

    fun getDouble(name: String?): Double? {
        return this.getDouble(name, null)
    }

    fun getDouble(name: String?, defaultValue: Double?): Double? {
        val value = data!!.opt(name) ?: return defaultValue

        if (value is Double) {
            return value
        }
        if (value is Float) {
            return value.toDouble()
        }
        if (value is Int) {
            return value.toDouble()
        }
        return defaultValue
    }

    fun getBoolean(name: String?): Boolean? {
        return this.getBoolean(name, null)
    }

    fun getBoolean(name: String?, defaultValue: Boolean?): Boolean? {
        val value = data!!.opt(name) ?: return defaultValue

        if (value is Boolean) {
            return value
        }
        return defaultValue
    }

    fun getObject(name: String?): JSObject? {
        return this.getObject(name, null)
    }

    fun getObject(name: String?, defaultValue: JSObject?): JSObject? {
        val value = data!!.opt(name) ?: return defaultValue

        if (value is JSONObject) {
            return try {
                JSObject.Companion.fromJSONObject(value)
            } catch (ex: JSONException) {
                defaultValue
            }
        }
        return defaultValue
    }

    fun getArray(name: String?): JSArray? {
        return this.getArray(name, null)
    }

    /**
     * Get a JSONArray and turn it into a JSArray
     * @param name
     * @param defaultValue
     * @return
     */
    fun getArray(name: String?, defaultValue: JSArray?): JSArray? {
        val value = data!!.opt(name) ?: return defaultValue

        if (value is JSONArray) {
            try {
                val valueArray = value
                val items: MutableList<Any> = ArrayList()
                for (i in 0 until valueArray.length()) {
                    items.add(valueArray[i])
                }
                return JSArray(items.toTypedArray())
            } catch (ex: JSONException) {
                return defaultValue
            }
        }
        return defaultValue
    }

    /**
     * @param name of the option to check
     * @return boolean indicating if the plugin call has an option for the provided name.
     */
    @Deprecated(
        """Presence of a key should not be considered significant.
      Use typed accessors to check the value instead."""
    )
    fun hasOption(name: String?): Boolean {
        return data!!.has(name)
    }

    /**
     * Indicate that the Bridge should cache this call in order to call
     * it again later. For example, the addListener system uses this to
     * continuously call the call's callback (😆).
     */
    @Deprecated("use {@link #setKeepAlive(Boolean)} instead")
    fun save() {
        setKeepAlive(true)
    }

    /**
     * Indicate that the Bridge should cache this call in order to call
     * it again later. For example, the addListener system uses this to
     * continuously call the call's callback.
     *
     * @param keepAlive whether to keep the callback saved
     */
    fun setKeepAlive(keepAlive: Boolean) {
        this.isKeptAlive = keepAlive
    }

    fun release(bridge: Bridge?) {
        this.isKeptAlive = false
        bridge!!.releaseCall(this)
        this.isReleased = true
    }

    @get:Deprecated(
        """use {@link #isKeptAlive()}
      """
    )
    val isSaved: Boolean
        /**
         * @return true if the plugin call is kept alive
         */
        get() = isKeptAlive

    internal inner class PluginCallDataTypeException(m: String?) : Exception(m)
    companion object {
        /**
         * A special callback id that indicates there is no matching callback
         * on the client to associate any PluginCall results back to. This is used
         * in the case of an app resuming with saved instance data, for example.
         */
        const val CALLBACK_ID_DANGLING: String = "-1"
    }
}
