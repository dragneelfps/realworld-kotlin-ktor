package com.nooblabs.service

import com.nooblabs.models.Comment
import com.nooblabs.models.CommentResponse
import com.nooblabs.models.PostComment
import com.nooblabs.service.DatabaseFactory.dbQuery
import com.nooblabs.util.AuthorizationException
import com.nooblabs.util.CommentNotFound
import org.jetbrains.exposed.sql.SizedCollection

class CommentService {

    suspend fun addComment(userId: String, slug: String, postComment: PostComment): CommentResponse {
        return dbQuery {
            val user = getUser(userId)
            val article = getArticleBySlug(slug)
            val comment = Comment.new {
                body = postComment.comment.body
                author = user.id
            }
            article.comments = SizedCollection(article.comments.plus(comment))
            getCommentResponse(comment, userId)
        }
    }

    suspend fun getComments(userId: String?, slug: String): List<CommentResponse.Comment> {
        return dbQuery {
            val article = getArticleBySlug(slug)
            article.comments.map { comment -> getCommentResponse(comment, userId).comment }
        }
    }

    suspend fun deleteComment(userId: String, slug: String, commentId: Int) {
        dbQuery {
            val user = getUser(userId)
            val article = getArticleBySlug(slug)
            val comment = getCommentById(commentId)
            if (comment.author != user.id || article.comments.none { it == comment }) throw AuthorizationException()
            comment.delete()
        }
    }

}

fun getCommentResponse(comment: Comment, userId: String?): CommentResponse {
    val author = getUser(comment.author.toString())
    val currentUser = if (userId != null) getUser(userId) else null
    val following = if (currentUser != null) currentUser.followings.any { it == author } else false
    val authorProfile = getProfileByUser(author, following)
    return CommentResponse(
        comment = CommentResponse.Comment(
            id = comment.id.value,
            createdAt = comment.createdAt.toString(),
            updatedAt = comment.updatedAt.toString(),
            body = comment.body,
            author = authorProfile.profile!!
        )
    )
}

fun getCommentById(id: Int) = Comment.findById(id) ?: throw CommentNotFound()