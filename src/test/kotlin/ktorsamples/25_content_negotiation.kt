package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.JacksonConverter
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.pipeline.PipelineContext
import org.testng.annotations.Test

@Test
class ContentNegotiationDemo {

    fun `Given JSON and text supported When request with several accept headers Then react accordingly`() {
        withTestApplication({
            install(ContentNegotiation) {
                register(ContentType.Application.Json, JacksonConverter())
                register(ContentType.Text.Plain, TextConverter())
            }

            routing {
                get {
                    call.respond(Foo("bar"))
                }
            }
        }) {

            handleRequest(Get, "/") {
                addHeader("Accept", "application/json")
            }.response.also {
                assertThat(it.headers["Content-Type"]).isEqualTo("application/json; charset=UTF-8")
                assertThat(it.content).isEqualTo("""{"name":"bar"}""")
            }

            handleRequest(Get, "/") {
                addHeader("Accept", "text/plain")
            }.response.also {
                assertThat(it.headers["Content-Type"]).isEqualTo("text/plain; charset=UTF-8")
                assertThat(it.content).isEqualTo("Foo.name: bar")
            }

            handleRequest(Get, "/") {
                addHeader("Accept", "application/xml")
            }.response.also {
                assertThat(it.status()).isEqualTo(HttpStatusCode.NotAcceptable)
            }
        }
    }

    private data class Foo(val name: String)

    private class TextConverter : ContentConverter {
        override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
            throw UnsupportedOperationException("Receive not supported")
        }

        override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any? {
            if (value is Foo) {
                return "Foo.name: ${value.name}"
            }
            throw UnsupportedOperationException("unhandled value: $value")
        }

    }
}
