package com.nooblabs.api

import com.nooblabs.service.IProfileService
import com.nooblabs.util.param
import com.nooblabs.util.userId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.profile(profileService: IProfileService) {

    authenticate(optional = true) {

        /*
            Get Profile
            GET /api/profiles/:username
         */
        get("/profiles/{username}") {
            val username = call.param("username")
            val currentUserId = call.principal<UserIdPrincipal>()?.name
            val profile = profileService.getProfile(username, currentUserId)
            call.respond(profile)
        }

    }

    authenticate {

        /*
            Follow user
            POST /api/profiles/:username/follow
         */
        post("/profiles/{username}/follow") {
            val username = call.param("username")
            val currentUserId = call.userId()
            val profile = profileService.changeFollowStatus(username, currentUserId, true)
            call.respond(profile)
        }

        /*
            Unfollow user
            DELETE /api/profiles/:username/follow
         */
        delete("/profiles/{username}/follow") {
            val username = call.param("username")
            val currentUserId = call.userId()
            val profile = profileService.changeFollowStatus(username, currentUserId, false)
            call.respond(profile)
        }

    }

}