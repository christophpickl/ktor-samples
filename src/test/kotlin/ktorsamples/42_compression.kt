package ktorsamples

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.Compression
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.toMap
import org.testng.annotations.Test

@Test
class CompressionTest {
    fun `Given compression installed When get endpoint Then return compressed body`() = withTestApplication({
        install(Compression)
        routing {
            get {
                call.respondText("haha")
            }
        }
    }) {
        handleRequest(HttpMethod.Get, "").apply {
            println(response.content)
            println(response.contentType())
            println(response.headers.allValues().toMap())
        }
    }
}
