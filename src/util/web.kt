package com.nooblabs.util

import io.ktor.server.application.*
import io.ktor.server.auth.*


fun ApplicationCall.userId() = principal<UserIdPrincipal>()?.name ?: throw AuthenticationException()

fun ApplicationCall.param(param: String) =
    parameters[param] ?: throw ValidationException(mapOf("param" to listOf("can't be empty")))