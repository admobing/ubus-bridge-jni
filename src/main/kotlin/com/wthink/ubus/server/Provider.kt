package com.wthink.ubus.server

import com.wthink.ubus.Method
import com.wthink.ubus.Ubus
import com.wthink.ubus.UbusBridge

class Provider(val name: String, val methodList: List<Method>, val onInvoke: (id: Long, method: String, payload: String) -> Unit) {
    
    fun rsp(id: Long, payload: String) {
        Ubus.rsp(id, payload)
    }

    fun registeToUbus() {
        UbusBridge.registerProvider(name, methodList)
    }

}