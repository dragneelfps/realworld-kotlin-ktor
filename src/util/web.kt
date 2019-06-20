package com.nooblabs.util

import io.ktor.application.ApplicationCall
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.principal


fun ApplicationCall.userId() = principal<UserIdPrincipal>()?.name ?: throw AuthenticationException()

fun ApplicationCall.param(param: String) =
    parameters[param] ?: throw ValidationException(mapOf("param" to listOf("can't be empty")))