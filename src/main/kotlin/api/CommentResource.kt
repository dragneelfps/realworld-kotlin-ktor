package com.nooblabs.api

import com.nooblabs.models.PostComment
import com.nooblabs.service.ICommentService
import com.nooblabs.util.param
import com.nooblabs.util.userId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.comment(commentService: ICommentService) {

    authenticate {

        /*
            Add Comments to an Article
            POST /api/articles/:slug/comments
         */
        post("/articles/{slug}/comments") {
            val slug = call.param("slug")
            val postComment = call.receive<PostComment>()
            val comment = commentService.addComment(call.userId(), slug, postComment)
            call.respond(comment)
        }

        /*
            Delete Comment
            DELETE /api/articles/:slug/comments/:id
         */
        delete("/articles/{slug}/comments/{id}") {
            val slug = call.param("slug")
            val id = call.param("id").toInt()
            commentService.deleteComment(call.userId(), slug, id)
            call.respond(HttpStatusCode.OK)
        }
    }

    authenticate(optional = true) {

        /*
            Get Comments from an Article
            GET /api/articles/:slug/comments
         */
        get("/articles/{slug}/comments") {
            val slug = call.param("slug")
            val userId = call.principal<UserIdPrincipal>()?.name
            val comments = commentService.getComments(userId, slug)
            call.respond(mapOf("comments" to comments))
        }

    }

}