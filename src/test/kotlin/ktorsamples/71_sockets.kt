package ktorsamples

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.server.testing.withTestApplication
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.testng.annotations.Test
import java.net.InetSocketAddress

// dependency "ktor-network"
@Test
class RawSockets {
    fun `raw sockets demo`() {
        val port = 4242
        runBlocking {

            launch {
                delay(1_000)
                println("Starting client")
                val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", port))
                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(autoFlush = true)

                output.writeStringUtf8("hello\r\n")
                val response = input.readUTF8Line()
                println("Server said: '$response'")
            }

            val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", port))

            var connections = 0
            val maxConnections = 1

            while (connections < maxConnections) {
                println("Waiting for connection ...")
                val socket = server.accept()
                println("Received connection")
                connections++
                launch {
                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel(autoFlush = true)
                    val line = input.readUTF8Line()
                    output.writeStringUtf8(">> $line\r\n")
                    socket.close()
                }
            }
            println("server shutting down...")
            server.close()
        }

    }
}
