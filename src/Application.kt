package com.nooblabs

import com.fasterxml.jackson.databind.SerializationFeature
import com.nooblabs.service.DatabaseFactory
import config.api
import config.cors
import config.jwt
import config.statusPages
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.*
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    install(DefaultHeaders)
    install(CORS) {
        cors()
    }

    install(CallLogging) {
        level = Level.INFO
    }

    install(Authentication) {
        jwt()
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    DatabaseFactory.init()

    routing {
        install(StatusPages) {
            statusPages()
        }
        api()
    }
}


