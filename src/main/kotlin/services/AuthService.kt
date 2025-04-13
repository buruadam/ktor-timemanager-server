package com.adam.buru.services

import com.adam.buru.db.dbQuery
import com.adam.buru.models.User
import com.adam.buru.models.Users
import io.ktor.util.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

interface AuthService {
    suspend fun registerUser(user: User): User?
    suspend fun authenticateUser(email: String, password: String): String?
}

class AuthServiceImpl(private val jwtService: JwtService) : AuthService {

    private fun resultRowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            email = row[Users.email]
        )
    }

    private fun generateRandomSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    private fun ByteArray.toHexString(): String =
        HexFormat.of().formatHex(this)

    private fun generateHash(password: String, salt: String): String {
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 120_000, 256)
        val key: SecretKey = factory.generateSecret(spec)
        return key.encoded.toHexString()
    }

    override suspend fun registerUser(user: User): User? = dbQuery {
        val generatedSalt = generateRandomSalt().toHexString()
        val hashedPassword = generateHash(user.password, generatedSalt)

        val existingUser = Users.selectAll().where { Users.email eq user.email }.singleOrNull()
        if (existingUser != null) return@dbQuery null

        val insertStatement = Users.insert {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[password] = hashedPassword
            it[salt] = generatedSalt
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }



    override suspend fun authenticateUser(email: String, password: String): String? = dbQuery {
        Users.selectAll()
            .where { Users.email eq email }
            .mapNotNull {
                val storedPassword = it[Users.password]
                val storedSalt = it[Users.salt]

                if (storedPassword == generateHash(password, storedSalt)) {
                    User(
                        id = it[Users.id],
                        firstName = it[Users.firstName],
                        lastName = it[Users.lastName],
                        email = it[Users.email],
                        password = "",
                        salt = ""
                    )
                    val user = resultRowToUser(it)
                    jwtService.generateToken(user)
                } else {
                    null
                }
            }
            .singleOrNull()
    }
}