import com.wthink.ubus.*
import com.wthink.ubus.server.EventListener
import com.wthink.ubus.server.Provider
import com.wthink.ubus.server.Subscriber
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    Ubus.load()
    Ubus.registeProvider(Provider("jni", listOf(
            Method("hello", listOf(
                    MethodParam("name", ParamType.STRING)
            ))
    )) { id, method, payload ->
        println("on invoke ${id},${method},${payload}")
    })

    Ubus.registerSubscriber(Subscriber("test") { method, payload ->
        println("on sucscribe ${method}, ${payload}")
    })

    Ubus.registerEventListener(EventListener("testa", "test") { type, payload ->
        println("on event ${type},${payload}")
    })

    GlobalScope.launch {
        val ret = Ubus.invoke("test", "count", "{\"to\":123,\"string\":\"abcd\"}")
        println("${ret.success},${ret.rsp},${ret.err}")
    }

    val ret = Ubus.invokeSync("test", "count", "{\"to\":123,\"string\":\"abcd\"}")
    println("${ret.success},${ret.rsp},${ret.err}")

    TimeUnit.SECONDS.sleep(2)

    UbusBridge.loop()
}