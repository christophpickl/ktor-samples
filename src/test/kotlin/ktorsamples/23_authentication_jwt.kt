package ktorsamples

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.auth.principal
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit

// SEE: https://github.com/AndreasVolkmann/ktor-auth-jwt-sample

@Test
class AuthenticationWithJwt {
    fun `Given JWT enabled server When login Then return proper status`() {
//        val issuer = "https://jwt-provider-domain"
        withTestApplication({
            val simpleJwt = SimpleJWT("my-super-secret-for-jwt")
            install(Authentication) {
                jwt {
                    realm = "demo realm"
                    verifier(simpleJwt.verifier)
//                    verifier(makeJwtVerifier(issuer), issuer)
                    validate { credential ->
                        //                        if (credential.payload.audience.contains("jwt-audience")) JWTPrincipal(credential.payload) else null
                        UserIdPrincipal(credential.payload.getClaim("name").asString())
                    }
                }
            }
            routing {
                post {
                    val requestUser = call.request.queryParameters["username"] ?: ""
                    val requestPass = call.request.queryParameters["password"] ?: "" // never send via query params!!!
                    if (requestUser == requestPass) {
                        call.respondText(simpleJwt.sign(requestUser))
                    } else {
                        call.response.status(HttpStatusCode.Forbidden)
                    }
                }
                authenticate {
                    get("/secure") {
                        val user = call.principal<UserIdPrincipal>()!!
                        call.respondText("secret for ${user.name}")
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Post, "?username=foo&password=bar").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.Forbidden)
            }

            handleRequest(HttpMethod.Get, "/secure").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.Unauthorized)
            }

            handleRequest(HttpMethod.Post, "?username=foobar&password=foobar").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isNotNull().isNotEmpty()
                val token = response.content!!
                println("Token: [$token]")

                handleRequest(HttpMethod.Get, "/secure") {
                    addHeader("Authorization", "Bearer $token")
                }.apply {
                    assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                    assertThat(response.content).isNotNull().all {
                        contains("secret")
                        contains("foobar")
                    }
                }
            }
        }
    }

    private fun makeJwtVerifier(issuer: String): JwkProvider {
//        return object : JwkProvider {
//            override fun get(keyId: String?): Jwk {
//                Jwk()
//            }
//        }
        return JwkProviderBuilder(issuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
    }
}

open class SimpleJWT(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create()
        .withClaim("name", name)
        .withClaim("foo", "bar")
        .sign(algorithm)
}
