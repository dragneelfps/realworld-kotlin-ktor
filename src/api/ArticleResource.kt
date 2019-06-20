package com.nooblabs.api

import com.nooblabs.models.NewArticle
import com.nooblabs.models.UpdateArticle
import com.nooblabs.service.ArticleService
import com.nooblabs.util.param
import com.nooblabs.util.userId
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.article(articleService: ArticleService) {

    authenticate {

        get("/articles/feed") {
            val params = call.parameters
            val filter = mapOf(
                "limit" to params["limit"],
                "offset" to params["offset"]
            )
            val articles = articleService.getFeedArticles(call.userId(), filter)
            call.respond(mapOf("articles" to articles))
        }

        post("/articles") {
            val newArticle = call.receive<NewArticle>()
            val article = articleService.createArticle(call.userId(), newArticle)
            call.respond(article)
        }

        put("/articles/{slug}") {
            val slug = call.param("slug")
            val updateArticle = call.receive<UpdateArticle>()
            val article = articleService.updateArticle(call.userId(), slug, updateArticle)
            call.respond(article)
        }

        post("/articles/{slug}/favorite") {
            val slug = call.param("slug")
            val article = articleService.changeFavorite(call.userId(), slug, favorite = true)
            call.respond(article)
        }

        delete("/articles/{slug}/favorite") {
            val slug = call.param("slug")
            val article = articleService.changeFavorite(call.userId(), slug, favorite = false)
            call.respond(article)
        }

        delete("/articles/{slug}") {
            val slug = call.param("slug")
            articleService.deleteArticle(call.userId(), slug)
            call.respond(HttpStatusCode.OK)
        }
    }

    authenticate(optional = true) {
        get("/articles") {
            val userId = call.principal<UserIdPrincipal>()?.name
            val params = call.parameters
            val filter = mapOf(
                "tag" to params["tag"],
                "author" to params["author"],
                "favorited" to params["favorited"],
                "limit" to params["limit"],
                "offset" to params["offset"]
            )
            call.application.environment.log.debug(filter.toString())
            val articles = articleService.getArticles(userId, filter)
            call.respond(mapOf("articles" to articles))
        }
    }

    get("/tags") {
        call.respond(articleService.getAllTags())
    }

}