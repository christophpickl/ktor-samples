package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test

// SEE: https://ktor.io/servers/configuration/environments.html

@Test
class ConfigurationEnvironment {
    fun `Given test configuration When get read environment Then returned test configured environment`() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("ktor.environment", Env.Test.propertyValue)
            }
            routing {
                get {
                    call.respondText(customEnvironment.name)
                }
            }
        }, {
        }) {
            handleRequest(HttpMethod.Get, "").apply {
                assertThat(response.content).isEqualTo("Test")
            }
        }
    }
}

private val Application.customEnvironment
    get() = Env.values().first {
        it.propertyValue == environment.config.property("ktor.environment").getString()
    }

private enum class Env(val propertyValue: String) {
    Development("dev"),
    Test("test"),
    Production("prod")
}
