package com.adam.buru.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Priority(
    val id: Int,
    val name: String
)

object TaskPriorities : Table("task_priorities") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()

    override val primaryKey = PrimaryKey(id)

    fun initializeData() {
        transaction {
            val existingPriorities = TaskPriorities.selectAll().map { it[name] }.toSet()
            val defaultPriorities = listOf("Low", "Medium", "High")

            defaultPriorities.forEach { priority ->
                if (priority !in existingPriorities) {
                    TaskPriorities.insert {
                        it[name] = priority
                    }
                }
            }
        }
    }
}
