package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

@Test
class SerializerTest {

    fun `Given server with JSON capabilities When get endpoint Then return JSON`() = withTestApplication({
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
        routing {
            get {
                call.respond(SerializeDto(internalFoo = "bar"))
            }
        }
    }) {
        handleRequest(HttpMethod.Get, "/").apply {
            assertThat(response.content).isEqualTo("""{
                |  "foo" : "bar"
                |}""".trimMargin())
        }
    }
}

@Suppress("unused")
private class SerializeDto(
    @JsonProperty("foo")
    val internalFoo: String
)
