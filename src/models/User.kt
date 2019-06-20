package com.nooblabs.models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import java.util.*

object Users : UUIDTable() {
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 255).uniqueIndex()
    val bio = text("bio").default("")
    val image = varchar("image", 255).nullable()
    val password = varchar("password", 255)
}

object Followings : UUIDTable() {
    val userId = reference("userId", Users)
    val followerId = reference("followerId", Users)
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var email by Users.email
    var username by Users.username
    var bio by Users.bio
    var image by Users.image
    var password by Users.password
    var followers by User.via(Followings.userId, Followings.followerId)
}

data class RegisterUser(val user: RegisterUser.User) {
    data class User(val email: String, val username: String, val password: String)
}

data class LoginUser(val user: LoginUser.User) {
    data class User(val email: String, val password: String)
}

data class UpdateUser(val user: UpdateUser.User) {
    data class User(
        val email: String? = null,
        val username: String? = null,
        val password: String? = null,
        val image: String? = null,
        val bio: String? = null
    )
}

data class UserResponse(val user: UserResponse.User) {
    data class User(
        val email: String,
        val token: String = "",
        val username: String,
        val bio: String,
        val image: String?
    )

    companion object {
        fun fromUser(user: com.nooblabs.models.User, token: String = ""): UserResponse = UserResponse(
            user = User(
                email = user.email,
                token = token,
                username = user.username,
                bio = user.bio,
                image = user.image
            )
        )
    }
}
