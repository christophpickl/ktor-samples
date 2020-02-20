package ktorsamples

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.github.mustachejava.DefaultMustacheFactory
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.mustache.Mustache
import io.ktor.mustache.MustacheContent
import io.ktor.pebble.Pebble
import io.ktor.pebble.PebbleContent
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.thymeleaf.ThymeleafContent
import io.ktor.velocity.Velocity
import io.ktor.velocity.VelocityContent
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.testng.annotations.Test
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver


// must not be private in order for template engine to detect
data class User(val name: String)

data class Record(val item: String)

@Test
class Templates {

    private val user = User("Christoph")
    private val records = listOf(Record("a1"), Record("a2"), Record("a3"))
    private val templateMap = mapOf("user" to user, "records" to records)

    // https://ktor.io/servers/features/templates/freemarker.html
    // https://freemarker.apache.org/
    fun freemarker(): Unit = withTestApplication({
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        getReturns {
            FreeMarkerContent("freemarker.ftl", templateMap)
        }
    }) {
        handleRequest(HttpMethod.Get, "").apply {
            assertResponseCorrect(response)
        }
    }

    // https://ktor.io/servers/features/templates/mustache.html
    // https://mustache.github.io/
    fun mustache(): Unit = withTestApplication({
        install(Mustache) {
            mustacheFactory = DefaultMustacheFactory("templates")
        }
        getReturns {
            MustacheContent("mustache.hbs", templateMap)
        }
    }) {
        handleRequest(HttpMethod.Get, "").apply {
            assertResponseCorrect(response)
        }
    }

    // https://ktor.io/servers/features/templates/pebble.html
    // https://pebbletemplates.io/
    fun pebble(): Unit = withTestApplication({
        install(Pebble) {
            loader(ClasspathLoader().apply {
                prefix = "templates"
            })
        }
        getReturns {
            PebbleContent("pebble.html", templateMap)
        }
    }) {
        handleRequest(HttpMethod.Get, "").apply {
            assertResponseCorrect(response)
        }
    }

    // https://ktor.io/servers/features/templates/thymeleaf.html
    // https://www.thymeleaf.org/documentation.html
    fun thymeleaf(): Unit = withTestApplication({
        install(Thymeleaf) {
            setTemplateResolver(ClassLoaderTemplateResolver().apply {
                prefix = "templates/"
                suffix = ".html"
                characterEncoding = "utf-8"
            })
        }
        getReturns {
            ThymeleafContent("thymeleaf", templateMap)
        }
    }) {
        handleRequest(HttpMethod.Get, "").apply {
            assertResponseCorrect(response)
        }
    }

    // https://ktor.io/servers/features/templates/velocity.html
    // http://velocity.apache.org/
    fun velocity(): Unit = withTestApplication({
        install(Velocity) {
            "resource.loader"
            setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
            setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
        }
        getReturns {
            VelocityContent("templates/velocity.vl", templateMap)
        }
    }) {
        handleRequest(HttpMethod.Get, "").apply {
            assertResponseCorrect(response)
        }
    }

    private fun assertResponseCorrect(response: TestApplicationResponse) {
        assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
        assertThat(response.content).isNotNull().all {
            contains("<h2>Hello ${user.name}!</h2>")
            records.forEach { record ->
                contains("<li>${record.item}</li>")
            }
        }
    }

    private fun Application.getReturns(contentProvider: () -> Any) {
        routing {
            get {
                call.respond(contentProvider())
            }
        }
    }
}
