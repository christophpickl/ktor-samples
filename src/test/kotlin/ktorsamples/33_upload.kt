package ktorsamples

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.application.call
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.http.headersOf
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.testng.annotations.Test
import java.io.File
import java.io.InputStream
import java.io.OutputStream

@Test
class UploadDemo {
    fun `Upload test`() {
        withTestApplication({
            routing {
                post {
                    val multipart = call.receiveMultipart()
                    multipart.forEachPart { part ->
                        when (part) {
//                            is PartData.FormItem -> {
//                                if (part.name == "title") {
//                                    title = part.value
//                                }
//                            }
                            is PartData.FileItem -> {
                                val ext = File(part.originalFileName!!).extension
//                                val file = File(uploadDir, "upload-${System.currentTimeMillis()}-${session.userId.hashCode()}-${title.hashCode()}.$ext")
                                val file = File("build", "upload-${System.currentTimeMillis()}.$ext")
                                println("saving to: ${file.canonicalPath}")
                                part.streamProvider().use { input -> file.outputStream().buffered().use { output -> input.copyToSuspend(output) } }
                            }
                        }

                        part.dispose()
                    }

                    call.respond(HttpStatusCode.OK)
                }
            }
        }) {
            val file = File("src/test/resources/test.pdf")
            handleRequest(HttpMethod.Post, "") {
                val boundary = "***bbb***"
                // addHeader("Content-Type", "multipart/form-data; boundary=AaB03x")
                addHeader(HttpHeaders.ContentType, ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString())
                val input = file.inputStream()
                setBody(boundary, listOf(PartData.FileItem(
                    provider = { input.asInput() },
                    dispose = { input.close() },
//                    partHeaders = Headers.build { append("Content-Disposition", "form-data; name=\"files\"; filename=\"test.pdf\"") }))
                    partHeaders = headersOf(
                        HttpHeaders.ContentDisposition to listOf(ContentDisposition.File
                            .withParameter(ContentDisposition.Parameters.Name, "file")
                            .withParameter(ContentDisposition.Parameters.FileName, "test.pdf")
                            .toString())
                    ))
                ))
            }.apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            }
        }
    }
}

private suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}
