package com.nooblabs.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Comments : IntIdTable() {
    val createdAt = timestamp("createdAt").default(Instant.now())
    val updatedAt = timestamp("updatedAt").default(Instant.now())
    val body = text("body")
    val author = reference("author", Users, onUpdate = ReferenceOption.CASCADE, onDelete = ReferenceOption.CASCADE)
}

object ArticleComment : Table() {
    val article = reference(
        "article", Articles,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val comment = reference(
        "comment", Comments,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    override val primaryKey = PrimaryKey(article, comment)
}

class Comment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Comment>(Comments)

    var createdAt by Comments.createdAt
    var updatedAt by Comments.updatedAt
    var body by Comments.body
    var author by Comments.author
}

data class CommentResponse(val comment: Comment) {
    data class Comment(
        val id: Int,
        val createdAt: String,
        val updatedAt: String,
        val body: String,
        val author: ProfileResponse.Profile
    )
}

data class PostComment(val comment: Comment) {
    data class Comment(val body: String)
}