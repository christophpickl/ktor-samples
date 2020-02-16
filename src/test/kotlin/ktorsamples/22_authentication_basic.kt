package ktorsamples

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.basic
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test
import java.util.Base64

@Test
class AuthenticationBasicTest {
    
    private val authName = "basicAuth"
    private val userAndPassword = "foo" to "bar"

    fun `Given secured endpoint When get it with or without credentials Then succeed or fail`() = withTestApplication({
        install(Authentication) {
            basic(name = authName) {
                realm = "demo realm"
                validate { credentials ->
                    if (credentials.name == userAndPassword.first && credentials.password == userAndPassword.second) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
                // disable: skipWhen {  }
            }
        }
        routing {
            authenticate(authName) {
                get("/secure") {
                    val user = call.authentication.principal as UserIdPrincipal
                    call.respondText("top secret message for ${user.name}")
                }
            }
        }
    }) {
        
        handleRequest(HttpMethod.Get, "/secure").apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.Unauthorized)
        }
        
        handleRequest(HttpMethod.Get, "/secure") {
            val encodedCredentials = Base64.getEncoder().encodeToString("${userAndPassword.first}:${userAndPassword.second}".toByteArray())
            addHeader("Authorization", "Basic $encodedCredentials")
        }.apply {
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            assertThat(response.content).isNotNull().all {
                contains("secret")
                contains(userAndPassword.first)
            }
        }
        Unit // :( due to withTestApplication returning this' value of last expression and TestNG being strict with return types
    }
}
