package ktorsamples

import io.ktor.application.Application
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

// see: src/main/resources/application.conf
// -config=anotherfile.conf

fun main() {
    // SEE: https://ktor.io/servers/configuration.html#accessing-config
    // need commandLineEnvironment to load conf file
    embeddedServer(Netty, commandLineEnvironment(emptyArray())).start().stop(1_000L, 1_000L)
    // or simply: `val port = System.getenv("PORT")?.toInt() ?: 8080` to avoid all the HOCON magic ;)
}

@Suppress("unused")
fun Application.main() {
    printProperty("ktor.environment")
    printProperty("custom.fromAppConf")
    printProperty("custom.fromProperties")
    printProperty("custom.fromJson")
    printProperty("custom.override")
}

private fun Application.printProperty(propertyName: String) {
    println("$propertyName = ${environment.config.property(propertyName).getString()}")
    
}
