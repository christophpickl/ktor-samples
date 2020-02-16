package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

@Test
class SimplestTest {
    fun `Given a routing When get root path Then return 200 OK`() = withTestApplication({
        routing {
            get {
                call.response.status(HttpStatusCode.OK)
            }
        }
    }) {
        val call = handleRequest(HttpMethod.Get, "/")
        assertThat(call.response.status()).isEqualTo(HttpStatusCode.OK)
    }
}
