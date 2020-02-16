package ktorsamples

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.file
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test
import java.io.File

@Test
class StaticContent {

    fun `Given static file When get it Then return it`() = withTestApplication({
        routing {
            static("/remoteStatic") {
                file("remoteFile.foo", File("settings.gradle"))
            }
        }
    }) {
        handleRequest(HttpMethod.Get, "/remoteStatic/remoteFile.foo").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            assertThat(response.content).isEqualTo("""rootProject.name = "ktor-samples"${"\n"}""")
        }
    }

    fun `Given static resources When get a resource Then return it`() = withTestApplication({
        routing {
            static("/static") {
                resources("staticResources")
            }
        }
    }) {
        handleRequest(HttpMethod.Get, "/static/foo.html").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            assertThat(response.content).isNotNull().contains("foo")
        }
    }
}
