package com.adam.buru.services

import com.adam.buru.db.dbQuery
import com.adam.buru.models.User
import com.adam.buru.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface UserService {
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUser(id: Int): Boolean
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Int): User?
}

class UserServiceImpl : UserService {

    private fun resultRowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            email = row[Users.email],
            password = row[Users.password],
            salt = row[Users.salt]
        )
    }

    override suspend fun updateUser(user: User): Boolean = dbQuery {
        Users.update({ Users.id eq user.id }) {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
        } > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    override suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun getUserById(id: Int): User? = dbQuery {
        Users.selectAll()
            .where { Users.id eq id }
            .map {
                User(
                    firstName = it[Users.firstName],
                    lastName = it[Users.lastName],
                    email = it[Users.email]
                )
            }
            .singleOrNull()
    }
}