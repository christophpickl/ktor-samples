# ktor-samples

[Ktor](https://ktor.io) is JetBrain's asynchronous web framework (using coroutines) written in/for Kotlin. Start here: [https://start.ktor.io](https://start.ktor.io)

Hello world in Ktor:
```kotlin
fun main() {
    embeddedServer(Netty) {
        routing {
            get("/") {
                call.respondText("Hello World")
            }
        }
    }.start(wait = true)
}
```
One of it's long-term goal is to provide a unified experience across all kind of **platforms** (JVM, JavaScript, mobile, native),
for server as well client side. For example [it does](https://ktor.io/clients/) to different HTTP clients (Apache, CIO, Jetty, OkHttp, curl)
what Slf4j does to the different logging implementations - and that across platforms!

See also: https://github.com/ktorio/ktor-samples

## Terminology

* Application
    * Call
    * Request
    * Response
    * Environment
    * (Test-)Engine
* Pipeline
* Modules
* Features
* Routing
* Events
* Interceptors
* Server implementations: Netty, Jetty, CIO or Tomcat

# Topics

## Done

* Simple Server
* Monitor Events
* Exception Handling
* Request Logging
* Request parameters/headers
* Serialization (Jackson)
* Config files (HOCON & ENV)
* Ktor HTTP Client (Apache)
* Authentication Basic
* Test Engine
* ShutDown URL
* Static content
* ktor html builder

## Do

* downloadable file
* caching (etags)
* content negotiation (offer JSON / XML / CSV / HTML / plain text)

### Must

* Metrics (micrometer, dropwizard)
* Compression
* Templates: freemarker/velocity/mustache/thymeleaf
* Custom Feature

### Maybe

* ConditionalHeaders (optional response with ETags)
* CachingHeaders
* Enable CORS
* Locations (typed routes, experimental)
* SSL: https://ktor.io/servers/configuration.html
* Authentication JWT
* Authentication OAuth
* Session
* WebSockets
* Raw sockets

## Wont

* In-detail HTTP client
* kotlinx serialization
* More multiplatform "stuff"

# Ktor Artifacts

Ktor is split into several groups of modules:
* `ktor-server` contains modules that support running the Ktor Application with different engines: Netty, Jetty, Tomcat, and a generic servlet. It also contains a TestEngine for setting up application tests without starting the real server
    * `ktor-server-core` is a core package where most of the application API and implementation is located
    * `ktor-server-jetty` supports a deployed or embedded Jetty instance
    * `ktor-server-netty` supports Netty in embedded mode
    * `ktor-server-tomcat` supports Tomcat servers
    * `ktor-server-servlet` is used by Jetty and Tomcat and allows running in a generic servlet container
    * `ktor-server-test-host` allows running application tests faster without starting the full host
* `ktor-features` groups modules for features that are optional and may not be required by every application
    * `ktor-auth` provides support for different authentication systems like Basic, Digest, Forms, OAuth 1a and 2
    * `ktor-auth-jwt` adds the ability to authenticate against JWT
    * `ktor-auth-ldap` adds the ability to authenticate against LDAP instance
    * `ktor-freemarker` integrates Ktor with Freemarker templates
    * `ktor-velocity` integrates Ktor with Velocity templates
    * `ktor-gson` integrates with Gson adding JSON content negotiation
    * `ktor-jackson` integrates with Jackson adding JSON content negotiation
    * `ktor-html-builder` integrates Ktor with kotlinx.html builders
    * `ktor-locations` contains experimental support for typed locations
    * `ktor-metrics` adds the ability to add some metrics to the server
    * `ktor-server-sessions` adds the ability to use stateful sessions stored on a server
    * `ktor-websockets` provides support for Websockets
* `ktor-client` contains modules for performing http requests
    * `ktor-client-core` is a core package where most of the http HttpClient API is located
    * `ktor-client-apache` adds support for the Apache asynchronous HttpClient
    * `ktor-client-cio` adds support for a pure Kotlin Corutine based I/O asynchronous HttpClient
    * `ktor-client-jetty` adds support for Jetty HTTP client
    * `ktor-client-okhttp` adds support for OkHttp client backend.
    * `ktor-client-auth-basic` adds support for authentication
    * `ktor-client-json` adds support for json content negotiation
* `ktor-network` includes raw sockets for client/server, and TCP/UDP
    * `ktor-network-tls` contains TLS support for raw sockets
