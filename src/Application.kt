package com.nooblabs

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.nooblabs.api.article
import com.nooblabs.api.auth
import com.nooblabs.api.comment
import com.nooblabs.api.profile
import com.nooblabs.service.*
import com.nooblabs.service.DatabaseFactory.drop
import com.nooblabs.util.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    val simpleJWT = SimpleJWT("my-secret")

    install(CallLogging) {
        level = Level.INFO
    }

    install(Authentication) {
        jwt {
            authSchemes("Token")
            verifier(simpleJWT.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("id").asString())
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    DatabaseFactory.init()

    val authService = AuthService()
    val profileService = ProfileService()
    val articleService = ArticleService()
    val commentService = CommentService()

    routing {
        install(StatusPages) {
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

        route("/api") {

            get {
                call.respond("Welcome to Realworld")
            }

            auth(authService, simpleJWT)
            profile(profileService)
            article(articleService)
            comment(commentService)

            get("/drop") {
                drop()
                call.respond("OK")
            }
        }
    }
}


