package com.getcapacitor

/**
 * An interface used in the processing of routes
 */
interface RouteProcessor {
    fun process(basePath: String?, path: String?): ProcessedRoute?
}
