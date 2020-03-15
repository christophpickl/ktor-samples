package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CachingHeaders
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

// see: https://ktor.io/servers/features/caching-headers.html

@Test
class CachingHeadersDemo {

    fun `Given caching enabled for CSS When get route Then response proper headers`() {
        withTestApplication({
            install(CachingHeaders) {
                options { outgoingContent ->
                    when (outgoingContent.contentType?.withoutParameters()) {
                        ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(
                            maxAgeSeconds = 21,
                            proxyMaxAgeSeconds = 42,
                            mustRevalidate = true,
                            proxyRevalidate = true,
                            visibility = CacheControl.Visibility.Public
                        ))
                        else -> null
                    }
                }
            }
            routing {
                get {
                    call.respondText(contentType = ContentType.Text.CSS, text = "{}")
                }
            }
        }) {
            handleRequest(HttpMethod.Get, "/").response.also { response ->
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.headers.contains("Cache-Control")).isTrue()
                assertThat(response.headers["Cache-Control"]).isEqualTo("max-age=21, s-maxage=42, must-revalidate, proxy-revalidate, public")
            }
        }
    }
}
