package com.getcapacitor

import org.json.JSONException
import org.json.JSONObject

/**
 * A wrapper around JSONObject that isn't afraid to do simple
 * JSON put operations without having to throw an exception
 * for every little thing jeez
 */
open class JSObject : JSONObject {
    constructor() : super()

    constructor(json: String?) : super(json)

    constructor(obj: JSONObject?, names: Array<String>?) : super(obj, names)

    override fun getString(key: String): String? {
        return getString(key, null)
    }

    fun getString(key: String?, defaultValue: String?): String? {
        try {
            val value = super.getString(key)
            if (!super.isNull(key)) {
                return value
            }
        } catch (ex: JSONException) {
        }
        return defaultValue
    }

    fun getInteger(key: String?): Int? {
        return getInteger(key, null)
    }

    fun getInteger(key: String?, defaultValue: Int?): Int? {
        try {
            return super.getInt(key)
        } catch (e: JSONException) {
        }
        return defaultValue
    }

    fun getBoolean(key: String?, defaultValue: Boolean?): Boolean? {
        try {
            return super.getBoolean(key)
        } catch (e: JSONException) {
        }
        return defaultValue
    }

    /**
     * Fetch boolean from jsonObject
     */
    fun getBool(key: String?): Boolean? {
        return getBoolean(key, null)
    }

    fun getJSObject(name: String?): JSObject? {
        try {
            return getJSObject(name, null)
        } catch (e: JSONException) {
        }
        return null
    }

    @Throws(JSONException::class)
    fun getJSObject(name: String?, defaultValue: JSObject?): JSObject? {
        try {
            val obj = get(name)
            if (obj is JSONObject) {
                val keysIter = obj.keys()
                val keys: MutableList<String> = ArrayList()
                while (keysIter.hasNext()) {
                    keys.add(keysIter.next())
                }

                return JSObject(obj, keys.toTypedArray<String>())
            }
        } catch (ex: JSONException) {
        }
        return defaultValue
    }

    override fun put(key: String, value: Boolean): JSObject {
        try {
            super.put(key, value)
        } catch (ex: JSONException) {
        }
        return this
    }

    override fun put(key: String, value: Int): JSObject {
        try {
            super.put(key, value)
        } catch (ex: JSONException) {
        }
        return this
    }

    override fun put(key: String, value: Long): JSObject {
        try {
            super.put(key, value)
        } catch (ex: JSONException) {
        }
        return this
    }

    override fun put(key: String, value: Double): JSObject {
        try {
            super.put(key, value)
        } catch (ex: JSONException) {
        }
        return this
    }

    override fun put(key: String, value: Any): JSObject {
        try {
            super.put(key, value)
        } catch (ex: JSONException) {
        }
        return this
    }

    fun put(key: String?, value: String?): JSObject {
        try {
            super.put(key, value)
        } catch (ex: JSONException) {
        }
        return this
    }

    @Throws(JSONException::class)
    fun putSafe(key: String?, value: Any?): JSObject {
        return super.put(key, value) as JSObject
    }

    companion object {
        /**
         * Convert a pathetic JSONObject into a JSObject
         * @param obj
         */
        @Throws(JSONException::class)
        fun fromJSONObject(obj: JSONObject): JSObject {
            val keysIter = obj.keys()
            val keys: MutableList<String> = ArrayList()
            while (keysIter.hasNext()) {
                keys.add(keysIter.next())
            }

            return JSObject(obj, keys.toTypedArray<String>())
        }
    }
}
