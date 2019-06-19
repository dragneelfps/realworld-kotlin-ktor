package com.nooblabs.service

import com.nooblabs.models.RegisterUser
import com.nooblabs.models.UpdateUser
import com.nooblabs.models.User
import com.nooblabs.models.Users
import com.nooblabs.service.DatabaseFactory.dbQuery
import com.nooblabs.util.UserDoesNotExists
import com.nooblabs.util.UserExists
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import java.util.*

class AuthService {

    suspend fun register(registerUser: RegisterUser): User {
        return dbQuery {
            val userInDatabase =
                User.find { (Users.username eq registerUser.user.username) or (Users.email eq registerUser.user.password) }
                    .firstOrNull()
            if (userInDatabase != null) throw UserExists()
            User.new {
                username = registerUser.user.username
                email = registerUser.user.email
                password = registerUser.user.password
            }
        }
    }

    suspend fun getAllUsers(): List<User> {
        return dbQuery {
            User.all().toList()
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return dbQuery {
            User.find { Users.email eq email }.firstOrNull()
        }
    }

    suspend fun getUserById(id: String): User {
        return dbQuery {
            User.findById(UUID.fromString(id)) ?: throw UserDoesNotExists()
        }
    }

    suspend fun loginAndGetUser(email: String, password: String): User {
        return dbQuery {
            User.find { (Users.email eq email) and (Users.password eq password) }.firstOrNull()
                ?: throw UserDoesNotExists()
        }
    }

    suspend fun updateUser(userId: String, updateUser: UpdateUser): User {
        return dbQuery {
            val user = User.find { Users.email eq userId }.firstOrNull() ?: throw UserDoesNotExists()
            user.apply {
                email = updateUser.user.email ?: email
                password = updateUser.user.password ?: password
                username = updateUser.user.username ?: username
                image = updateUser.user.image ?: image
                bio = updateUser.user.bio ?: bio
            }
        }
    }


}