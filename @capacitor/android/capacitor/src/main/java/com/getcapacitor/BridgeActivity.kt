package com.getcapacitor

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.getcapacitor.android.R
import kotlin.math.max

open class BridgeActivity : AppCompatActivity() {
    var bridge: Bridge? = null
        protected set
    protected var keepRunning: Boolean = true
    protected var config: CapConfig? = null

    protected var activityDepth: Int = 0
    protected var initialPlugins: List<Class<out Plugin>> = ArrayList()
    protected val bridgeBuilder: Bridge.Builder = Bridge.Builder(
        this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bridgeBuilder.setInstanceState(savedInstanceState)
        application.setTheme(R.style.AppTheme_NoActionBar)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.bridge_layout_main)
        val loader = PluginManager(assets)

        try {
            bridgeBuilder.addPlugins(loader.loadPluginClasses())
        } catch (ex: PluginLoadException) {
            Logger.Companion.error("Error loading plugins.", ex)
        }

        this.load()
    }

    protected fun load() {
        Logger.Companion.debug("Starting BridgeActivity")

        bridge = bridgeBuilder.addPlugins(initialPlugins).setConfig(config).create()

        this.keepRunning = bridge!!.shouldKeepRunning()
        this.onNewIntent(intent)
    }

    fun registerPlugin(plugin: Class<out Plugin?>?) {
        bridgeBuilder.addPlugin(plugin!!)
    }

    fun registerPlugins(plugins: List<Class<out Plugin?>?>?) {
        bridgeBuilder.addPlugins(plugins)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bridge!!.saveInstanceState(outState)
    }

    public override fun onStart() {
        super.onStart()
        activityDepth++
        bridge!!.onStart()
        Logger.Companion.debug("App started")
    }

    public override fun onRestart() {
        super.onRestart()
        bridge!!.onRestart()
        Logger.Companion.debug("App restarted")
    }

    public override fun onResume() {
        super.onResume()
        bridge!!.app.fireStatusChange(true)
        bridge!!.onResume()
        Logger.Companion.debug("App resumed")
    }

    public override fun onPause() {
        super.onPause()
        bridge!!.onPause()
        Logger.Companion.debug("App paused")
    }

    public override fun onStop() {
        super.onStop()

        activityDepth = max(0.0, (activityDepth - 1).toDouble()).toInt()
        if (activityDepth == 0) {
            bridge!!.app.fireStatusChange(false)
        }

        bridge!!.onStop()
        Logger.Companion.debug("App stopped")
    }

    public override fun onDestroy() {
        super.onDestroy()
        bridge!!.onDestroy()
        Logger.Companion.debug("App destroyed")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bridge!!.onDetachedFromWindow()
    }

    /**
     * Handles permission request results.
     *
     * Capacitor is backwards compatible such that plugins using legacy permission request codes
     * may coexist with plugins using the AndroidX Activity v1.2 permission callback flow introduced
     * in Capacitor 3.0.
     *
     * In this method, plugins are checked first for ownership of the legacy permission request code.
     * If the [Bridge.onRequestPermissionsResult] method indicates it has
     * handled the permission, then the permission callback will be considered complete. Otherwise,
     * the permission will be handled using the AndroidX Activity flow.
     *
     * @param requestCode the request code associated with the permission request
     * @param permissions the Android permission strings requested
     * @param grantResults the status result of the permission request
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (this.bridge == null) {
            return
        }

        if (!bridge!!.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Handles activity results.
     *
     * Capacitor is backwards compatible such that plugins using legacy activity result codes
     * may coexist with plugins using the AndroidX Activity v1.2 activity callback flow introduced
     * in Capacitor 3.0.
     *
     * In this method, plugins are checked first for ownership of the legacy request code. If the
     * [Bridge.onActivityResult] method indicates it has handled the activity
     * result, then the callback will be considered complete. Otherwise, the result will be handled
     * using the AndroidX Activiy flow.
     *
     * @param requestCode the request code associated with the activity result
     * @param resultCode the result code
     * @param data any data included with the activity result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (this.bridge == null) {
            return
        }

        if (!bridge!!.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (this.bridge == null || intent == null) {
            return
        }

        bridge!!.onNewIntent(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (this.bridge == null) {
            return
        }

        bridge!!.onConfigurationChanged(newConfig)
    }
}
