package com.nooblabs.service

import com.nooblabs.models.*
import com.nooblabs.service.DatabaseFactory.dbQuery
import com.nooblabs.util.ArticleDoesNotExist
import com.nooblabs.util.AuthorizationException
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SortOrder
import org.joda.time.DateTime

class ArticleService {

    suspend fun createArticle(userId: String, newArticle: NewArticle): ArticleResponse {
        return dbQuery {
            val user = getUser(userId)
            val article = Article.new {
                title = newArticle.article.title
                slug = newArticle.article.title.toLowerCase().replace(" ", "-")
                description = newArticle.article.description
                body = newArticle.article.body
                author = user.id
            }
            val tags = newArticle.article.tagList.map { tag -> getOrCreateTag(tag) }
            article.tags = SizedCollection(tags)
            getArticleResponse(article, user)
        }
    }

    suspend fun updateArticle(userId: String, slug: String, updateArticle: UpdateArticle): ArticleResponse {
        return dbQuery {
            val user = getUser(userId)
            val article = getArticleBySlug(slug)
            if (!isArticleAuthor(article, user)) throw AuthorizationException()
            if (updateArticle.article.title != null) {
                article.slug = updateArticle.article.title.toLowerCase().replace(" ", "-")
                article.title = updateArticle.article.title
                article.updatedAt = DateTime.now()
            }
            getArticleResponse(article, user)
        }
    }

    suspend fun getArticles(userId: String? = null, filter: Map<String, String?>): List<ArticleResponse.Article> {
        return dbQuery {
            val user = if (userId != null) getUser(userId) else null
            getAllArticles(
                currentUser = user,
                tag = filter["tag"],
                authorUserName = filter["author"],
                favoritedByUserName = filter["favorited"],
                limit = filter["limit"]?.toInt() ?: 20,
                offset = filter["offset"]?.toInt() ?: 0
            )
        }
    }

    suspend fun getFeedArticles(userId: String, filter: Map<String, String?>): List<ArticleResponse.Article> {
        return dbQuery {
            val user = getUser(userId)
            getAllArticles(
                currentUser = user,
                limit = filter["limit"]?.toInt() ?: 20,
                offset = filter["offset"]?.toInt() ?: 0,
                followedBy = true
            )
        }
    }

    suspend fun changeFavorite(userId: String, slug: String, favorite: Boolean): ArticleResponse {
        return dbQuery {
            val user = getUser(userId)
            val article = getArticleBySlug(slug)
            if (favorite) {
                favoriteArticle(article, user)
            } else {
                unfavoriteArticle(article, user)
            }
            getArticleResponse(article, user)
        }
    }

    suspend fun deleteArticle(userId: String, slug: String) {
        dbQuery {
            val user = getUser(userId)
            val article = getArticleBySlug(slug)
            if (!isArticleAuthor(article, user)) throw AuthorizationException()
            article.delete()
        }
    }

    suspend fun getAllTags(): TagResponse {
        return dbQuery {
            val tags = Tag.all().map { it.tag }
            TagResponse(tags)
        }
    }

    private fun getAllArticles(
        currentUser: User? = null,
        tag: String? = null,
        authorUserName: String? = null,
        favoritedByUserName: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        followedBy: Boolean = false
    ): List<ArticleResponse.Article> {
        val author = if (authorUserName != null) getUserByUsername(authorUserName) else null
        val articles = Article.find {
            if (author != null) (Articles.author eq author.id) else Op.TRUE
        }.limit(limit, offset).orderBy(Articles.createdAt to SortOrder.DESC)
        val filteredArticles = articles.filter { article ->
            if (favoritedByUserName != null) {
                val favoritedByUser = getUserByUsername(favoritedByUserName)
                article.favoritedBy.any { it == favoritedByUser }
            } else {
                true
            }
                    &&
                    if (tag != null) {
                        article.tags.any { it.tag == tag }
                    } else {
                        true
                    }
                    &&
                    if (followedBy) {
                        val articleAuthor = getUser(article.author.toString())
                        articleAuthor.followings.any { it == currentUser!! }
                    } else {
                        true
                    }
        }
        return filteredArticles.map {
            getArticleResponse(it, currentUser).article
        }
    }

    private fun favoriteArticle(article: Article, user: User) {
        if (article.favoritedBy.none { it == user }) {
            article.favoritedBy = SizedCollection(article.favoritedBy.plus(user))
        }
    }

    private fun unfavoriteArticle(article: Article, user: User) {
        if (article.favoritedBy.any { it == user }) {
            article.favoritedBy = SizedCollection(article.favoritedBy.minus(user))
        }
    }
}

fun getArticleBySlug(slug: String) =
    Article.find { Articles.slug eq slug }.firstOrNull() ?: throw ArticleDoesNotExist(slug)

fun getOrCreateTag(tagName: String) =
    Tag.find { Tags.tagName eq tagName }.firstOrNull() ?: Tag.new { this.tag = tagName }

fun getArticleResponse(article: Article, currentUser: User? = null): ArticleResponse {
    val author = getUser(article.author.toString())
    val tagList = article.tags.map { it.tag }
    val favoriteCount = article.favoritedBy.count()
    val favorited =
        if (currentUser != null) article.favoritedBy.any { it == currentUser } else false
    val following = author.followings.any { it == currentUser }
    val authorProfile = getProfileByUser(getUser(article.author.toString()), following).profile!!
    return ArticleResponse(
        article = ArticleResponse.Article(
            slug = article.slug,
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = tagList,
            createdAt = article.createdAt.toString(),
            updatedAt = article.updatedAt.toString(),
            favorited = favorited,
            favoritesCount = favoriteCount,
            author = authorProfile
        )
    )
}

fun isArticleAuthor(article: Article, user: User) = article.author == user.id