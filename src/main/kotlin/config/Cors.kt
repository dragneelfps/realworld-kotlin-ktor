package config

import io.ktor.http.*
import io.ktor.server.plugins.cors.*
import kotlin.time.Duration.Companion.days

fun CORSConfig.cors() {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowHeader(HttpHeaders.AccessControlAllowHeaders)
    allowHeader(HttpHeaders.AccessControlAllowOrigin)
    allowHeader(HttpHeaders.Authorization)
    allowCredentials = true
    allowSameOrigin = true
    anyHost()
    maxAgeDuration = 1.days
}