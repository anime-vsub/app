package com.getcapacitor

import android.text.TextUtils
import android.util.Log

class Logger {
    private fun loadConfig(config: CapConfig) {
        Companion.config = config
    }

    companion object {
        const val LOG_TAG_CORE: String = "Capacitor"
        var config: CapConfig? = null

        private var instance: Logger? = null
            get() {
                if (field == null) {
                    field = Logger()
                }
                return field
            }

        fun init(config: CapConfig) {
            instance!!.loadConfig(config)
        }

        @JvmStatic
        fun tags(vararg subtags: String?): String {
            if (subtags != null && subtags.size > 0) {
                return LOG_TAG_CORE + "/" + TextUtils.join("/", subtags)
            }

            return LOG_TAG_CORE
        }

        fun verbose(message: String?) {
            verbose(LOG_TAG_CORE, message)
        }

        fun verbose(tag: String?, message: String?) {
            if (!shouldLog()) {
                return
            }

            Log.v(tag, message!!)
        }

        fun debug(message: String?) {
            debug(LOG_TAG_CORE, message)
        }

        @JvmStatic
        fun debug(tag: String?, message: String?) {
            if (!shouldLog()) {
                return
            }

            Log.d(tag, message!!)
        }

        fun info(message: String?) {
            info(LOG_TAG_CORE, message)
        }

        @JvmStatic
        fun info(tag: String?, message: String?) {
            if (!shouldLog()) {
                return
            }

            Log.i(tag, message!!)
        }

        fun warn(message: String?) {
            warn(LOG_TAG_CORE, message)
        }

        fun warn(tag: String?, message: String?) {
            if (!shouldLog()) {
                return
            }

            Log.w(tag, message!!)
        }

        @JvmStatic
        fun error(message: String?) {
            error(LOG_TAG_CORE, message, null)
        }

        @JvmStatic
        fun error(message: String?, e: Throwable?) {
            error(LOG_TAG_CORE, message, e)
        }

        @JvmStatic
        fun error(tag: String?, message: String?, e: Throwable?) {
            if (!shouldLog()) {
                return
            }

            Log.e(tag, message, e)
        }

        fun shouldLog(): Boolean {
            return config == null || config!!.isLoggingEnabled
        }
    }
}
