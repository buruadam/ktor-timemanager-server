package com.adam.buru.routes

import com.adam.buru.TestConfig
import com.adam.buru.models.User
import com.adam.buru.services.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import org.junit.Test

class UserRouteTest {

    private fun Application.testModule(userService: UserService) {
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
            })
        }

        authentication {
            jwt {
                realm = "test-realm"
                verifier(
                    JWT
                        .require(Algorithm.HMAC256("test-secret"))
                        .withAudience("test-audience")
                        .withIssuer("test-issuer")
                        .build())
                validate { credential ->
                    if (credential.payload.getClaim("email").asString() != "") {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                }
            }
        }

        routing {
            authenticate {
                userRoute(userService)
            }
        }
    }

    private val userService = mockk<UserService>()

    @Test
    fun testGetUserByIdRoute() = testApplication {
        val config = TestConfig.getConfig()

        val userId = 1
        val user = User(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "hashedpassword",
            salt = "randomsalt"
        )

        coEvery { userService.getUserById(userId) } returns user

        environment { this.config = config }
        application { testModule(userService) }
        val token = TestConfig.generateToken()
        val response = client.get("/api/users/$userId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
        }
        val expectedJson = Json.encodeToString(user)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }
}