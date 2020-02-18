package ktorsamples

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

// SEE: https://ktor.io/servers/deploy/packing/fatjar.html
// $ ./gradlew build
// $ java -jar build/libs/ktor-samples-all.jar

fun main() {
    println("Starting server at http://localhost:80")
    embeddedServer(Netty) {
        routing {
            get {
                call.respondText("Hello FatJar!")
            }
        }
    }.start(wait = true)
}
