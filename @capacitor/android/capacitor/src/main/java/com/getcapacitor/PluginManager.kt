package com.getcapacitor

import android.content.res.AssetManager
import com.getcapacitor.PluginLoadException
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class PluginManager(private val assetManager: AssetManager) {
    @Throws(PluginLoadException::class)
    fun loadPluginClasses(): List<Class<out Plugin>> {
        val pluginsJSON = parsePluginsJSON()
        val pluginList = ArrayList<Class<out Plugin>>()

        try {
            var i = 0
            val size = pluginsJSON.length()
            while (i < size) {
                val pluginJSON = pluginsJSON.getJSONObject(i)
                val classPath = pluginJSON.getString("classpath")
                val c = Class.forName(classPath)
                pluginList.add(c.asSubclass(Plugin::class.java))
                i++
            }
        } catch (e: JSONException) {
            throw PluginLoadException("Could not parse capacitor.plugins.json as JSON")
        } catch (e: ClassNotFoundException) {
            throw PluginLoadException("Could not find class by class path: " + e.message)
        }

        return pluginList
    }

    @Throws(PluginLoadException::class)
    private fun parsePluginsJSON(): JSONArray {
        try {
            BufferedReader(InputStreamReader(assetManager.open("capacitor.plugins.json"))).use { reader ->
                val builder = StringBuilder()
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    builder.append(line)
                }
                val jsonString = builder.toString()
                return JSONArray(jsonString)
            }
        } catch (e: IOException) {
            throw PluginLoadException("Could not load capacitor.plugins.json")
        } catch (e: JSONException) {
            throw PluginLoadException("Could not parse capacitor.plugins.json as JSON")
        }
    }
}
