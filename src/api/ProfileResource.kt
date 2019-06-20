package com.nooblabs.api

import com.nooblabs.service.ProfileService
import com.nooblabs.util.param
import com.nooblabs.util.userId
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post

fun Route.profile(profileService: ProfileService) {

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