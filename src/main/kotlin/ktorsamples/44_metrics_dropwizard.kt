package ktorsamples

import com.codahale.metrics.Slf4jReporter
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.metrics.dropwizard.DropwizardMetrics
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging.logger
import java.util.concurrent.TimeUnit

// SEE: https://ktor.io/servers/features/metrics-micrometer.html

fun main() {
    val log = logger {}
    embeddedServer(Netty) {
        install(DropwizardMetrics) {
            Slf4jReporter.forRegistry(registry)
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build()
                .start(10, TimeUnit.SECONDS)
        }
//        JmxReporter.forRegistry(registry)
//            .convertRatesTo(TimeUnit.SECONDS)
//            .convertDurationsTo(TimeUnit.MILLISECONDS)
//            .build()
//            .start()
        routing {
            get {
                call.respondText("Hello Dropwizard!")
            }
        }
    }.start(wait = true)
}
