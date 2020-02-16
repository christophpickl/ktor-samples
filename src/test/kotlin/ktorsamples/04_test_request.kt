package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.formUrlEncode
import io.ktor.request.receiveParameters
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

// SEE: https://ktor.io/servers/testing.html

@Test
class TestRequest {
    
    private val paramKey = "paramKey"
    private val paramValue = "paramValue"
    
    fun `Given server responds param When Post Then return param value`() = withTestApplication({
        routing {
            post {
                call.respondText(call.receiveParameters()[paramKey] ?: "")
            }
        }
    }) {
        val call = handleRequest(HttpMethod.Post, "/") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(paramKey to paramValue).formUrlEncode())
        }
        assertThat(call.response.content).isEqualTo(paramValue)
    }
}
