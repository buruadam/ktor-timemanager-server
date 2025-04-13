package com.adam.buru.routes

import com.adam.buru.TestConfig
import com.adam.buru.models.Priority
import com.adam.buru.models.Task
import com.adam.buru.services.TaskService
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
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class TaskRouteTest {

    private fun Application.testModule(taskService: TaskService) {
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
                taskRoute(taskService)
            }
        }
    }

    private val taskService = mockk<TaskService>()

    private fun generateToken(): String {
        return JWT.create()
            .withAudience("test-audience")
            .withIssuer("test-issuer")
            .withClaim("id", 1)
            .sign(Algorithm.HMAC256("test-secret"))
    }


    @Test
    fun testGetAllTasksByUserIdRoute() = testApplication {
        val config = TestConfig.getConfig()
        val format = Json { encodeDefaults = true }

        val tasks = listOf(
            Task(1, 1, 1, true, "Task 1", "Description 1", LocalDateTime.parse("2021-08-01T00:00:00")),
            Task(2, 1, 1, false, "Task 2", "Description 2", LocalDateTime.parse("2021-08-02T00:00:00"))
        )

        coEvery { taskService.getAllTasksByUserId(1) } returns tasks

        environment { this.config = config }
        application { testModule(taskService) }
        val token = TestConfig.generateToken()
        val response = client.get("/api/tasks") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }

        val expectedJson = format.encodeToString(tasks)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun testGetTaskByIdRoute() = testApplication {
        val config = TestConfig.getConfig()

        val taskId = 1
        val task = Task(
            id = taskId,
            userId = 1,
            priorityId = 1,
            isCompleted = true,
            title = "Test Task",
            description = "Test Description",
            dueDate = LocalDateTime.parse("2021-08-01T00:00:00")
        )

        coEvery { taskService.getTaskById(taskId) } returns task

        testApplication {
            environment {
                this.config = config
            }
            application { testModule(taskService) }
            val token = generateToken()
            val response = client.get("/api/tasks/$taskId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
            }
            val expectedJson = Json.encodeToString(task)
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedJson, response.bodyAsText())
        }
    }

    @Test
    fun testCreateTaskRoute() = testApplication {
        val config = TestConfig.getConfig()

        val newTask = Task(
            id = 0,
            userId = 1,
            priorityId = 1,
            isCompleted = false,
            title = "New Task",
            description = "New Task Description",
            dueDate = LocalDateTime.parse("2021-08-01T00:00:00")
        )

        val createdTask = newTask.copy(id = 1)

        coEvery { taskService.createTask(newTask) } returns createdTask

        coEvery { taskService.getAllPriorities() } returns listOf(
            Priority(1, "High"),
            Priority(2, "Medium"),
            Priority(3, "Low")
        )

        environment { this.config = config }
        application { testModule(taskService) }
        val token = generateToken()
        val response = client.post("/api/tasks") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody(Json.encodeToString(newTask))
        }

        val expectedJson = """{"message":"Task created successfully"}"""
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun testUpdateTaskRoute() = testApplication {
        val config = TestConfig.getConfig()

        val taskId = 1
        val updatedTask = Task(taskId, 1, 1, true, "Updated Task", "Updated Description", LocalDateTime.parse("2021-08-01T00:00:00"))

        coEvery { taskService.updateTask(match { it.id == taskId }) } returns true

        environment { this.config = config }
        application { testModule(taskService) }
        val token = generateToken()
        val response = client.put("/api/tasks/$taskId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody(Json.encodeToString(updatedTask))
        }

        val expectedJson = """{"message":"Task updated successfully"}"""
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun testDeleteTaskRoute() = testApplication {
        val config = TestConfig.getConfig()

        val taskId = 1

        coEvery { taskService.deleteTask(taskId) } returns true

        environment { this.config = config }
        application { testModule(taskService) }
        val token = generateToken()
        val response = client.delete("/api/tasks/$taskId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        val expectedJson = """{"message":"Task deleted successfully"}"""
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun testGetTaskCountRoute() = testApplication {
        val config = TestConfig.getConfig()

        coEvery { taskService.getTaskCountByUserId(1) } returns 5

        environment { this.config = config }
        application { testModule(taskService) }
        val token = generateToken()
        val response = client.get("/api/tasks/count/all") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        val expectedJson = """{"taskCount":5}"""
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun testGetDoneTaskCountRoute() = testApplication {
        val config = TestConfig.getConfig()

        coEvery { taskService.getDoneTaskCountByUserId(1) } returns 3

        environment { this.config = config }
        application { testModule(taskService) }
        val token = generateToken()
        val response = client.get("/api/tasks/count/done") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        val expectedJson = """{"doneTaskCount":3}"""
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun testSetTaskCompletionRoute() = testApplication {
        val config = TestConfig.getConfig()

        val taskId = 1
        val isCompleted = true

        coEvery { taskService.setTaskCompletion(taskId, isCompleted) } returns true

        environment { this.config = config }
        application { testModule(taskService) }
        val token = generateToken()
        val response = client.put("/api/tasks/completion/$taskId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody(Json.encodeToString(isCompleted))
        }
        val expectedJson = """{"message":"Task completion status updated successfully"}"""
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun testGetPrioritiesRoute() = testApplication {
        val config = TestConfig.getConfig()

        val priorities = listOf(
            Priority(1, "High"),
            Priority(2, "Medium"),
            Priority(3, "Low")
        )

        coEvery { taskService.getAllPriorities() } returns priorities

        environment { this.config = config }
        application { testModule(taskService) }
        val token = generateToken()
        val response = client.get("/api/tasks/priorities") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        val expectedJson = Json.encodeToString(priorities)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }
}