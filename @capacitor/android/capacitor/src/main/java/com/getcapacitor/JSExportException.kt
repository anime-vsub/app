package com.getcapacitor

class JSExportException : Exception {
    constructor(s: String?) : super(s)

    constructor(t: Throwable?) : super(t)

    constructor(s: String?, t: Throwable?) : super(s, t)
}
