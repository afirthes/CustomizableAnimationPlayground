import kotlinx.coroutines.*
import java.lang.Thread.sleep

suspend fun myFoo() = coroutineScope {
    delay(1000)
    print("Hi there")
    delay(1000)
    print("Hi there")
    delay(1000)
    print("Hi there")
    delay(1000)
    print("Hi there")
    delay(1000)
    print("Hi there")
}

fun main(args: Array<String>) {
    runBlocking {
        myFoo()
    }
}