package com.getcapacitor

/**
 * Base annotation for all Plugins
 */
@Retention(AnnotationRetention.RUNTIME)
@Deprecated(" <p> Use {@link CapacitorPlugin} instead")
annotation class NativePlugin(
    /**
     * Request codes this plugin uses and responds to, in order to tie
     * Android events back the plugin to handle
     */
    val requestCodes: IntArray = [],
    /**
     * Permissions this plugin needs, in order to make permission requests
     * easy if the plugin only needs basic permission prompting
     */
    val permissions: Array<String> = [],
    /**
     * The request code to use when automatically requesting permissions
     */
    val permissionRequestCode: Int = 9000,
    /**
     * A custom name for the plugin, otherwise uses the
     * simple class name.
     */
    val name: String = ""
)
