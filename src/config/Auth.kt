package config

import com.nooblabs.util.SimpleJWT
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.jwt.jwt

val simpleJWT = SimpleJWT("my-secret")

fun Authentication.Configuration.jwt() {
    jwt {
        authSchemes("Token")
        verifier(simpleJWT.verifier)
        validate {
            UserIdPrincipal(it.payload.getClaim("id").asString())
        }
    }
}