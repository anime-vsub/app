package com.getcapacitor

import org.json.JSONException

/**
 * Represents a single user-data value of any type on the capacitor PluginCall object.
 */
class JSValue(call: PluginCall, name: String) {
    /**
     * Returns the coerced but uncasted underlying value.
     */
    @JvmField
    val value: Any

    /**
     * @param call The capacitor plugin call, used for accessing the value safely.
     * @param name The name of the property to access.
     */
    init {
        this.value = this.toValue(call, name)
    }

    override fun toString(): String {
        return value.toString()
    }

    /**
     * Returns the underlying value as a JSObject, or throwing if it cannot.
     *
     * @throws JSONException If the underlying value is not a JSObject.
     */
    @Throws(JSONException::class)
    fun toJSObject(): JSObject {
        if (value is JSObject) return value
        throw JSONException("JSValue could not be coerced to JSObject.")
    }

    /**
     * Returns the underlying value as a JSArray, or throwing if it cannot.
     *
     * @throws JSONException If the underlying value is not a JSArray.
     */
    @Throws(JSONException::class)
    fun toJSArray(): JSArray {
        if (value is JSArray) return value
        throw JSONException("JSValue could not be coerced to JSArray.")
    }

    /**
     * Returns the underlying value this object represents, coercing it into a capacitor-friendly object if supported.
     */
    private fun toValue(call: PluginCall, name: String): Any {
        var value: Any? = null
        value = call.getArray(name, null)
        if (value != null) return value
        value = call.getObject(name, null)
        if (value != null) return value
        value = call.getString(name, null)
        if (value != null) return value
        return call.data.opt(name)
    }
}
