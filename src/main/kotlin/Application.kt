package com.nooblabs

import com.fasterxml.jackson.databind.SerializationFeature
import com.nooblabs.service.IDatabaseFactory
import com.nooblabs.util.SimpleJWT
import config.api
import config.cors
import config.jwtConfig
import config.statusPages
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
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

    val simpleJWT = SimpleJWT(secret = environment.config.property("jwt.secret").getString())

    install(Authentication) {
        jwtConfig(simpleJWT)
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

    install(StatusPages) {
        statusPages()
    }

    routing {

        api(simpleJWT)
    }
}


