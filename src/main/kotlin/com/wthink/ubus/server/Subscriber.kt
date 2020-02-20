package com.wthink.ubus.server

class Subscriber(val name: String, val onNotify: (method: String, payload: String) -> Unit)