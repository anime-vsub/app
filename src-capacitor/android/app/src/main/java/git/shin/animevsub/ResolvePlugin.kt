package git.shin.animevsub

import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture

@CapacitorPlugin(name = "Resolve")
class ResolvePlugin : Plugin() {
    @PluginMethod
    fun echo(call: PluginCall) {
        val value = call.getString("value")

        val ret = JSObject()
        ret.put("value", value)
        call.resolve(ret)
    }

    @PluginMethod
    fun resolve(call: PluginCall) {
        val url = call.getString("url")
        val headers = call.getObject("headers")

        if (url == null) {
            call.reject("Must provide a URL")
            return
        }

        val ret = resolveUrl(url, headers)
        if (ret.has("error")) {
            call.reject(ret.getString("error"))
        } else {
            call.resolve(ret)
        }
    }

    @PluginMethod
    fun resolveAll(call: PluginCall) {
        val urlArray = call.getArray("urls")

        //    JSArray headersArray = call.getArray("headers") ?? [];
        if (urlArray == null) {
            call.reject("Must provide an array of URLs")
            return
        }

        try {
            val futures: List<CompletableFuture<JSObject>> = ArrayList()
            val urls = urlArray.toList<JSONObject>()

            val results: MutableList<JSObject?> = ArrayList()

            for (jsonObject in urls) {
                val urlObject = JSObject.fromJSONObject(jsonObject)

                val url = urlObject.getString("url")
                val headers = urlObject.getJSObject("headers")

                results.add(resolveUrl(url, headers))
            }

            call.resolve(JSObject().put("results", JSArray(results)))
        } catch (e: JSONException) {
            call.reject("Error processing URLs array: " + e.message)
        }
    }

    private fun resolveUrl(url: String?, headers: JSObject?): JSObject {
        val result = JSObject()

        try {
            val obj = URL(url)
            val connection = obj.openConnection() as HttpURLConnection

            // Set headers
            if (headers != null) {
                val keys = headers.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    connection.setRequestProperty(key, headers.getString(key))
                }
            }

            connection.instanceFollowRedirects = false
            connection.connect()

            val locationHeader = connection.getHeaderField("Location")
            val resolvedUrl = if ((locationHeader != null)) locationHeader else url!!

            result.put("url", resolvedUrl)
        } catch (e: IOException) {
            result.put("error", e.message)
        }

        return result
    }
}
