package com.nooblabs

import com.fasterxml.jackson.databind.SerializationFeature
import com.nooblabs.api.auth
import com.nooblabs.api.profile
import com.nooblabs.service.AuthService
import com.nooblabs.service.DatabaseFactory
import com.nooblabs.service.ProfileService
import com.nooblabs.util.AuthenticationException
import com.nooblabs.util.AuthorizationException
import com.nooblabs.util.MissingParameter
import com.nooblabs.util.SimpleJWT
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
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

    routing {
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
            exception<MissingParameter>() { cause ->
                call.respond(HttpStatusCode.BadRequest, "missing" to cause.param)
            }

        }

        route("/api") {

            get {
                call.respond("Welcome to Realworld")
            }

            auth(authService, simpleJWT)
        }
    }
}


