package com.adam.buru.routes

import com.adam.buru.TestConfig
import com.adam.buru.models.User
import com.adam.buru.services.AuthService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class AuthRouteTest {

    private fun Application.testModule(authService: AuthService) {
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
            })
        }

        routing {
            authRoute(authService)
        }
    }

    private val authService = mockk<AuthService>()

    @Test
    fun testLoginRoute() = testApplication {
        val config = TestConfig.getConfig()

        val email = "john.doe@example.com"
        val password = "password123"
        val token = "mocked-jwt-token"

        coEvery { authService.authenticateUser(email, password) } returns token

        environment { this.config = config }
        application { testModule(authService) }
        val response = client.post("/api/auth/login") {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody("""{"email":"$email","password":"$password"}""")
        }

        val expectedJson = """{"token":"$token"}"""
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun testRegisterRoute() = testApplication {
        val config = TestConfig.getConfig()

        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val password = "password123"

        val user = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
        )

        coEvery { authService.registerUser(user) } returns user

        environment { this.config = config }
        application { testModule(authService) }
        val response = client.post("/api/auth/register") {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody("""
                {
                    "firstName":"$firstName",
                    "lastName":"$lastName",
                    "email":"$email",
                    "password":"$password"
                }
                """)
        }

        val expectedJson = """{"message":"Registration successful"}"""
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }
}