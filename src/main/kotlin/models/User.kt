package com.adam.buru.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String = "",
    val salt: String = ""
)

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val salt = varchar("salt", 32)

    override val primaryKey = PrimaryKey(id)
}