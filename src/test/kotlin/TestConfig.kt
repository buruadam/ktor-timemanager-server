package com.adam.buru

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.*

object TestConfig {
    fun getConfig(): ApplicationConfig {
        return MapApplicationConfig(
            "jwt.secret" to "test-secret",
            "jwt.issuer" to "test-issuer",
            "jwt.audience" to "test-audience",
            "jwt.expiration" to "3600000"
        )
    }

    fun generateToken(): String {
        return JWT.create()
            .withAudience("test-audience")
            .withIssuer("test-issuer")
            .withClaim("id", 1)
            .sign(Algorithm.HMAC256("test-secret"))
    }
}