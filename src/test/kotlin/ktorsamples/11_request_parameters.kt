package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.request.header
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

@Test
class RequestParametersTest {

    fun `path parameter`() = withTestApplication({ installPathRoute() }) {
        handleRequest(Get, "/foobar").apply {
            assertThat(response.content).isEqualTo("foobar")
        }
    }

    private fun Application.installPathRoute() {
        routing {
            get("/{param}") {
                val pathValue = call.parameters["param"] ?: ""
                call.respondText(pathValue)
            }
        }
    }

    fun `query parameter`() = withTestApplication({ installQueryRoute(paramName = "foo") }) {
        handleRequest(Get, "/?foo=bar").apply {
            assertThat(response.content).isEqualTo("bar")
        }
    }

    private fun Application.installQueryRoute(paramName: String) {
        routing {
            get {
                val queryParamValue = call.request.queryParameters[paramName] ?: ""
                // OR: val queryParamValue = call.parameters[paramName] ?: ""
                call.respondText(queryParamValue)
            }
        }
    }

    fun `header `() = withTestApplication({ installHeaderRoute(headerName = "foo") }) {
        handleRequest(Get, "/") {
            addHeader("foo", "bar")
        }.apply {
            assertThat(response.content).isEqualTo("bar")
        }
    }

    private fun Application.installHeaderRoute(headerName: String) {
        routing {
            get {
                val headerValue = call.request.header(headerName) ?: ""
                call.respondText(headerValue)
            }
        }
    }
}
