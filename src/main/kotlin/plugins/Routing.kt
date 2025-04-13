package com.adam.buru.plugins

import com.adam.buru.routes.authRoute
import com.adam.buru.routes.taskRoute
import com.adam.buru.routes.userRoute
import com.adam.buru.services.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    authService: AuthService = get(),
    taskService: TaskService = get(),
    userService: UserService = get()
) {

    routing {
        authenticate {
            taskRoute(taskService)
            userRoute(userService)
        }
        authRoute(authService)
    }
}
