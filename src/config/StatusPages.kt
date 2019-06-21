package config

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.nooblabs.util.*
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun StatusPages.Configuration.statusPages() {
    exception<AuthenticationException> {
        call.respond(HttpStatusCode.Unauthorized)
    }
    exception<AuthorizationException> {
        call.respond(HttpStatusCode.Forbidden)
    }
    exception<ValidationException> { cause ->
        call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to cause.params))
    }
    exception<UserExists> {
        call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to mapOf("user" to listOf("exists"))))
    }
    exception<UserDoesNotExists> {
        call.respond(HttpStatusCode.NotFound)
    }
    exception<ArticleDoesNotExist> {
        call.respond(HttpStatusCode.NotFound)
    }
    exception<MissingKotlinParameterException> { cause ->
        call.respond(
            HttpStatusCode.UnprocessableEntity,
            mapOf("errors" to mapOf(cause.parameter.name to listOf("can't be empty")))
        )
    }
    exception<CommentNotFound> {
        call.respond(HttpStatusCode.NotFound)
    }
}