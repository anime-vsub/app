package com.getcapacitor

import com.getcapacitor.Bridge.config
import com.getcapacitor.util.JSONUtils
import org.json.JSONObject

/**
 * Represents the configuration options for plugins used by Capacitor
 */
class PluginConfig
/**
 * Constructs a PluginsConfig with the provided JSONObject value.
 *
 * @param config A plugin configuration expressed as a JSON Object
 */ internal constructor(
    /**
     * The object containing plugin config values.
     */
    val configJSON: JSONObject?
) {
    /**
     * Gets the JSON Object containing the config of the the provided plugin ID.
     *
     * @return The config for that plugin
     */

    /**
     * Get a string value for a plugin in the Capacitor config.
     *
     * @param configKey The key of the value to retrieve
     * @return The value from the config, if exists. Null if not
     */
    fun getString(configKey: String?): String {
        return getString(configKey, null)
    }

    /**
     * Get a string value for a plugin in the Capacitor config.
     *
     * @param configKey The key of the value to retrieve
     * @param defaultValue A default value to return if the key does not exist in the config
     * @return The value from the config, if key exists. Default value returned if not
     */
    fun getString(configKey: String?, defaultValue: String?): String {
        return JSONUtils.getString(configJSON, configKey, defaultValue)
    }

    /**
     * Get a boolean value for a plugin in the Capacitor config.
     *
     * @param configKey The key of the value to retrieve
     * @param defaultValue A default value to return if the key does not exist in the config
     * @return The value from the config, if key exists. Default value returned if not
     */
    fun getBoolean(configKey: String?, defaultValue: Boolean): Boolean {
        return JSONUtils.getBoolean(configJSON, configKey, defaultValue)
    }

    /**
     * Get an integer value for a plugin in the Capacitor config.
     *
     * @param configKey The key of the value to retrieve
     * @param defaultValue A default value to return if the key does not exist in the config
     * @return The value from the config, if key exists. Default value returned if not
     */
    fun getInt(configKey: String?, defaultValue: Int): Int {
        return JSONUtils.getInt(configJSON, configKey, defaultValue)
    }

    /**
     * Get a string array value for a plugin in the Capacitor config.
     *
     * @param configKey The key of the value to retrieve
     * @return The value from the config, if exists. Null if not
     */
    fun getArray(configKey: String?): Array<String> {
        return getArray(configKey, null)
    }

    /**
     * Get a string array value for a plugin in the Capacitor config.
     *
     * @param configKey The key of the value to retrieve
     * @param defaultValue A default value to return if the key does not exist in the config
     * @return The value from the config, if key exists. Default value returned if not
     */
    fun getArray(configKey: String?, defaultValue: Array<String?>?): Array<String> {
        return JSONUtils.getArray(configJSON, configKey, defaultValue)
    }

    /**
     * Get a JSON object value for a plugin in the Capacitor config.
     *
     * @param configKey The key of the value to retrieve
     * @return The value from the config, if exists. Null if not
     */
    fun getObject(configKey: String?): JSONObject {
        return JSONUtils.getObject(configJSON, configKey)
    }

    val isEmpty: Boolean
        /**
         * Check if the PluginConfig is empty.
         *
         * @return true if the plugin config has no entries
         */
        get() = configJSON!!.length() == 0
}
