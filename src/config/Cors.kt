package config

import io.ktor.features.CORS
import io.ktor.features.maxAgeDuration
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun CORS.Configuration.cors() {
    method(HttpMethod.Options)
    method(HttpMethod.Get)
    method(HttpMethod.Post)
    method(HttpMethod.Put)
    method(HttpMethod.Delete)
    header(HttpHeaders.AccessControlAllowHeaders)
    header(HttpHeaders.AccessControlAllowOrigin)
    header(HttpHeaders.Authorization)
    allowCredentials = true
    allowSameOrigin = true
    anyHost()
    maxAgeDuration = Duration.days(1)
}