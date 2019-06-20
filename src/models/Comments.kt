package com.nooblabs.models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

object Comments : IntIdTable() {
    val createdAt = datetime("createdAt").default(DateTime.now())
    val updatedAt = datetime("updatedAt").default(DateTime.now())
    val body = text("body")
    val author = reference("author", Users, onUpdate = ReferenceOption.CASCADE, onDelete = ReferenceOption.CASCADE)
}

object ArticleComment : Table() {
    val article = reference(
        "article", Articles,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    ).primaryKey(0)
    val comment = reference(
        "comment", Comments,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    ).primaryKey(1)
}

class Comment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Comment>(Comments)

    var createdAt by Comments.createdAt
    var updatedAt by Comments.updatedAt
    var body by Comments.body
    var author by Comments.author
}

data class CommentResponse(val comment: CommentResponse.Comment) {
    data class Comment(
        val id: Int,
        val createdAt: String,
        val updatedAt: String,
        val body: String,
        val author: ProfileResponse.Profile
    )
}

data class PostComment(val comment: PostComment.Comment) {
    data class Comment(val body: String)
}