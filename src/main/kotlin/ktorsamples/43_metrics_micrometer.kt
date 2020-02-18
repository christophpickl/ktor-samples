package ktorsamples

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.micrometer.core.instrument.simple.SimpleMeterRegistry

// SEE: https://ktor.io/servers/features/metrics-micrometer.html

fun main() {
    embeddedServer(Netty) {
        val meterRegistry = SimpleMeterRegistry() // for testing purpose
        install(MicrometerMetrics) {
            registry = meterRegistry
//            meterBinders = listOf(
//                ClassLoaderMetrics(),
//                JvmMemoryMetrics(),
//                JvmGcMetrics(),
//                ProcessorMetrics(),
//                JvmThreadMetrics(),
//                FileDescriptorMetrics()
//            )
        }

        routing {
            get {
                val stats = meterRegistry.meters
                    .map {
                        it.id to it.measure().joinToString()
                    }
                    .sortedBy { it.first.name }
                    .joinToString("\n") { "${it.first} -> ${it.second}" }
                call.respondText("Hello Micrometer!\n\n$stats")
            }
        }
    }.start(wait = true)
}
