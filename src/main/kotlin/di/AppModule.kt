package com.adam.buru.di

import com.adam.buru.services.*
import io.ktor.server.config.*
import org.koin.dsl.module

val appModule = module {
    single<AuthService> { AuthServiceImpl(get()) }
    single<TaskService> { TaskServiceImpl() }
    single<UserService> { UserServiceImpl() }
    single<JwtService> {
        val config = get<ApplicationConfig>()
        JwtService(config)
    }
}