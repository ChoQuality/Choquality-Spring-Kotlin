package com.example.choquality.common.exception

import com.example.choquality.common.spec.SDKSpec

class SDKException : RuntimeException {
    val code: Int
    private val customMessage: String

    constructor(spec: SDKSpec) : super(spec.message) {
        this.code = spec.code
        this.customMessage = spec.message
    }

    constructor(spec: SDKSpec, message: String) : super(message) {
        this.code = spec.code
        this.customMessage = message
    }

    override val message: String
        get() = customMessage
}
