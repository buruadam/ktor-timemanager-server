package com.adam.buru.models

import com.adam.buru.utils.DateSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

@Serializable
data class Task(
    val id: Int = 0,
    val userId: Int,
    val priorityId: Int,
    val isCompleted: Boolean = false,
    val title: String,
    val description: String,
    @Serializable(with = DateSerializer::class) val dueDate: LocalDateTime,
    @Serializable(with = DateSerializer::class) val createdAt: LocalDateTime = LocalDateTime.now()
)

object Tasks : Table("tasks") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val priorityId = integer("priority_id").references(TaskPriorities.id)
    val isCompleted = bool("is_completed").default(false)
    val title = varchar("title", 255)
    val description = text("description")
    val dueDate = datetime("due_date")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}

