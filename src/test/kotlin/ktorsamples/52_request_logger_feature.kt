package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.origin
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.RequestConnectionPoint
import io.ktor.request.httpMethod
import io.ktor.request.path
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.AttributeKey
import io.ktor.util.toMap
import mu.KLogger
import mu.KotlinLogging.logger
import org.slf4j.event.Level
import org.testng.annotations.Test

// see: https://ktor.io/advanced/pipeline/route.html#hooking-before-and-after-routing

@Test
class RequestLoggerFeatureTest {
    fun `Given request logger installed When get endpoint Then logs printed`(): Unit = withTestApplication({
        install(RequestLoggerFeature) {
            logLevel = Level.INFO
        }
        routing {
            get {
                call.response.header("ser", "ver")
                call.respondText("OK")
            }
        }
    }) {
        handleRequest(HttpMethod.Get, "/?faz=baz") {
            addHeader("foo", "bar")
        }.apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
        }
        println("Check logs")
    }
}

class RequestLoggerFeature(configuration: Configuration) {

    private val log = logger {}
    private val logLevel = configuration.logLevel

    class Configuration {
        var logLevel = Level.TRACE
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RequestLoggerFeature> {
        override val key = AttributeKey<RequestLoggerFeature>("RequestLoggerFeature")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RequestLoggerFeature {
            val feature = RequestLoggerFeature(Configuration().apply(configure))
            pipeline.intercept(ApplicationCallPipeline.Call) {
                val now = System.currentTimeMillis()
                try {
                    proceed() // need to proceed the pipeline exeuction, as otherwise response would not be set
                } finally {
                    val duration = System.currentTimeMillis() - now
                    feature.log(call, duration)
                }
            }
            return feature
        }

    }

    private fun log(call: ApplicationCall, duration: Long) {
        log.log(logLevel) {
//            val testCall = call as TestApplicationCall
//            val routeCall = call as RoutingApplicationCall
            // routeCall.route.selector

            val request = call.request
            val response = call.response
            // TODO intercepting the request body would require the ~"double read feature"
            // TODO response body
            """
                RequestLoggerFeature says ...
                
                Request:
                ================================
                  - ${request.httpMethod.value} ${request.origin.fullUri}
                  - Path: ${request.path()}
                  - Headers: ${request.headers}
                  
                Response:
                ================================
                  - Status: ${response.status()}
                  - Headers: ${response.headers.allValues().toMap()}
                  - Time needed: ${duration}ms
                  
            """.trimIndent() }
    }

    private fun KLogger.log(level: Level, messageProvider: () -> String) {
        when(level) {
            Level.ERROR -> error(messageProvider)
            Level.WARN -> warn(messageProvider)
            Level.INFO -> info(messageProvider)
            Level.DEBUG -> debug(messageProvider)
            Level.TRACE -> trace(messageProvider)
        }
    }


}
private val RequestConnectionPoint.fullUri get() = "$scheme://$host:$port$uri"
/*
pipeline.environment.monitor.subscribe(Routing.RoutingCallStarted) { call: RoutingApplicationCall ->
    println("Route started: ${call.route}")
}

pipeline.environment.monitor.subscribe(Routing.RoutingCallFinished) { call: RoutingApplicationCall ->
    println("Route completed: ${call.route}")
}

 */
