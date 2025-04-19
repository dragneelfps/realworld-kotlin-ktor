package config

import com.nooblabs.util.*
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun StatusPagesConfig.statusPages() {
    exception<Throwable> { call, cause ->
        when (cause) {
            is AuthenticationException -> call.respond(HttpStatusCode.Unauthorized)
            is AuthorizationException -> call.respond(HttpStatusCode.Forbidden)
            is ValidationException -> call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to cause.params))
            is UserExists -> call.respond(
                HttpStatusCode.UnprocessableEntity,
                mapOf("errors" to mapOf("user" to listOf("exists")))
            )
            is UserDoesNotExists, is ArticleDoesNotExist, is CommentNotFound -> call.respond(HttpStatusCode.NotFound)
        }
    }
}