package com.getcapacitor

@Retention(AnnotationRetention.RUNTIME)
annotation class PluginMethod(val returnType: String = RETURN_PROMISE) {
    companion object {
        const val RETURN_PROMISE: String = "promise"

        const val RETURN_CALLBACK: String = "callback"

        const val RETURN_NONE: String = "none"
    }
}
