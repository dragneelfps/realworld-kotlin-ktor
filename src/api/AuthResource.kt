package com.nooblabs.api

import com.nooblabs.models.LoginUser
import com.nooblabs.models.RegisterUser
import com.nooblabs.models.UpdateUser
import com.nooblabs.models.UserResponse
import com.nooblabs.service.AuthService
import com.nooblabs.util.AuthenticationException
import com.nooblabs.util.AuthorizationException
import com.nooblabs.util.SimpleJWT
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put

fun Route.auth(authService: AuthService, simpleJWT: SimpleJWT) {

    post("/users") {
        val registerUser = call.receive<RegisterUser>()
        val newUser = authService.register(registerUser)
        call.respond(UserResponse.fromUser(newUser, token = simpleJWT.sign(newUser.email)))
    }

    post("/users/login") {
        val loginUser = call.receive<LoginUser>()
        val user = authService.loginAndGetUser(loginUser.user.email, loginUser.user.password)
            ?: throw AuthenticationException()
        call.respond(UserResponse.fromUser(user, token = simpleJWT.sign(user.email)))
    }

    authenticate {
        get("/user") {
            val principal = call.principal<UserIdPrincipal>() ?: throw AuthorizationException()
            val user = authService.getUserByEmail(principal.name) ?: error("invalid state")
            call.respond(UserResponse.fromUser(user))
        }

        put("/user") {
            val updateUser = call.receive<UpdateUser>()
            val principal = call.principal<UserIdPrincipal>() ?: throw AuthorizationException()
            val user = authService.updateUser(principal.name, updateUser) ?: throw AuthorizationException()
            call.respond(UserResponse.fromUser(user, token = simpleJWT.sign(user.email)))
        }
    }

    //For development purposes
    get("/users") {
        val users = authService.getAllUsers()
        call.respond(users.map { UserResponse.fromUser(it) })
    }
}