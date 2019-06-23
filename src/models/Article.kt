package com.nooblabs.models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import java.util.*

object Articles : UUIDTable() {
    val slug = varchar("slug", 255)
    var title = varchar("title", 255)
    val description = varchar("description", 255)
    val body = varchar("body", 255)
    val author = reference("author", Users)
    val createdAt = datetime("createdAt").default(DateTime.now())
    val updatedAt = datetime("updatedAt").default(DateTime.now())
}

object Tags : UUIDTable() {
    val tagName = varchar("tagName", 255).uniqueIndex()
}

object ArticleTags : Table() {
    val article = reference(
        "article",
        Articles,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    ).primaryKey(0)
    val tag = reference(
        "tag",
        Tags,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    ).primaryKey(1)
}

object FavoriteArticle : Table() {
    val article = reference("article", Articles).primaryKey(0)
    val user = reference("user", Users).primaryKey(1)
}

class Tag(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Tag>(Tags)

    var tag by Tags.tagName
}

class Article(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Article>(Articles) {
        fun generateSlug(title: String) = title.toLowerCase().replace(" ", "-")
    }

    var slug by Articles.slug
    var title by Articles.title
    var description by Articles.description
    var body by Articles.body
    var tags by Tag via ArticleTags
    var author by Articles.author
    var favoritedBy by User via FavoriteArticle
    var createdAt by Articles.createdAt
    var updatedAt by Articles.updatedAt
    var comments by Comment via ArticleComment
}

data class NewArticle(val article: NewArticle.Article) {
    data class Article(
        val title: String,
        val description: String,
        val body: String,
        val tagList: List<String> = emptyList()
    )
}

data class UpdateArticle(val article: UpdateArticle.Article) {
    data class Article(
        val title: String? = null,
        val description: String? = null,
        val body: String? = null
    )
}

data class ArticleResponse(val article: ArticleResponse.Article) {
    data class Article(
        val slug: String,
        val title: String,
        val description: String,
        val body: String,
        val tagList: List<String>,
        val createdAt: String,
        val updatedAt: String,
        val favorited: Boolean = false,
        val favoritesCount: Int = 0,
        val author: ProfileResponse.Profile
    )
}

data class MultipleArticlesResponse(val articles: List<ArticleResponse.Article>, val articlesCount: Int)

data class TagResponse(val tags: List<String>)