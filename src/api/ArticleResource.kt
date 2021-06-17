package com.nooblabs.api

import com.nooblabs.models.MultipleArticlesResponse
import com.nooblabs.models.NewArticle
import com.nooblabs.models.UpdateArticle
import com.nooblabs.service.IArticleService
import com.nooblabs.util.param
import com.nooblabs.util.userId
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put

fun Route.article(articleService: IArticleService) {

    authenticate {

        /*
            Feed Articles
            GET /api/articles/feed
         */
        get("/articles/feed") {
            val params = call.parameters
            val filter = mapOf(
                "limit" to params["limit"],
                "offset" to params["offset"]
            )
            val articles = articleService.getFeedArticles(call.userId(), filter)
            call.respond(MultipleArticlesResponse(articles, articles.size))
        }

        /*
            Create Article
            POST /api/articles
         */
        post("/articles") {
            val newArticle = call.receive<NewArticle>()
            val article = articleService.createArticle(call.userId(), newArticle)
            call.respond(article)
        }

        /*
            Update Article
            PUT /api/articles/:slug
         */
        put("/articles/{slug}") {
            val slug = call.param("slug")
            val updateArticle = call.receive<UpdateArticle>()
            val article = articleService.updateArticle(call.userId(), slug, updateArticle)
            call.respond(article)
        }

        /*
            Favorite Article
            POST /api/articles/:slug/favorite
         */
        post("/articles/{slug}/favorite") {
            val slug = call.param("slug")
            val article = articleService.changeFavorite(call.userId(), slug, favorite = true)
            call.respond(article)
        }

        /*
            Unfavorite Article
            DELETE /api/articles/:slug/favorite
         */
        delete("/articles/{slug}/favorite") {
            val slug = call.param("slug")
            val article = articleService.changeFavorite(call.userId(), slug, favorite = false)
            call.respond(article)
        }

        /*
            Delete Article
            DELETE /api/articles/:slug
         */
        delete("/articles/{slug}") {
            val slug = call.param("slug")
            articleService.deleteArticle(call.userId(), slug)
            call.respond(HttpStatusCode.OK)
        }
    }

    authenticate(optional = true) {

        /*
            List Articles
            GET /api/articles
         */
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
            val articles = articleService.getArticles(userId, filter)
            call.respond(MultipleArticlesResponse(articles, articles.size))
        }

    }

    /*
        Get Article
        GET /api/articles/:slug
     */
    get("/articles/{slug}") {
        val slug = call.param("slug")
        val article = articleService.getArticle(slug)
        call.respond(article)
    }

    /*
        Get Tags
        GET /api/tags
     */
    get("/tags") {
        call.respond(articleService.getAllTags())
    }

}