package com.adam.buru.services

import com.adam.buru.models.User
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class UserServiceTest {

    private val userService = mockk<UserService>()

    @Test
    fun testGetUserByIdService() = runBlocking {
        val userId = 2
        val user = User(
            id = userId,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com"
        )

        coEvery { userService.getUserById(userId) } returns user

        val result = userService.getUserById(userId)

        assertEquals(user, result)
    }
}