package ktorsamples

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.toMap
import org.testng.annotations.Test
import kotlin.time.ExperimentalTime

// SEE: https://ktor.io/servers/features/cors.html

@Test
class CorsHeaders {
    @ExperimentalTime
    fun `Given CORS installed When get endpoint Then return proper headers`() {
        withTestApplication({
            //            install(CORS) {
//                method(HttpMethod.Options)
//                header(HttpHeaders.XForwardedProto)
//                anyHost()
//                host("my-host")
//                // host("my-host:80")
//                // host("my-host", subDomains = listOf("www"))
//                // host("my-host", schemes = listOf("http", "https"))
//                allowCredentials = true
//                allowNonSimpleContentTypes = true
//                maxAgeDuration = 1.toDuration(DurationUnit.DAYS)
//            }
            routing {
                get {
                    call.respondText("hello cors")
                }
                get("cors.js") {
                    call.respondText("""
                    function foo() {
                    	alert("foo clicked");
                    }
                """.trimIndent(), contentType = ContentType.Application.JavaScript)
                }
            }
        }) {
            handleRequest(HttpMethod.Get, "").apply {
                println(response.status())
                println(response.headers.allValues().toMap())
                println(response.content)
            }
//            <html>
//            <head><script type="application/javascript" src="http://localhost:80/cors.js"></script></head>
//            <body><a href="javascript:foo()">link</a></body>
//            </html>
        }
    }
}

/*
CONFIGURATION:

- method("HTTP_METHOD") : Includes this method to the white list of Http methods to use CORS.
- header("header-name") : Includes this header to the white list of headers to use CORS.
- exposeHeader("header-name") : Exposes this header in the response.
- exposeXHttpMethodOverride() : Exposes X-Http-Method-Override header in the response
- anyHost() : Allows any host to access the resources
- host("hostname") : Allows only the specified host to use CORS, it can have the port number, a list of subDomains or the supported schemes.
- allowCredentials : Includes Access-Control-Allow-Credentials header in the response
- allowNonSimpleContentTypes: Inclues Content-Type request header to the white list for values other than simple content types.
- maxAge: Includes Access-Control-Max-Age header in the response with the given max age
*/
