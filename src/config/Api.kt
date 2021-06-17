package config

import com.nooblabs.api.article
import com.nooblabs.api.auth
import com.nooblabs.api.comment
import com.nooblabs.api.profile
import com.nooblabs.service.IArticleService
import com.nooblabs.service.IAuthService
import com.nooblabs.service.ICommentService
import com.nooblabs.service.IDatabaseFactory
import com.nooblabs.service.IProfileService
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import org.koin.ktor.ext.inject

fun Route.api() {

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