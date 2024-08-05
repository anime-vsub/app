package com.getcapacitor

/**
 * An data class used in conjunction with RouteProcessor.
 *
 * @see com.getcapacitor.RouteProcessor
 */
class ProcessedRoute {
    var path: String? = null
    var isAsset: Boolean = false
    var isIgnoreAssetPath: Boolean = false
}
