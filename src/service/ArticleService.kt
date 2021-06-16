package com.nooblabs.service

import com.nooblabs.models.Article
import com.nooblabs.models.ArticleResponse
import com.nooblabs.models.Articles
import com.nooblabs.models.NewArticle
import com.nooblabs.models.Tag
import com.nooblabs.models.TagResponse
import com.nooblabs.models.Tags
import com.nooblabs.models.UpdateArticle
import com.nooblabs.models.User
import com.nooblabs.service.DatabaseFactory.dbQuery
import com.nooblabs.util.ArticleDoesNotExist
import com.nooblabs.util.AuthorizationException
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SortOrder
import java.time.Instant

class ArticleService {

    suspend fun createArticle(userId: String, newArticle: NewArticle): ArticleResponse {
        return dbQuery {
            val user = getUser(userId)
            val article = Article.new {
                title = newArticle.article.title
                slug = Article.generateSlug(newArticle.article.title)
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
                article.slug = Article.generateSlug(updateArticle.article.title)
                article.title = updateArticle.article.title
                article.updatedAt = Instant.now()
            }
            getArticleResponse(article, user)
        }
    }

    suspend fun getArticle(slug: String): ArticleResponse {
        return dbQuery {
            val article = getArticleBySlug(slug)
            getArticleResponse(article)
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
                follows = true
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
        follows: Boolean = false
    ): List<ArticleResponse.Article> {
        val author = if (authorUserName != null) getUserByUsername(authorUserName) else null
        val articles = Article.find {
            if (author != null) (Articles.author eq author.id) else Op.TRUE
        }.limit(limit, offset.toLong()).orderBy(Articles.createdAt to SortOrder.DESC)
        val filteredArticles = articles.filter { article ->
            if (favoritedByUserName != null) {
                val favoritedByUser = getUserByUsername(favoritedByUserName)
                isFavoritedArticle(article, favoritedByUser)
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
                    if (follows) {
                        val articleAuthor = getUser(article.author.toString())
                        isFollower(articleAuthor, currentUser)
                    } else {
                        true
                    }
        }
        return filteredArticles.map {
            getArticleResponse(it, currentUser).article
        }
    }

    private fun favoriteArticle(article: Article, user: User) {
        if (!isFavoritedArticle(article, user)) {
            article.favoritedBy = SizedCollection(article.favoritedBy.plus(user))
        }
    }

    private fun unfavoriteArticle(article: Article, user: User) {
        if (isFavoritedArticle(article, user)) {
            article.favoritedBy = SizedCollection(article.favoritedBy.minus(user))
        }
    }
}

fun isFavoritedArticle(article: Article, user: User?) =
    if (user != null) article.favoritedBy.any { it == user } else false

fun getArticleBySlug(slug: String) =
    Article.find { Articles.slug eq slug }.firstOrNull() ?: throw ArticleDoesNotExist(slug)

fun getOrCreateTag(tagName: String) =
    Tag.find { Tags.tagName eq tagName }.firstOrNull() ?: Tag.new { this.tag = tagName }

fun getArticleResponse(article: Article, currentUser: User? = null): ArticleResponse {
    val author = getUser(article.author.toString())
    val tagList = article.tags.map { it.tag }
    val favoriteCount = article.favoritedBy.count()
    val favorited = isFavoritedArticle(article, currentUser)
    val following = isFollower(author, currentUser)
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
            favoritesCount = favoriteCount.toInt(),
            author = authorProfile
        )
    )
}

fun isArticleAuthor(article: Article, user: User) = article.author == user.id