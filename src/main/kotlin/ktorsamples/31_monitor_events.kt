package ktorsamples

import io.ktor.application.ApplicationStarted
import io.ktor.application.ApplicationStarting
import io.ktor.application.ApplicationStopPreparing
import io.ktor.application.ApplicationStopped
import io.ktor.application.ApplicationStopping
import mu.KotlinLogging.logger

private val log = logger {}

// see for example: https://github.com/ktorio/ktor/blob/45d7487b82b9dfc281a8c56c1dd3989ccf67bb5d/ktor-server/ktor-server-core/src/io/ktor/features/CallLogging.kt
fun main() {
    startSampleKtorServer {
        log.info { "Subscribing to monitor events" }
        environment.monitor.subscribe(ApplicationStarting) {
            // TODO not invoked? maybe not when
            log.info { "Application starting" }
        }
        environment.monitor.subscribe(ApplicationStarted) {
            log.info { "Application started" }
        }
        environment.monitor.subscribe(ApplicationStopPreparing) {
            log.info { "Application stop preparing" }
        }
        environment.monitor.subscribe(ApplicationStopping) {
            log.info { "Application stopping" }
        }
        environment.monitor.subscribe(ApplicationStopped) {
            log.info { "Application stopped" }
            // monitor.unsubscribe(ApplicationStarting, startingLambda)
        }
    }
}
