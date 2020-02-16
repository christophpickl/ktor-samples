package ktorsamples

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.slf4j.event.Level
import org.testng.annotations.Test

// SEE: https://ktor.io/servers/features/call-logging.html

@Test
class RequestLogging {
    fun `Given call loging server When get endpoints Then logs written`() {
        withTestApplication({
            install(CallLogging) {
                level = Level.INFO
                filter { call -> call.request.path().startsWith("/foo") }
            }
            routing {
                get("/foo") {
                    call.response.status(HttpStatusCode.OK)
                }
                get("/foo/faa") {
                    call.response.status(HttpStatusCode.OK)
                }
                get("/bar") {
                    call.response.status(HttpStatusCode.OK)
                }
            }
        }) {
            println("ASSERT => log for `GET /foo`")
            handleRequest(HttpMethod.Get, "/foo")
            println("ASSERT => log for `GET /foo/faa`")
            handleRequest(HttpMethod.Get, "/foo/faa")
            println("ASSERT => no log for `GET /bar`")
            handleRequest(HttpMethod.Get, "/bar")
        }
    }
}
