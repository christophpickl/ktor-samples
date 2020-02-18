package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.Compression
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.features.matchContentType
import io.ktor.features.minimumSize
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.response.respondBytes
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

@Test
class CompressionTest {
    fun `Given compression installed When get endpoint Then return compressed body`() {
        withTestApplication({
            install(Compression) {
                // gzip will be selected for all contents less than 1K in size
                // and all the rest will be encoded with deflate encoder
                gzip {
                    priority = 1.0
                    matchContentType(ContentType.Image.Any)
//                    excludeContentType(ContentType.Text.Any)
//                    condition {
//                         parameters["foo"] == "bar"
//                    }
                }
                deflate {
                    priority = 10.0
                    minimumSize(1024)
                }
            }

            routing {
                get {
                    call.respondBytes(byteArrayOf(0, 1, 1, 0), ContentType.Image.GIF)
                }
            }
        }) {
            handleRequest(HttpMethod.Get, "") {
                addHeader("Accept-Encoding", "gzip, deflate")
            }.apply {
                assertThat(response.headers["Content-Encoding"]).isEqualTo("gzip")
            }
        }
    }
}
