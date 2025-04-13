package com.adam.buru.routes

import com.adam.buru.models.User
import com.adam.buru.services.AuthService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(authService: AuthService) {
    route("/api/auth") {

        post("/register") {

            val user = call.receive<User>()

            if (user.firstName.isBlank() || user.lastName.isBlank() || user.email.isBlank() || user.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "All fields are required"))
                return@post
            }

            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            if (!user.email.matches(emailRegex)) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid email format"))
                return@post
            }

            val newUser = authService.registerUser(user)
            if (newUser != null) {
                call.respond(HttpStatusCode.Created, mapOf("message" to "Registration successful"))
            } else {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to "User already exists"))
            }
        }

        post("/login") {

            val request = call.receive<Map<String, String>>()

            val email = request["email"]
            val password = request["password"]

            if (email.isNullOrBlank() || password.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email and password are required"))
                return@post
            }

            val user = authService.authenticateUser(email, password)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                return@post
            }

            val token = authService.authenticateUser(email, password)
            if (token != null) {
                call.respond(HttpStatusCode.OK, mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }

        post("/logout") {
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }
    }
}