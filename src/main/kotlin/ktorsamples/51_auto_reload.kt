package ktorsamples

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

// SEE: https://ktor.io/servers/autoreload.html

fun main() {
    embeddedServer(
        factory = Netty,
        // watch keys are just strings that are matched with contains, against the classpath entries
        watchPaths = listOf("ktor-samples"),
        // When using watchPaths you should NOT use a LAMBDA to configure the server
        // but to provide a method reference to your Application module
        module = Application::myModule).start(wait = true)
}

// let gradle auto-recompile on source changes:
// >> $ ./gradlew -t installDist
// THEN: You can then use another terminal to run the application with
// >> $ ./gradlew run
//       If you use IntelliJ IDEA to run the application, you should properly configure its compilation output locations
//       because it uses a different output location from that gradle uses.
fun Application.myModule() {
    routing {
        get {
            call.respondText("foobar2")
        }
    }
}
