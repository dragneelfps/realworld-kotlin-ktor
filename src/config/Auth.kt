package config

import com.nooblabs.util.SimpleJWT
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun AuthenticationConfig.jwtConfig(simpleJWT: SimpleJWT) {

    jwt {
        authSchemes("Token")
        verifier(simpleJWT.verifier)
        validate {
            UserIdPrincipal(it.payload.getClaim("id").asString())
        }
    }
}