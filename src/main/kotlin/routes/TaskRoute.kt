package com.adam.buru.routes

import com.adam.buru.models.Task
import com.adam.buru.services.TaskService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.jwt.*

fun Route.taskRoute(taskService: TaskService) {

    route("/api/tasks") {
        get {
            val principal = call.principal<JWTPrincipal>()
                ?: return@get call.respond(HttpStatusCode.Unauthorized)

            val userId = principal.payload.getClaim("id").asInt()

            val tasks = taskService.getAllTasksByUserId(userId)
            call.respond(HttpStatusCode.OK, tasks)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID format"))
                return@get
            }
            val task = taskService.getTaskById(id)
            if (task != null) {
                call.respond(HttpStatusCode.OK, task)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
            }
        }

        post {
            val task = call.receive<Task>()
            if (task.title.isBlank() || task.description.isBlank() || task.dueDate.toString().isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "All fields are required"))
                return@post
            }

            val validPriorities = taskService.getAllPriorities().map { it.id }
            if (task.priorityId !in validPriorities) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid priority ID"))
                return@post
            }

            val newTask = taskService.createTask(task)
            if (newTask != null) {
                call.respond(HttpStatusCode.Created, mapOf("message" to "Task created successfully"))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Failed to create task")
            }
        }

        put("/{id}") {

            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID format"))
                return@put
            }

            val request = call.receive<Task>().copy(id = id)
            if (request.title.isBlank() || request.description.isBlank() || request.dueDate.toString().isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "All fields are required"))
                return@put
            }

            val task = Task(
                id = request.id,
                userId = request.userId,
                priorityId = request.priorityId,
                title = request.title,
                description = request.description,
                dueDate = request.dueDate
            )

            val success = taskService.updateTask(task)
            if (success) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Task updated successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID format"))
                return@delete
            }
            val success = taskService.deleteTask(id)
            if (success) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Task deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
            }
        }

        get("/count/all") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@get call.respond(HttpStatusCode.Unauthorized)

            val userId = principal.payload.getClaim("id").asInt()
            val taskCount = taskService.getTaskCountByUserId(userId)
            call.respond(HttpStatusCode.OK, mapOf("taskCount" to taskCount))
        }

        get("/count/done") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@get call.respond(HttpStatusCode.Unauthorized)

            val userId = principal.payload.getClaim("id").asInt()
            val doneTaskCount = taskService.getDoneTaskCountByUserId(userId)
            call.respond(HttpStatusCode.OK, mapOf("doneTaskCount" to doneTaskCount))
        }

        put("/completion/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID format"))
                return@put
            }

            val isCompleted = call.receive<Boolean>()
            val success = taskService.setTaskCompletion(id, isCompleted)
            if (success) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Task completion status updated successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
            }
        }

        get("/priorities") {
            val priorities = taskService.getAllPriorities()
            call.respond(HttpStatusCode.OK, priorities)
        }
    }
}
