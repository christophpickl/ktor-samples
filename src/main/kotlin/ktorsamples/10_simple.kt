package ktorsamples

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging.logger

private val log = logger {}

// start options:
// - EngineMain (file-based and reflective lookup via FQN)
// - commandLine (semi-programmatic)
// - embeddedServer (programmatic)

fun main() {
    log.info { "Starting server" }
    embeddedServer(
        factory = Netty,
        port = 80,
        configure = {
            requestReadTimeoutSeconds = 5
            responseWriteTimeoutSeconds = 5
        }
    ) {
        log.debug { "Configure routing" }
        routing {
            route("/") {
                get {
                    call.respondText(
                        text = "Hello",
                        contentType = ContentType.Text.Plain,
                        status = HttpStatusCode.OK
                    ) {
                        // process OutgoingContent
                    }
                }
            }
        }
    }.start(wait = true)
}
