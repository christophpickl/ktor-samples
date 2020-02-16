package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

// usually resides in src/main/kotlin
fun main() {
    embeddedServer(Netty) {
        ktorMain()
    }
}

fun Application.ktorMain() {
    routing {
        get {
            call.respondText("foo")
        }
    }
}

// the test itself
@Test
class IntegrationTest {
    fun `When get root path Then return 200 and proper payload`() = withKtor {
        handleRequest(Get, "/").apply {
            assertThat(response.status()).isEqualTo(OK)
            assertThat(response.content).isEqualTo("foo")
        }
    }
}

// reusable infrastructure
fun withKtor(testCode: TestApplicationEngine.() -> Unit) {
    withTestApplication({
        ktorMain()
    }, testCode)
}
