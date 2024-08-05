package com.getcapacitor

class ServerPath(val type: PathType, val path: String) {
    enum class PathType {
        BASE_PATH,
        ASSET_PATH
    }
}
