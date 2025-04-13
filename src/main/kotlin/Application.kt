package com.adam.buru

import com.adam.buru.db.configureDatabase
import com.adam.buru.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureDI()
    configureSerialization()
    configureDatabase()
    configureSecurity()
    configureRouting()
}
