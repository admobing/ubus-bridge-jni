package com.wthink.ubus

class UResult(val success: Boolean, val rsp: String? = null, val err: String? = "invoke err") {

    companion object {
        val FAILED = UResult(false)
    }
}

