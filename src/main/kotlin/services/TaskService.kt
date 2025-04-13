package com.adam.buru.services

import com.adam.buru.db.dbQuery
import com.adam.buru.models.Priority
import com.adam.buru.models.Task
import com.adam.buru.models.TaskPriorities
import com.adam.buru.models.Tasks
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime


interface TaskService {
    suspend fun createTask(task: Task): Task?
    suspend fun updateTask(task: Task): Boolean
    suspend fun deleteTask(id: Int): Boolean
    suspend fun getAllTasks(): List<Task>
    suspend fun getTaskById(id: Int): Task?
    suspend fun getAllTasksByUserId(id: Int): List<Task>
    suspend fun getTaskCountByUserId(userId: Int): Int
    suspend fun getDoneTaskCountByUserId(userId: Int): Int
    suspend fun setTaskCompletion(id: Int, isCompleted: Boolean): Boolean
    suspend fun getAllPriorities(): List<Priority>
}

class TaskServiceImpl : TaskService {

    private fun resultRowToTask(row: ResultRow): Task {
        return Task(
            id = row[Tasks.id],
            userId = row[Tasks.userId],
            priorityId = row[Tasks.priorityId],
            isCompleted = row[Tasks.isCompleted],
            title = row[Tasks.title],
            description = row[Tasks.description],
            dueDate = row[Tasks.dueDate],
            createdAt = row[Tasks.createdAt]
        )
    }

    override suspend fun createTask(task: Task): Task? = dbQuery {
        val insertStatement = Tasks.insert {
            it[userId] = task.userId
            it[priorityId] = task.priorityId
            it[isCompleted] = task.isCompleted
            it[title] = task.title
            it[description] = task.description
            it[dueDate] = task.dueDate
            it[createdAt] = LocalDateTime.now()
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToTask)
    }

    override suspend fun getAllTasks(): List<Task> = dbQuery {
        Tasks.selectAll().map(::resultRowToTask)
    }

    override suspend fun getTaskById(id: Int): Task? = dbQuery {
        Tasks.selectAll()
            .where { Tasks.id eq id }
            .map(::resultRowToTask)
            .singleOrNull()
    }

    override suspend fun updateTask(task: Task): Boolean = dbQuery {
        Tasks.update({ Tasks.id eq task.id }) {
            it[priorityId] = task.priorityId
            it[title] = task.title
            it[description] = task.description
            it[dueDate] = task.dueDate
        }
    } > 0

    override suspend fun deleteTask(id: Int): Boolean = dbQuery {
        Tasks.deleteWhere { Tasks.id eq id } > 0
    }

    override suspend fun getAllTasksByUserId(id: Int): List<Task> = dbQuery {
        Tasks.selectAll().where { Tasks.userId eq id }.map(::resultRowToTask)
    }

    override suspend fun getTaskCountByUserId(userId: Int): Int = dbQuery {
        Tasks.selectAll().where { Tasks.userId eq userId }.count().toInt()
    }

    override suspend fun getDoneTaskCountByUserId(userId: Int): Int = dbQuery {
        Tasks.selectAll().where { (Tasks.userId eq userId) and (Tasks.isCompleted eq true) }.count().toInt()
    }

    override suspend fun setTaskCompletion(id: Int, isCompleted: Boolean): Boolean = dbQuery {
        Tasks.update({ Tasks.id eq id }) {
            it[Tasks.isCompleted] = isCompleted
        } > 0
    }

    override suspend fun getAllPriorities(): List<Priority> = dbQuery {
        TaskPriorities.selectAll().orderBy(TaskPriorities.id to SortOrder.ASC).map {
            Priority(
                id = it[TaskPriorities.id],
                name = it[TaskPriorities.name]
            )
        }
    }
}
