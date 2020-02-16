package ktorsamples

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isNotNull
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.toMap
import org.testng.annotations.Test

@Suppress("MapGetWithNotNullAssertionOperator")
@Test
class DefaultHeadersTest {
    
    fun `Given default headers installed When get endpoint Then return more headers`() = withTestApplication({
        install(DefaultHeaders) {
            header("foo", "bar")
        }
        routing { 
            get { 
                call.respondText("haha")
            }
        }
    }) {
        handleRequest(HttpMethod.Get, "").apply {
            val headers = response.headers.allValues().toMap()
            println(headers)
            assertThat(headers).all {
                contains("foo", listOf("bar"))
                transform { it["Date"]!!.first() }.isNotNull() // "Sat, 15 Feb 2020 17:27:21 GMT"
                transform { it["Server"]!!.first() }.contains("ktor-server-core") // "ktor-server-core/1.3.0 ktor-server-core/1.3.0"
            }
        }
    }
}
