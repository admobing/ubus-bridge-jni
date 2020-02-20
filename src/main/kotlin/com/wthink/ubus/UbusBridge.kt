package com.wthink.ubus

import com.alibaba.fastjson.JSON

class UbusBridge {

    companion object {

        init {
            System.loadLibrary("ubusbridge")
        }

        @JvmStatic
        fun onRequestArrive(provider: String, method: String, payload: String, id: Long) {
            Ubus.onRequestArrive(provider, method, payload, id)
        }

        @JvmStatic
        fun onInvokeCallback(success: Boolean, id: Long, rsp: String?, err: String?) {
            Ubus.onInvokeCallback(success, id, rsp, err)
        }

        @JvmStatic
        fun onNotifyArrive(subscriber: String, method: String, payload: String) {
            Ubus.onNotifyArrive(subscriber, method, payload)
        }

        @JvmStatic
        fun onEventArrive(eventListener: String, type: String, payload: String) {
            Ubus.onEventArrive(eventListener, type, payload)
        }

        @JvmStatic
        external fun load(server: String? = null)

        @JvmStatic
        fun registerProvider(name: String, methodList: List<Method>) {
            registerProvider(name, JSON.toJSONString(methodList))
        }

        @JvmStatic
        private external fun registerProvider(name: String, methodSignature: String)

        @JvmStatic
        external fun invoke(obj: String, method: String, payload: String): Long

        @JvmStatic
        external fun registerSubscriber(name: String)

        @JvmStatic
        external fun registerEventListener(name: String, path: String)

        @JvmStatic
        external fun rsp(reqId: Long, payload: String)

        @JvmStatic
        external fun notify(provider: String, type: String, payload: String)

        @JvmStatic
        external fun sendEvent(event: String, payload: String)

        @JvmStatic
        external fun loop()
    }
}