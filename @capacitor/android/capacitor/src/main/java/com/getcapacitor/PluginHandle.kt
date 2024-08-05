package com.getcapacitor

import com.getcapacitor.InvalidPluginMethodException
import com.getcapacitor.PluginLoadException
import com.getcapacitor.annotation.CapacitorPlugin
import java.lang.reflect.InvocationTargetException

/**
 * PluginHandle is an instance of a plugin that has been registered
 * and indexed. Think of it as a Plugin instance with extra metadata goodies
 */
class PluginHandle @Suppress("deprecation") private constructor(
    val pluginClass: Class<out Plugin>,
    private val bridge: Bridge
) {
    private val pluginMethods: MutableMap<String, PluginMethodHandle> = HashMap()

    var id: String? = null

    @get:Suppress("deprecation")
    @Suppress("deprecation")
    var legacyPluginAnnotation: NativePlugin? = null

    var pluginAnnotation: CapacitorPlugin? = null

    var instance: Plugin? = null
        private set

    init {
        val pluginAnnotation = pluginClass.getAnnotation(
            CapacitorPlugin::class.java
        )
        if (pluginAnnotation == null) {
            // Check for legacy plugin annotation, @NativePlugin
            val legacyPluginAnnotation = pluginClass.getAnnotation(
                NativePlugin::class.java
            )
            if (legacyPluginAnnotation == null) {
                throw InvalidPluginException("No @CapacitorPlugin annotation found for plugin " + pluginClass.name)
            }

            if (legacyPluginAnnotation.name != "") {
                this.id = legacyPluginAnnotation.name
            } else {
                this.id = pluginClass.simpleName
            }

            this.legacyPluginAnnotation = legacyPluginAnnotation
        } else {
            if (pluginAnnotation.name != "") {
                this.id = pluginAnnotation.name
            } else {
                this.id = pluginClass.simpleName
            }

            this.pluginAnnotation = pluginAnnotation
        }

        this.indexMethods(pluginClass)
    }

    constructor(bridge: Bridge, pluginClass: Class<out Plugin>) : this(pluginClass, bridge) {
        this.load()
    }

    constructor(bridge: Bridge, plugin: Plugin) : this(plugin.javaClass, bridge) {
        this.loadInstance(plugin)
    }

    val methods: Collection<PluginMethodHandle>
        get() = pluginMethods.values

    @Throws(PluginLoadException::class)
    fun load(): Plugin? {
        if (this.instance != null) {
            return this.instance
        }

        try {
            this.instance = pluginClass.newInstance()
            return this.loadInstance(instance)
        } catch (ex: InstantiationException) {
            throw PluginLoadException("Unable to load plugin instance. Ensure plugin is publicly accessible")
        } catch (ex: IllegalAccessException) {
            throw PluginLoadException("Unable to load plugin instance. Ensure plugin is publicly accessible")
        }
    }

    fun loadInstance(plugin: Plugin?): Plugin? {
        this.instance = plugin
        instance.setPluginHandle(this)
        instance.setBridge(this.bridge)
        instance!!.load()
        instance!!.initializeActivityLaunchers()
        return this.instance
    }

    /**
     * Call a method on a plugin.
     * @param methodName the name of the method to call
     * @param call the constructed PluginCall with parameters from the caller
     * @throws InvalidPluginMethodException if no method was found on that plugin
     */
    @Throws(
        PluginLoadException::class,
        InvalidPluginMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    fun invoke(methodName: String, call: PluginCall?) {
        if (this.instance == null) {
            // Can throw PluginLoadException
            this.load()
        }

        val methodMeta = pluginMethods[methodName]
            ?: throw InvalidPluginMethodException("No method " + methodName + " found for plugin " + pluginClass.name)

        methodMeta.method.invoke(this.instance, call)
    }

    /**
     * Index all the known callable methods for a plugin for faster
     * invocation later
     */
    private fun indexMethods(plugin: Class<out Plugin>) {
        //Method[] methods = pluginClass.getDeclaredMethods();
        val methods = pluginClass.methods

        for (methodReflect in methods) {
            val method = methodReflect.getAnnotation(PluginMethod::class.java) ?: continue

            val methodMeta = PluginMethodHandle(methodReflect, method)
            pluginMethods[methodReflect.name] = methodMeta
        }
    }
}
