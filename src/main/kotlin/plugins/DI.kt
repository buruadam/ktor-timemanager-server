package com.adam.buru.plugins

import com.adam.buru.di.appModule
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    val config = environment.config

    install(Koin) {
        modules(
            module {
                single { config }
            },
            appModule
        )
    }
}