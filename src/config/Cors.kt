package config

import io.ktor.features.CORS
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import java.time.Duration

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
    maxAge = Duration.ofDays(1)
}