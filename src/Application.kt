package com.nooblabs

import com.fasterxml.jackson.databind.SerializationFeature
import com.nooblabs.api.article
import com.nooblabs.api.auth
import com.nooblabs.api.profile
import com.nooblabs.service.ArticleService
import com.nooblabs.service.AuthService
import com.nooblabs.service.DatabaseFactory
import com.nooblabs.service.DatabaseFactory.drop
import com.nooblabs.service.ProfileService
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

    routing {
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
            exception<MissingParameter>() { cause ->
                call.respond(HttpStatusCode.BadRequest, mapOf("missing" to cause.params))
            }
            exception<UserExists> {
                call.respond(HttpStatusCode.BadRequest, mapOf("reason" to "user exists"))
            }
            exception<UserDoesNotExists> {
                call.respond(HttpStatusCode.NotFound, mapOf("reason" to "user doesnt not exists"))
            }
            exception<ArticleDoesNotExist> { cause ->
                call.respond(HttpStatusCode.NotFound, mapOf("slug" to cause.slug))
            }

        }

        route("/api") {

            get {
                call.respond("Welcome to Realworld")
            }

            auth(authService, simpleJWT)
            profile(profileService)
            article(articleService)

            get("/drop") {
                drop()
                call.respond("OK")
            }
        }
    }
}


