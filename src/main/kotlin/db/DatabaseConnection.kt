package com.adam.buru.db

import com.adam.buru.models.TaskPriorities
import com.adam.buru.models.Tasks
import com.adam.buru.models.Users
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val configuration = environment.config.config("ktor.database")
    val url = configuration.property("url").getString()
    val driver = configuration.property("driver").getString()
    val user = configuration.property("user").getString()
    val password = configuration.property("password").getString()

    val db = Database.connect(
        url = url,
        driver = driver,
        user = user,
        password = password
    )

    transaction(db) {
        SchemaUtils.create(Users, Tasks, TaskPriorities)
    }
    TaskPriorities.initializeData()
}

suspend fun <T> dbQuery(block: suspend () -> T): T {
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}