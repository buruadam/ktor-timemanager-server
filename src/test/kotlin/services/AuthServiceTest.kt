package com.adam.buru.services

import com.adam.buru.models.User
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthServiceTest {

    private val authService = mockk<AuthService>()

    @Test
    fun testAuthenticateUserService() = runBlocking {
        val email = "test@example.com"
        val password = "password123"
        val expectedToken = "mocked-jwt-token"

        coEvery { authService.authenticateUser(email, password) } returns expectedToken

        val result = authService.authenticateUser(email, password)

        assertEquals(expectedToken, result)
    }

    @Test
    fun testRegisterUserService() = runBlocking {
        val user = User(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "securepassword123"
        )

        coEvery { authService.registerUser(any()) } returns user

        val result = authService.registerUser(user)

        assertNotNull(result)
        assertEquals(user.id, result?.id)
        assertEquals(user.email, result?.email)
    }
}