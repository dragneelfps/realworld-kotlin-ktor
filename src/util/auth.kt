package com.nooblabs.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.ApplicationCall
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.principal

open class SimpleJWT(secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun <T> sign(id: T): String = JWT.create().withClaim("id", id.toString()).sign(algorithm)
}

fun ApplicationCall.userId() = principal<UserIdPrincipal>()?.name ?: throw AuthenticationException()
