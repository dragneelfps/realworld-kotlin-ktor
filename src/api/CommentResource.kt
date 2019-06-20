package com.nooblabs.api

import com.nooblabs.models.PostComment
import com.nooblabs.service.CommentService
import com.nooblabs.util.param
import com.nooblabs.util.userId
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post

fun Route.comment(commentService: CommentService) {

    authenticate {
        post("/articles/{slug}/comments") {
            val slug = call.param("slug")
            val postComment = call.receive<PostComment>()
            val comment = commentService.addComment(call.userId(), slug, postComment)
            call.respond(comment)
        }

        delete("/articles/{slug}/comments/{id}") {
            val slug = call.param("slug")
            val id = call.param("id").toInt()
            commentService.deleteComment(call.userId(), slug, id)
            call.respond(HttpStatusCode.OK)
        }
    }

    authenticate(optional = true) {
        get("/articles/{slug}/comments") {
            val slug = call.param("slug")
            val userId = call.principal<UserIdPrincipal>()?.name
            val comments = commentService.getComments(userId, slug)
            call.respond(mapOf("comments" to comments))
        }
    }

}