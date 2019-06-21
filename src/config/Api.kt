package config

import com.nooblabs.api.article
import com.nooblabs.api.auth
import com.nooblabs.api.comment
import com.nooblabs.api.profile
import com.nooblabs.service.*
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.api() {

    val authService = AuthService()
    val profileService = ProfileService()
    val articleService = ArticleService()
    val commentService = CommentService()

    route("/api") {

        get {
            call.respond("Welcome to Realworld")
        }

        auth(authService, simpleJWT)
        profile(profileService)
        article(articleService)
        comment(commentService)

        get("/drop") {
            DatabaseFactory.drop()
            call.respond("OK")
        }
    }

}