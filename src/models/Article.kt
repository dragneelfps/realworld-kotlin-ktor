package com.nooblabs.models

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

object Articles : UUIDTable() {
    val slug = varchar("slug", 255)
    var title = varchar("title", 255)
    val description = varchar("description", 255)
    val body = varchar("body", 255)
    val author = reference("author", Users)
    val createdAt = timestamp("createdAt").default(Instant.now())
    val updatedAt = timestamp("updatedAt").default(Instant.now())
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
    )
    val tag = reference(
        "tag",
        Tags,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    override val primaryKey = PrimaryKey(article, tag)
}

object FavoriteArticle : Table() {
    val article = reference("article", Articles)
    val user = reference("user", Users)

    override val primaryKey = PrimaryKey(article, user)
}

class Tag(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Tag>(Tags)

    var tag by Tags.tagName
}

class Article(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Article>(Articles) {
        fun generateSlug(title: String) = title.lowercase(Locale.US).replace(" ", "-")
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

data class NewArticle(val article: Article) {
    data class Article(
        val title: String,
        val description: String,
        val body: String,
        val tagList: List<String> = emptyList()
    )
}

data class UpdateArticle(val article: Article) {
    data class Article(
        val title: String? = null,
        val description: String? = null,
        val body: String? = null
    )
}

data class ArticleResponse(val article: Article) {
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