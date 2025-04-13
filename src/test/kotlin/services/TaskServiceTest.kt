package com.adam.buru.services

import com.adam.buru.models.Priority
import com.adam.buru.models.Task
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskServiceTest {

    private val taskService = mockk<TaskService>()

    @Test
    fun testGetAllTasksService() = runBlocking {
        val tasks = listOf(
            Task(id = 1, userId = 1, priorityId = 1, isCompleted = false, title = "Task 1", description = "Description 1", dueDate = LocalDateTime.parse("2021-08-01T00:00:00")),
            Task(id = 2, userId = 2, priorityId = 2, isCompleted = true, title = "Task 2", description = "Description 2", dueDate = LocalDateTime.parse("2021-08-02T00:00:00")),
            Task(id = 3, userId = 3, priorityId = 3, isCompleted = false, title = "Task 3", description = "Description 3", dueDate = LocalDateTime.parse("2021-08-03T00:00:00"))
        )

        coEvery { taskService.getAllTasks() } returns tasks

        val result = taskService.getAllTasks()
        assertEquals(tasks, result)
    }

    @Test
    fun testGetAllTasksByUserIdService() = runBlocking {
        val userId = 2
        val tasks = listOf(
            Task(id = 1, userId = userId, priorityId = 1, isCompleted = false, title = "Task 1", description = "Description 1", dueDate = LocalDateTime.parse("2021-08-01T00:00:00")),
            Task(id = 2, userId = userId, priorityId = 2, isCompleted = false, title = "Task 2", description = "Description 2", dueDate = LocalDateTime.parse("2021-08-02T00:00:00"))
        )

        coEvery { taskService.getAllTasksByUserId(userId) } returns tasks

        val result = taskService.getAllTasksByUserId(userId)
        assertEquals(tasks, result)
    }

    @Test
    fun testGetTaskByIdRoute() = runBlocking {
        val taskId = 1
        val task = Task(
            id = taskId,
            userId = 1,
            priorityId = 1,
            isCompleted = false,
            title = "Test Task",
            description = "Test Description",
            dueDate = LocalDateTime.parse("2021-08-01T00:00:00")
        )

        coEvery { taskService.getTaskById(taskId) } returns task

        val result = taskService.getTaskById(taskId)
        assertEquals(task, result)
    }

    @Test
    fun testCreateTaskService() = runBlocking {
        val task = Task(
            id = 1,
            userId = 1,
            priorityId = 1,
            isCompleted = false,
            title = "Test Task",
            description = "Test Description",
            dueDate = LocalDateTime.parse("2021-08-01T00:00:00")
        )

        coEvery { taskService.createTask(task) } returns task

        val result = taskService.createTask(task)
        assertEquals(task, result)
    }

    @Test
    fun testUpdateTaskService() = runBlocking {
        val task = Task(
            id = 1,
            userId = 1,
            priorityId = 1,
            isCompleted = false,
            title = "Updated Task",
            description = "Updated Description",
            dueDate = LocalDateTime.parse("2021-08-01T00:00:00")
        )

        coEvery { taskService.updateTask(task) } returns true

        val result = taskService.updateTask(task)
        assertEquals(true, result)
    }

    @Test
    fun testDeleteTaskService() = runBlocking {
        val taskId = 1

        coEvery { taskService.deleteTask(taskId) } returns true

        val result = taskService.deleteTask(taskId)
        assertEquals(true, result)
    }

    @Test
    fun testGetTaskCountByUserIdService() = runBlocking {
        val userId = 1
        val tasks = listOf(
            Task(id = 1, userId = userId, priorityId = 1, isCompleted = false, title = "Task 1", description = "Description 1", dueDate = LocalDateTime.parse("2021-08-01T00:00:00")),
            Task(id = 2, userId = userId, priorityId = 2, isCompleted = false, title = "Task 2", description = "Description 2", dueDate = LocalDateTime.parse("2021-08-02T00:00:00"))
        )
        val taskCount = 2

        coEvery { taskService.getTaskCountByUserId(userId) } returns tasks.size

        val result = taskService.getTaskCountByUserId(userId)
        assertEquals(taskCount, result)
    }

    @Test
    fun testGetDoneTaskCountByUserIdService() = runBlocking {
        val userId = 1
        val doneTaskCount = 2

        coEvery { taskService.getDoneTaskCountByUserId(userId) } returns doneTaskCount

        val result = taskService.getDoneTaskCountByUserId(userId)
        assertEquals(doneTaskCount, result)
    }

    @Test
    fun testSetTaskCompletionService() = runBlocking {
        val isCompleted = true
        val task = Task(
            id = 1,
            userId = 1,
            priorityId = 1,
            isCompleted = false,
            title = "Updated Task",
            description = "Updated Description",
            dueDate = LocalDateTime.parse("2021-08-01T00:00:00")
        )

        coEvery { taskService.setTaskCompletion(task.id, isCompleted) } returns true

        val result = taskService.setTaskCompletion(task.id, isCompleted)
        assertEquals(true, result)
    }

    @Test
    fun testGetPrioritiesService() = runBlocking {
        val priorities = listOf(
            Priority(1, "Low"),
            Priority(2, "Medium"),
            Priority(3, "High")
        )

        coEvery { taskService.getAllPriorities() } returns priorities

        val result = taskService.getAllPriorities()
        assertEquals(priorities, result)
    }
}