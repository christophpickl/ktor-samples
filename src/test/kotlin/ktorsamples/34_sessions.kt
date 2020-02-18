package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.sessions.Sessions
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.header
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.testng.annotations.Test

// SEE: https://ktor.io/servers/features/sessions.html

@Test
class SessionsDemo {
    private val sessionHeaderName = "SESSION_HEADER_NAME"
    fun `Given sessions installed When manage session Then react properly`() {
        withTestApplication({
            install(Sessions) {
                // or via cookies ...
                header<SampleSession>(sessionHeaderName /*, SessionStorage */) {
                    // serializer = SessionSerializerReflection
                    // transformers += SessionTransportTransformerMessageAuthentication
                }
            }
            routing {
                get("/get") {
                    val mySession = call.sessions.get<SampleSession>()
                    call.respondText(mySession?.toString() ?: "null")
                }
                get("/set") {
                    // to modify a session (for example incrementing a counter), you have to call the .copy() method
                    call.sessions.set(SampleSession("my name", 42))
                    call.respondText("set")
                }
                get("/clear") {
                    // when a user logs out, or a session should be cleared for any other reason
                    call.sessions.clear<SampleSession>()
                    call.respondText("cleared")
                }
            }
        }) {
            handleRequest(HttpMethod.Get, "/get").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("null")
            }
            handleRequest(HttpMethod.Get, "/set").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("set")
                assertThat(response.headers[sessionHeaderName]).isEqualTo("name=%23smy+name&value=%23i42")
            }
            handleRequest(HttpMethod.Get, "/clear").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("cleared")
            }
            handleRequest(HttpMethod.Get, "/get").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("null")
            }
        }
    }
}

data class SampleSession(val name: String, val value: Int)
