package com.wthink.ubus

import com.wthink.ubus.server.EventListener
import com.wthink.ubus.server.Provider
import com.wthink.ubus.server.Subscriber
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consume
import java.util.concurrent.CopyOnWriteArrayList

object Ubus {

    private val providers = CopyOnWriteArrayList<Provider>()

    private val subscribers = CopyOnWriteArrayList<Subscriber>()

    private val listeners = CopyOnWriteArrayList<EventListener>()

    private val invokeResultChannel = Channel<InvokeResult>(10)

    fun load(server: String? = null) {
        UbusBridge.load(server)
    }

    fun registeProvider(provider: Provider) {
        providers.add(provider)
        UbusBridge.registerProvider(provider.name, provider.methodList)
    }

    fun registerSubscriber(subscriber: Subscriber) {
        subscribers.add(subscriber)
        UbusBridge.registerSubscriber(subscriber.name)
    }

    fun registerEventListener(eventListener: EventListener) {
        listeners.add(eventListener)
        UbusBridge.registerEventListener(eventListener.name, eventListener.path)
    }

    fun rsp(reqId: Long, payload: String) {
        UbusBridge.rsp(reqId, payload)
    }

    fun notify(provider: String, type: String, payload: String) {
        UbusBridge.notify(provider, type, payload)
    }

    fun sendEvent(event: String, payload: String) {
        UbusBridge.sendEvent(event, payload)
    }

    fun invokeSync(obj: String, method: String, payload: String, timeout: Long = 1000): UResult {
        return runBlocking {
            invoke(obj, method, payload, timeout)
        }
    }

    suspend fun invoke(obj: String, method: String, payload: String, timeout: Long = 1000): UResult {
        val req = UbusBridge.invoke(obj, method, payload)
        if (req == 0L) {
            return UResult.FAILED
        }

        val invokeResult = invokeResultChannel.consume {
            withTimeoutOrNull(timeout) {
                while (isActive) {
                    val result = receive()
                    if (result.id == req) {
                        return@withTimeoutOrNull result
                    }
                }
            }
        }

        if (invokeResult == null) {
            return UResult.FAILED
        }

        val result = invokeResult as InvokeResult
        if (result.success) {
            return UResult(true, result.rsp, result.err)
        }

        return UResult(false, result.rsp, result.err)
    }

    internal fun onRequestArrive(provider: String, method: String, payload: String, id: Long) {
        providers.find { it.name.equals(provider) }?.onInvoke?.invoke(id, method, payload)
    }

    internal fun onInvokeCallback(success: Boolean, id: Long, rsp: String?, err: String?) {
        GlobalScope.launch {
            invokeResultChannel.send(InvokeResult(id, success, rsp, err))
        }
    }

    internal fun onNotifyArrive(subscriber: String, method: String, payload: String) {
        subscribers.find { it.name.equals(subscriber) }?.onNotify?.invoke(method, payload)
    }

    internal fun onEventArrive(eventListener: String, type: String, payload: String) {
        listeners.find { it.name.equals(eventListener) }?.onEvent?.invoke(type, payload)
    }
}

private data class InvokeResult(val id: Long, val success: Boolean, val rsp: String?, val err: String?)