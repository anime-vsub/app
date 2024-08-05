package com.getcapacitor

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.AssetManager
import com.getcapacitor.util.JSONUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Represents the configuration options for Capacitor
 */
class CapConfig {
    // Server Config
    var isHTML5Mode: Boolean = true
        private set
    var serverUrl: String? = null
        private set
    var hostname: String = "localhost"
        private set
    var androidScheme: String = Bridge.CAPACITOR_HTTP_SCHEME
        private set
    var allowNavigation: Array<String>
        private set

    // Android Config
    var overriddenUserAgentString: String? = null
        private set
    var appendedUserAgentString: String? = null
        private set
    var backgroundColor: String? = null
        private set
    var isMixedContentAllowed: Boolean = false
        private set
    var isInputCaptured: Boolean = false
        private set
    var isWebContentsDebuggingEnabled: Boolean = false
        private set
    var isLoggingEnabled: Boolean = true
        private set
    var isInitialFocus: Boolean = true
        private set
    var isUsingLegacyBridge: Boolean = false
        private set
    private var minWebViewVersion = Bridge.DEFAULT_ANDROID_WEBVIEW_VERSION
    private var minHuaweiWebViewVersion = Bridge.DEFAULT_HUAWEI_WEBVIEW_VERSION
    var errorPath: String? = null
        private set

    // Embedded
    var startPath: String? = null
        private set

    // Plugins
    private var pluginsConfiguration: Map<String?, PluginConfig>? = null

    // Config Object JSON (legacy)
    private var configJSON = JSONObject()

    /**
     * Constructs an empty config file.
     */
    private constructor()

    /**
     * Get an instance of the Config file object.
     * @param assetManager The AssetManager used to load the config file
     * @param config JSON describing a configuration to use
     */
    @Deprecated(
        """use {@link #loadDefault(Context)} to load an instance of the Config object
      from the capacitor.config.json file, or use the {@link CapConfig.Builder} to construct
      a CapConfig for embedded use.

      """
    )
    constructor(assetManager: AssetManager, config: JSONObject?) {
        if (config != null) {
            this.configJSON = config
        } else {
            // Load the capacitor.config.json
            loadConfigFromAssets(assetManager, null)
        }

        deserializeConfig(null)
    }

    /**
     * Constructs a Capacitor Configuration using ConfigBuilder.
     *
     * @param builder A config builder initialized with values
     */
    private constructor(builder: Builder) {
        // Server Config
        this.isHTML5Mode = builder.html5mode
        this.serverUrl = builder.serverUrl
        this.hostname = builder.hostname

        if (this.validateScheme(builder.androidScheme)) {
            this.androidScheme = builder.androidScheme
        }

        this.allowNavigation = builder.allowNavigation

        // Android Config
        this.overriddenUserAgentString = builder.overriddenUserAgentString
        this.appendedUserAgentString = builder.appendedUserAgentString
        this.backgroundColor = builder.backgroundColor
        this.isMixedContentAllowed = builder.allowMixedContent
        this.isInputCaptured = builder.captureInput
        this.isWebContentsDebuggingEnabled = builder.webContentsDebuggingEnabled!!
        this.isLoggingEnabled = builder.loggingEnabled
        this.isInitialFocus = builder.initialFocus
        this.isUsingLegacyBridge = builder.useLegacyBridge
        this.minWebViewVersion = builder.minWebViewVersion
        this.minHuaweiWebViewVersion = builder.minHuaweiWebViewVersion
        this.errorPath = builder.errorPath

        // Embedded
        this.startPath = builder.startPath

        // Plugins Config
        this.pluginsConfiguration = builder.pluginsConfiguration
    }

    /**
     * Loads a Capacitor Configuration JSON file into a Capacitor Configuration object.
     * An optional path string can be provided to look for the config in a subdirectory path.
     */
    private fun loadConfigFromAssets(assetManager: AssetManager, path: String?) {
        var path = path
        if (path == null) {
            path = ""
        } else {
            // Add slash at the end to form a proper file path if going deeper in assets dir
            if (path[path.length - 1] != '/') {
                path = "$path/"
            }
        }

        try {
            val jsonString =
                FileUtils.readFileFromAssets(assetManager, path + "capacitor.config.json")
            configJSON = JSONObject(jsonString)
        } catch (ex: IOException) {
            Logger.Companion.error(
                "Unable to load capacitor.config.json. Run npx cap copy first",
                ex
            )
        } catch (ex: JSONException) {
            Logger.Companion.error(
                "Unable to parse capacitor.config.json. Make sure it's valid json",
                ex
            )
        }
    }

    /**
     * Loads a Capacitor Configuration JSON file into a Capacitor Configuration object.
     * An optional path string can be provided to look for the config in a subdirectory path.
     */
    private fun loadConfigFromFile(path: String) {
        var path: String? = path
        if (path == null) {
            path = ""
        } else {
            // Add slash at the end to form a proper file path if going deeper in assets dir
            if (path[path.length - 1] != '/') {
                path = "$path/"
            }
        }

        try {
            val configFile = File(path + "capacitor.config.json")
            val jsonString = FileUtils.readFileFromDisk(configFile)
            configJSON = JSONObject(jsonString)
        } catch (ex: JSONException) {
            Logger.Companion.error(
                "Unable to parse capacitor.config.json. Make sure it's valid json",
                ex
            )
        } catch (ex: IOException) {
            Logger.Companion.error("Unable to load capacitor.config.json.", ex)
        }
    }

    /**
     * Deserializes the config from JSON into a Capacitor Configuration object.
     */
    private fun deserializeConfig(context: Context?) {
        val isDebug =
            context != null && (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        // Server
        isHTML5Mode = JSONUtils.getBoolean(configJSON, "server.html5mode", isHTML5Mode)
        serverUrl = JSONUtils.getString(configJSON, "server.url", null)
        hostname = JSONUtils.getString(configJSON, "server.hostname", hostname)
        errorPath = JSONUtils.getString(configJSON, "server.errorPath", null)

        val configSchema = JSONUtils.getString(configJSON, "server.androidScheme", androidScheme)
        if (this.validateScheme(configSchema)) {
            androidScheme = configSchema
        }

        allowNavigation = JSONUtils.getArray(configJSON, "server.allowNavigation", null)

        // Android
        overriddenUserAgentString =
            JSONUtils.getString(
                configJSON,
                "android.overrideUserAgent",
                JSONUtils.getString(configJSON, "overrideUserAgent", null)
            )
        appendedUserAgentString =
            JSONUtils.getString(
                configJSON,
                "android.appendUserAgent",
                JSONUtils.getString(configJSON, "appendUserAgent", null)
            )
        backgroundColor =
            JSONUtils.getString(
                configJSON,
                "android.backgroundColor",
                JSONUtils.getString(configJSON, "backgroundColor", null)
            )
        isMixedContentAllowed =
            JSONUtils.getBoolean(
                configJSON,
                "android.allowMixedContent",
                JSONUtils.getBoolean(configJSON, "allowMixedContent", isMixedContentAllowed)
            )
        minWebViewVersion = JSONUtils.getInt(
            configJSON,
            "android.minWebViewVersion",
            Bridge.DEFAULT_ANDROID_WEBVIEW_VERSION
        )
        minHuaweiWebViewVersion = JSONUtils.getInt(
            configJSON,
            "android.minHuaweiWebViewVersion",
            Bridge.DEFAULT_HUAWEI_WEBVIEW_VERSION
        )
        isInputCaptured = JSONUtils.getBoolean(configJSON, "android.captureInput", isInputCaptured)
        isUsingLegacyBridge =
            JSONUtils.getBoolean(configJSON, "android.useLegacyBridge", isUsingLegacyBridge)
        isWebContentsDebuggingEnabled =
            JSONUtils.getBoolean(configJSON, "android.webContentsDebuggingEnabled", isDebug)

        val logBehavior = JSONUtils.getString(
            configJSON,
            "android.loggingBehavior",
            JSONUtils.getString(configJSON, "loggingBehavior", LOG_BEHAVIOR_DEBUG)
        )
        when (logBehavior.lowercase()) {
            LOG_BEHAVIOR_PRODUCTION -> isLoggingEnabled = true
            LOG_BEHAVIOR_NONE -> isLoggingEnabled = false
            else -> isLoggingEnabled = isDebug
        }
        isInitialFocus = JSONUtils.getBoolean(configJSON, "android.initialFocus", isInitialFocus)

        // Plugins
        pluginsConfiguration = deserializePluginsConfig(JSONUtils.getObject(configJSON, "plugins"))
    }

    private fun validateScheme(scheme: String): Boolean {
        val invalidSchemes: List<String> =
            mutableListOf("file", "ftp", "ftps", "ws", "wss", "about", "blob", "data")
        if (invalidSchemes.contains(scheme)) {
            Logger.Companion.warn("$scheme is not an allowed scheme.  Defaulting to http.")
            return false
        }

        // Non-http(s) schemes are not allowed to modify the URL path as of Android Webview 117
        if (scheme != "http" && scheme != "https") {
            Logger.Companion.warn(
                "Using a non-standard scheme: $scheme for Android. This is known to cause issues as of Android Webview 117."
            )
        }

        return true
    }

    fun getMinWebViewVersion(): Int {
        if (minWebViewVersion < Bridge.MINIMUM_ANDROID_WEBVIEW_VERSION) {
            Logger.Companion.warn("Specified minimum webview version is too low, defaulting to " + Bridge.MINIMUM_ANDROID_WEBVIEW_VERSION)
            return Bridge.MINIMUM_ANDROID_WEBVIEW_VERSION
        }

        return minWebViewVersion
    }

    fun getMinHuaweiWebViewVersion(): Int {
        if (minHuaweiWebViewVersion < Bridge.MINIMUM_HUAWEI_WEBVIEW_VERSION) {
            Logger.Companion.warn("Specified minimum Huawei webview version is too low, defaulting to " + Bridge.MINIMUM_HUAWEI_WEBVIEW_VERSION)
            return Bridge.MINIMUM_HUAWEI_WEBVIEW_VERSION
        }

        return minHuaweiWebViewVersion
    }

    fun getPluginConfiguration(pluginId: String?): PluginConfig {
        var pluginConfig = pluginsConfiguration!![pluginId]
        if (pluginConfig == null) {
            pluginConfig = PluginConfig(JSONObject())
        }

        return pluginConfig
    }

    /**
     * Get a JSON object value from the Capacitor config.
     * @param key A key to fetch from the config
     * @return The value from the config, if exists. Null if not
     */
    @Deprecated(
        """use {@link PluginConfig#getObject(String)}  to access plugin config values.
      For main Capacitor config values, use the appropriate getter.

      """
    )
    fun getObject(key: String?): JSONObject? {
        try {
            return configJSON.getJSONObject(key)
        } catch (ex: Exception) {
        }
        return null
    }

    /**
     * Get a string value from the Capacitor config.
     * @param key A key to fetch from the config
     * @return The value from the config, if exists. Null if not
     */
    @Deprecated(
        """use {@link PluginConfig#getString(String, String)} to access plugin config
      values. For main Capacitor config values, use the appropriate getter.

      """
    )
    fun getString(key: String?): String {
        return JSONUtils.getString(configJSON, key, null)
    }

    /**
     * Get a string value from the Capacitor config.
     * @param key A key to fetch from the config
     * @param defaultValue A default value to return if the key does not exist in the config
     * @return The value from the config, if key exists. Default value returned if not
     */
    @Deprecated(
        """use {@link PluginConfig#getString(String, String)} to access plugin config
      values. For main Capacitor config values, use the appropriate getter.

      """
    )
    fun getString(key: String?, defaultValue: String?): String {
        return JSONUtils.getString(configJSON, key, defaultValue)
    }

    /**
     * Get a boolean value from the Capacitor config.
     * @param key A key to fetch from the config
     * @param defaultValue A default value to return if the key does not exist in the config
     * @return The value from the config, if key exists. Default value returned if not
     */
    @Deprecated(
        """use {@link PluginConfig#getBoolean(String, boolean)} to access plugin config
      values. For main Capacitor config values, use the appropriate getter.

      """
    )
    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return JSONUtils.getBoolean(configJSON, key, defaultValue)
    }

    /**
     * Get an integer value from the Capacitor config.
     * @param key A key to fetch from the config
     * @param defaultValue A default value to return if the key does not exist in the config
     * @return The value from the config, if key exists. Default value returned if not
     */
    @Deprecated(
        """use {@link PluginConfig#getInt(String, int)}  to access the plugin config
      values. For main Capacitor config values, use the appropriate getter.

      """
    )
    fun getInt(key: String?, defaultValue: Int): Int {
        return JSONUtils.getInt(configJSON, key, defaultValue)
    }

    /**
     * Get a string array value from the Capacitor config.
     * @param key A key to fetch from the config
     * @return The value from the config, if exists. Null if not
     */
    @Deprecated(
        """use {@link PluginConfig#getArray(String)}  to access the plugin config
      values. For main Capacitor config values, use the appropriate getter.

      """
    )
    fun getArray(key: String?): Array<String> {
        return JSONUtils.getArray(configJSON, key, null)
    }

    /**
     * Get a string array value from the Capacitor config.
     * @param key A key to fetch from the config
     * @param defaultValue A default value to return if the key does not exist in the config
     * @return The value from the config, if key exists. Default value returned if not
     */
    @Deprecated(
        """use {@link PluginConfig#getArray(String, String[])}  to access the plugin
      config values. For main Capacitor config values, use the appropriate getter.

      """
    )
    fun getArray(key: String?, defaultValue: Array<String?>?): Array<String> {
        return JSONUtils.getArray(configJSON, key, defaultValue)
    }

    /**
     * Builds a Capacitor Configuration in code
     */
    class Builder
    /**
     * Constructs a new CapConfig Builder.
     *
     * @param context The context
     */(private val context: Context) {
        // Server Config Values
        var html5mode: Boolean = true
        var serverUrl: String? = null
        var errorPath: String? = null
        var hostname: String = "localhost"
        var androidScheme: String = Bridge.CAPACITOR_HTTP_SCHEME
        var allowNavigation: Array<String>

        // Android Config Values
        var overriddenUserAgentString: String? = null
        var appendedUserAgentString: String? = null
        var backgroundColor: String? = null
        var allowMixedContent: Boolean = false
        var captureInput: Boolean = false
        var webContentsDebuggingEnabled: Boolean? = null
        var loggingEnabled: Boolean = true
        var initialFocus: Boolean = false
        var useLegacyBridge: Boolean = false
        val minWebViewVersion: Int = Bridge.DEFAULT_ANDROID_WEBVIEW_VERSION
        val minHuaweiWebViewVersion: Int = Bridge.DEFAULT_HUAWEI_WEBVIEW_VERSION

        // Embedded
        var startPath: String? = null

        // Plugins Config Object
        var pluginsConfiguration: Map<String?, PluginConfig> = HashMap()

        /**
         * Builds a Capacitor Config from the builder.
         *
         * @return A new Capacitor Config
         */
        fun create(): CapConfig {
            if (webContentsDebuggingEnabled == null) {
                webContentsDebuggingEnabled =
                    (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            }

            return CapConfig(this)
        }

        fun setPluginsConfiguration(pluginsConfiguration: JSONObject?): Builder {
            this.pluginsConfiguration = deserializePluginsConfig(pluginsConfiguration)
            return this
        }

        fun setHTML5mode(html5mode: Boolean): Builder {
            this.html5mode = html5mode
            return this
        }

        fun setServerUrl(serverUrl: String?): Builder {
            this.serverUrl = serverUrl
            return this
        }

        fun setErrorPath(errorPath: String?): Builder {
            this.errorPath = errorPath
            return this
        }

        fun setHostname(hostname: String): Builder {
            this.hostname = hostname
            return this
        }

        fun setStartPath(path: String?): Builder {
            this.startPath = path
            return this
        }

        fun setAndroidScheme(androidScheme: String): Builder {
            this.androidScheme = androidScheme
            return this
        }

        fun setAllowNavigation(allowNavigation: Array<String>): Builder {
            this.allowNavigation = allowNavigation
            return this
        }

        fun setOverriddenUserAgentString(overriddenUserAgentString: String?): Builder {
            this.overriddenUserAgentString = overriddenUserAgentString
            return this
        }

        fun setAppendedUserAgentString(appendedUserAgentString: String?): Builder {
            this.appendedUserAgentString = appendedUserAgentString
            return this
        }

        fun setBackgroundColor(backgroundColor: String?): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun setAllowMixedContent(allowMixedContent: Boolean): Builder {
            this.allowMixedContent = allowMixedContent
            return this
        }

        fun setCaptureInput(captureInput: Boolean): Builder {
            this.captureInput = captureInput
            return this
        }

        fun setUseLegacyBridge(useLegacyBridge: Boolean): Builder {
            this.useLegacyBridge = useLegacyBridge
            return this
        }

        fun setWebContentsDebuggingEnabled(webContentsDebuggingEnabled: Boolean): Builder {
            this.webContentsDebuggingEnabled = webContentsDebuggingEnabled
            return this
        }

        fun setLoggingEnabled(enabled: Boolean): Builder {
            this.loggingEnabled = enabled
            return this
        }

        fun setInitialFocus(focus: Boolean): Builder {
            this.initialFocus = focus
            return this
        }
    }

    companion object {
        private const val LOG_BEHAVIOR_NONE = "none"
        private const val LOG_BEHAVIOR_DEBUG = "debug"
        private const val LOG_BEHAVIOR_PRODUCTION = "production"

        /**
         * Constructs a Capacitor Configuration from config.json file.
         *
         * @param context The context.
         * @return A loaded config file, if successful.
         */
        fun loadDefault(context: Context?): CapConfig {
            val config = CapConfig()

            if (context == null) {
                Logger.Companion.error("Capacitor Config could not be created from file. Context must not be null.")
                return config
            }

            config.loadConfigFromAssets(context.assets, null)
            config.deserializeConfig(context)
            return config
        }

        /**
         * Constructs a Capacitor Configuration from config.json file within the app assets.
         *
         * @param context The context.
         * @param path A path relative to the root assets directory.
         * @return A loaded config file, if successful.
         */
        fun loadFromAssets(context: Context?, path: String?): CapConfig {
            val config = CapConfig()

            if (context == null) {
                Logger.Companion.error("Capacitor Config could not be created from file. Context must not be null.")
                return config
            }

            config.loadConfigFromAssets(context.assets, path)
            config.deserializeConfig(context)
            return config
        }

        /**
         * Constructs a Capacitor Configuration from config.json file within the app file-space.
         *
         * @param context The context.
         * @param path A path relative to the root of the app file-space.
         * @return A loaded config file, if successful.
         */
        fun loadFromFile(context: Context?, path: String): CapConfig {
            val config = CapConfig()

            if (context == null) {
                Logger.Companion.error("Capacitor Config could not be created from file. Context must not be null.")
                return config
            }

            config.loadConfigFromFile(path)
            config.deserializeConfig(context)
            return config
        }

        private fun deserializePluginsConfig(pluginsConfig: JSONObject?): Map<String?, PluginConfig> {
            val pluginsMap: MutableMap<String?, PluginConfig> = HashMap()

            // return an empty map if there is no pluginsConfig json
            if (pluginsConfig == null) {
                return pluginsMap
            }

            val pluginIds = pluginsConfig.keys()

            while (pluginIds.hasNext()) {
                val pluginId = pluginIds.next()
                var value: JSONObject? = null

                try {
                    value = pluginsConfig.getJSONObject(pluginId)
                    val pluginConfig = PluginConfig(value)
                    pluginsMap[pluginId] = pluginConfig
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            return pluginsMap
        }
    }
}
