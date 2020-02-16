package ktorsamples

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

// SEE: https://ktor.io/servers/features/status-pages.html

@Test
class ExceptionHandlingTest {

    private val givenStatusCode = HttpStatusCode.Conflict
    private val publicMessage = "public"
    private val privateMessage = "private"

    fun `Given exception handling server When endpoint raises exception Then return status code and public message`() {
        withTestApplication({
            install(StatusPages) {
                exception<Throwable> { cause ->
                    call.respond(HttpStatusCode.InternalServerError)
                    throw cause // rethrow (is recursion safe) and will lead to logging
                }
                exception<MyException> { cause ->
                    call.respond(givenStatusCode, cause.displayMessage)
                }
            }
            routing {
                get {
                    throw MyException(publicMessage, privateMessage)
                }
            }
        }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertThat(response.status()).isEqualTo(givenStatusCode)
                assertThat(response.content).isNotNull().all {
                    contains(publicMessage)
                    doesNotContain(privateMessage)
                }
            }
        }
    }
}

@Suppress("unused")
private class MyException(
    val displayMessage: String,
    internalMessage: String,
    cause: Throwable? = null
) : Exception(internalMessage, cause)
