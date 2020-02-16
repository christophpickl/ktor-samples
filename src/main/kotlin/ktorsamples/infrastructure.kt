package ktorsamples

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging.logger

private val log = logger {}

fun startSampleKtorServer(configure: Application.() -> Unit) {
    log.info { "Starting server" }
    embeddedServer(Netty) {
        configure()
        log.debug { "Configure routing" }
        routing {
            get {
                call.respondText("Hello") {
                }
            }
        }
    }.start(wait = true)
}
