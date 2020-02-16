package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpMethod
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.ul
import org.testng.annotations.Test

// SEE: https://ktor.io/servers/features/templates/html-dsl.html

@Test
class HtmlDsl {
    fun `foo bar`() = withTestApplication({
        routing {
            get {
                call.respondHtml {
                    body {
                        h1 { +"Hello" }
                        ul {
                            for (x in 1..3) {
                                li { +"item: $x" }
                            }
                        }
                    }
                }
            }
        }
    }) {
        handleRequest(HttpMethod.Get, "").apply {
            assertThat(response.content).isEqualTo("""<!DOCTYPE html>
                |<html>
                |  <body>
                |    <h1>Hello</h1>
                |    <ul>
                |      <li>item: 1</li>
                |      <li>item: 2</li>
                |      <li>item: 3</li>
                |    </ul>
                |  </body>
                |</html>
                |""".trimMargin())
        }
    }
}
