package config

import com.nooblabs.api.article
import com.nooblabs.api.auth
import com.nooblabs.api.comment
import com.nooblabs.api.profile
import com.nooblabs.service.*
import com.nooblabs.util.SimpleJWT
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.api(simpleJWT: SimpleJWT) {

    val authService: IAuthService by inject()
    val profileService: IProfileService by inject()
    val articleService: IArticleService by inject()
    val commentService: ICommentService by inject()
    val databaseFactory: IDatabaseFactory by inject()

    route("/api") {

        get {
            call.respond("Welcome to Realworld")
        }

        auth(authService, simpleJWT)
        profile(profileService)
        article(articleService)
        comment(commentService)

        get("/drop") {
            databaseFactory.drop()
            call.respond("OK")
        }
    }

}