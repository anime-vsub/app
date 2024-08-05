package com.getcapacitor

import android.content.Context
import android.text.TextUtils
import com.getcapacitor.JSExportException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object JSExport {
    private const val CATCHALL_OPTIONS_PARAM = "_options"
    private const val CALLBACK_PARAM = "_callback"

    fun getGlobalJS(context: Context?, loggingEnabled: Boolean, isDebug: Boolean): String {
        return "window.Capacitor = { DEBUG: $isDebug, isLoggingEnabled: $loggingEnabled, Plugins: {} };"
    }

    fun getCordovaJS(context: Context): String? {
        var fileContent: String? = ""
        try {
            fileContent = FileUtils.readFileFromAssets(context.assets, "public/cordova.js")
        } catch (ex: IOException) {
            Logger.Companion.error("Unable to read public/cordova.js file, Cordova plugins will not work")
        }
        return fileContent
    }

    fun getCordovaPluginsFileJS(context: Context): String? {
        var fileContent: String? = ""
        try {
            fileContent = FileUtils.readFileFromAssets(context.assets, "public/cordova_plugins.js")
        } catch (ex: IOException) {
            Logger.Companion.error("Unable to read public/cordova_plugins.js file, Cordova plugins will not work")
        }
        return fileContent
    }

    fun getPluginJS(plugins: Collection<PluginHandle>): String {
        val lines: MutableList<String?> = ArrayList()
        val pluginArray = JSONArray()

        lines.add("// Begin: Capacitor Plugin JS")
        for (plugin in plugins) {
            lines.add(
                """(function(w) {
var a = (w.Capacitor = w.Capacitor || {});
var p = (a.Plugins = a.Plugins || {});
var t = (p['${plugin.id}'] = {});
t.addListener = function(eventName, callback) {
  return w.Capacitor.addListener('${plugin.id}', eventName, callback);
}"""
            )
            val methods = plugin.methods
            for (method in methods!!) {
                if (method.name == "addListener" || method.name == "removeListener") {
                    // Don't export add/remove listener, we do that automatically above as they are "special snowflakes"
                    continue
                }
                lines.add(generateMethodJS(plugin, method))
            }

            lines.add("})(window);\n")
            pluginArray.put(createPluginHeader(plugin))
        }

        return """
             ${TextUtils.join("\n", lines)}
             window.Capacitor.PluginHeaders = $pluginArray;
             """.trimIndent()
    }

    fun getCordovaPluginJS(context: Context): String? {
        return getFilesContent(context, "public/plugins")
    }

    fun getFilesContent(context: Context, path: String): String? {
        val builder = StringBuilder()
        try {
            val content = context.assets.list(path)
            if (content!!.size > 0) {
                for (file in content) {
                    if (!file.endsWith(".map")) {
                        builder.append(getFilesContent(context, "$path/$file"))
                    }
                }
            } else {
                return FileUtils.readFileFromAssets(context.assets, path)
            }
        } catch (ex: IOException) {
            Logger.Companion.warn("Unable to read file at path $path")
        }
        return builder.toString()
    }

    private fun createPluginHeader(plugin: PluginHandle): JSONObject {
        val pluginObj = JSONObject()
        val methods = plugin.methods
        try {
            val id = plugin.id
            val methodArray = JSONArray()
            pluginObj.put("name", id)

            for (method in methods!!) {
                methodArray.put(createPluginMethodHeader(method))
            }

            pluginObj.put("methods", methodArray)
        } catch (e: JSONException) {
            // ignore
        }
        return pluginObj
    }

    private fun createPluginMethodHeader(method: PluginMethodHandle?): JSONObject {
        val methodObj = JSONObject()

        try {
            methodObj.put("name", method.getName())
            if (method.getReturnType() != PluginMethod.Companion.RETURN_NONE) {
                methodObj.put("rtype", method.getReturnType())
            }
        } catch (e: JSONException) {
            // ignore
        }

        return methodObj
    }

    @Throws(JSExportException::class)
    fun getBridgeJS(context: Context): String? {
        return getFilesContent(context, "native-bridge.js")
    }

    private fun generateMethodJS(plugin: PluginHandle, method: PluginMethodHandle?): String {
        val lines: MutableList<String?> = ArrayList()

        val args: MutableList<String?> = ArrayList()
        // Add the catch all param that will take a full javascript object to pass to the plugin
        args.add(CATCHALL_OPTIONS_PARAM)

        val returnType = method.getReturnType()
        if (returnType == PluginMethod.Companion.RETURN_CALLBACK) {
            args.add(CALLBACK_PARAM)
        }

        // Create the method function declaration
        lines.add("t['" + method.getName() + "'] = function(" + TextUtils.join(", ", args) + ") {")

        when (returnType) {
            PluginMethod.Companion.RETURN_NONE -> lines.add(
                "return w.Capacitor.nativeCallback('" +
                        plugin.id +
                        "', '" +
                        method.getName() +
                        "', " +
                        CATCHALL_OPTIONS_PARAM +
                        ")"
            )

            PluginMethod.Companion.RETURN_PROMISE -> lines.add(
                "return w.Capacitor.nativePromise('" + plugin.id + "', '" + method.getName() + "', " + CATCHALL_OPTIONS_PARAM + ")"
            )

            PluginMethod.Companion.RETURN_CALLBACK -> lines.add(
                "return w.Capacitor.nativeCallback('" +
                        plugin.id +
                        "', '" +
                        method.getName() +
                        "', " +
                        CATCHALL_OPTIONS_PARAM +
                        ", " +
                        CALLBACK_PARAM +
                        ")"
            )

            else -> {}
        }
        lines.add("}")

        return TextUtils.join("\n", lines)
    }
}
