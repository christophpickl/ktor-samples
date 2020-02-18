package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.HttpMethod
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import org.testng.annotations.Test

// SEE: https://ktor.io/advanced/features.html
// see: https://github.com/ktorio/ktor-samples/blob/master/feature/custom-feature/src/CustomHeader.kt

@Test
class CustomFeatureTest {
    fun `Given custom header feature installed When get an endpoint Then return custom header`() {
        withTestApplication({
            install(AlwaysSetHeaderFeature) {
                headerName = "X-foo"
                headerValue = "bar"
            }
            routing {
                get {
                    call.respondText("hello custom feature")
                }
            }
        }) {
            handleRequest(HttpMethod.Get, "").apply {
                assertThat(response.headers["X-foo"]).isEqualTo("bar")
            }
        }
    }
}

class AlwaysSetHeaderFeature(configuration: Configuration) {
    private val headerName = configuration.headerName
    private val headerValue = configuration.headerValue

    private fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        context.call.response.header(headerName, headerValue)
    }

    class Configuration {
        var headerName = "X-custom-header"
        var headerValue = "default value"
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, AlwaysSetHeaderFeature> {

        override val key = AttributeKey<AlwaysSetHeaderFeature>("AlwaysSetHeaderFeature")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): AlwaysSetHeaderFeature {
            val feature = AlwaysSetHeaderFeature(Configuration().apply(configure))
            pipeline.intercept(ApplicationCallPipeline.Call) {
                feature.intercept(this)
            }
            return feature
        }
    }
}
