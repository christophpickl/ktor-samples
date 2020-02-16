package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit

// https://ktor.io/clients/

@Test
class HttpClientTest {

    private val port = 9001
    private val requestValue = "foobar"

    fun `Given server running When execute http client Then return proper DTO`() {
        val server = embeddedServer(factory = Netty, port = port, module = Application::myModule).apply { start(wait = false) }

        val client = buildHttpClient()
        try {
            runBlocking {
                // val response = client.post<HttpResponse> { // or `HttpStatement` before execution()
                val responseDto = client.post<ClientDto> {
                    url("http://127.0.0.1:$port/")
                    contentType(ContentType.Application.Json)
                    body = ClientDto(value = requestValue)
                }
                assertThat(responseDto).isEqualTo(ClientDto("hello $requestValue"))
            }
        } finally {
            client.close()
            server.stop(1L, 1L, TimeUnit.SECONDS)
        }
    }

    private fun buildHttpClient() = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer() {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
            // serializer = GsonSerializer()
        }
    }
}

private data class ClientDto(val value: String)

private fun Application.myModule() {
    install(ContentNegotiation) { jackson() }
    routing {
        post {
            call.respond(ClientDto("hello ${call.receive<ClientDto>().value}"))
        }
    }
}
