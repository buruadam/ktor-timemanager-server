package com.adam.buru.routes

import com.adam.buru.models.User
import com.adam.buru.services.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute(userService: UserService) {

    route("/api/users") {
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            id?.let {
                userService.getUserById(it)?.let { user ->
                    call.respond(HttpStatusCode.OK, user)
                } ?: call.respond(HttpStatusCode.NotFound, "User not found")
            } ?: call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }

        get {
            val users = userService.getAllUsers()
            call.respond(HttpStatusCode.OK, users)
        }


        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val user = call.receive<User>().copy(id = id)
                val updated = userService.updateUser(user)
                if (updated) {
                    call.respond(HttpStatusCode.OK, "User updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val deleted = userService.deleteUser(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "User deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
    }
}