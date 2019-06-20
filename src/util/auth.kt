package com.nooblabs.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

open class SimpleJWT(secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun <T> sign(id: T): String = JWT.create().withClaim("id", id.toString()).sign(algorithm)
}
