package ktorsamples

import io.ktor.application.install
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    println("START")
    embeddedServer(Netty) {
        install(ShutDownUrl.ApplicationCallFeature) {
            shutDownUrl = "/shutdown"
            exitCodeSupplier = {
                println("Shutting down ...")
                0
            }
        }
    }.start(wait = true)
    println("END")
}
