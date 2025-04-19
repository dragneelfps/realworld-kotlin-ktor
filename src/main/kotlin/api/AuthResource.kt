package com.nooblabs.api

import com.nooblabs.models.LoginUser
import com.nooblabs.models.RegisterUser
import com.nooblabs.models.UpdateUser
import com.nooblabs.models.UserResponse
import com.nooblabs.service.IAuthService
import com.nooblabs.util.SimpleJWT
import com.nooblabs.util.userId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.auth(authService: IAuthService, simpleJWT: SimpleJWT) {

    /*
        Registration:
        POST /api/users
     */
    post("/users") {
        val registerUser = call.receive<RegisterUser>()
        val newUser = authService.register(registerUser)
        call.respond(UserResponse.fromUser(newUser, token = simpleJWT.sign(newUser.id)))
    }

    /*
        Authentication:
        POST /api/users/login
     */
    post("/users/login") {
        val loginUser = call.receive<LoginUser>()
        val user = authService.loginAndGetUser(loginUser.user.email, loginUser.user.password)
        call.respond(UserResponse.fromUser(user, token = simpleJWT.sign(user.id)))
    }

    authenticate {

        /*
            Get Current User
            GET /api/user
         */
        get("/user") {
            val user = authService.getUserById(call.userId())
            call.respond(UserResponse.fromUser(user))
        }

        /*
            Update User
            PUT /api/user
         */
        put("/user") {
            val updateUser = call.receive<UpdateUser>()
            val user = authService.updateUser(call.userId(), updateUser)
            call.respond(UserResponse.fromUser(user, token = simpleJWT.sign(user.id)))
        }

    }

    //For development purposes
    //Returns all users
    get("/users") {
        val users = authService.getAllUsers()
        call.respond(users.map { UserResponse.fromUser(it) })
    }

}