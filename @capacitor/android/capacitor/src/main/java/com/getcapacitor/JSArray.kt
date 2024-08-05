package com.getcapacitor

import org.json.JSONArray
import org.json.JSONException

class JSArray : JSONArray {
    constructor() : super()

    constructor(json: String?) : super(json)

    constructor(copyFrom: Collection<*>?) : super(copyFrom)

    constructor(array: Any?) : super(array)

    @Throws(JSONException::class)
    fun <E> toList(): List<E> {
        val items: MutableList<E> = ArrayList()
        var o: Any? = null
        for (i in 0 until this.length()) {
            o = this[i]
            try {
                items.add(this[i] as E)
            } catch (ex: Exception) {
                throw JSONException("Not all items are instances of the given type")
            }
        }
        return items
    }

    companion object {
        /**
         * Create a new JSArray without throwing a error
         */
        fun from(array: Any?): JSArray? {
            try {
                return JSArray(array)
            } catch (ex: JSONException) {
            }
            return null
        }
    }
}
