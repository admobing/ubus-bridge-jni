package com.wthink.ubus.server

class EventListener(val name: String, val path: String, val onEvent: (type: String, payload: String) -> Unit)