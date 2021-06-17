package com.nooblabs

import com.fasterxml.jackson.databind.SerializationFeature
import com.nooblabs.service.IDatabaseFactory
import config.api
import config.cors
import config.jwt
import config.statusPages
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
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

    install(Koin) {
        modules(serviceKoinModule)
        modules(databaseKoinModule)
    }

    val factory: IDatabaseFactory by inject()
    factory.init()

    routing {
        install(StatusPages) {
            statusPages()
        }
        api()
    }
}


