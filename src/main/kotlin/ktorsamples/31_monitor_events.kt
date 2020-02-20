package ktorsamples

import io.ktor.application.ApplicationStarted
import io.ktor.application.ApplicationStarting
import io.ktor.application.ApplicationStopPreparing
import io.ktor.application.ApplicationStopped
import io.ktor.application.ApplicationStopping
import io.ktor.application.install
import io.ktor.server.engine.ShutDownUrl
import mu.KotlinLogging.logger

private val log = logger {}

// see for example: https://github.com/ktorio/ktor/blob/45d7487b82b9dfc281a8c56c1dd3989ccf67bb5d/ktor-server/ktor-server-core/src/io/ktor/features/CallLogging.kt
// these events are NOT fired when run in the test-application-engine :-/

fun main() {
    startSampleKtorServer {
        log.info { "Subscribing to monitor events" }
        environment.monitor.subscribe(ApplicationStarting) {
            // Note, that application itself cannot receive this event because it fires before application is created.
            // It is meant to be used by engines.
            log.info { "Event: ApplicationStarting" }
        }
        environment.monitor.subscribe(ApplicationStarted) {
            log.info { "Event: ApplicationStarted" }
        }
        environment.monitor.subscribe(ApplicationStopPreparing) {
            log.info { "Event: ApplicationStopPreparing" }
        }
        environment.monitor.subscribe(ApplicationStopping) {
            log.info { "Event: ApplicationStopping" }
        }
        environment.monitor.subscribe(ApplicationStopped) {
            log.info { "Event: ApplicationStopped" }
            // monitor.unsubscribe(ApplicationStarting, startingLambda)
        }
        install(ShutDownUrl.ApplicationCallFeature) {
            shutDownUrl = "/shutdown"
            exitCodeSupplier = { 0 }
        }
    }
}
