package com.nooblabs.service

import com.nooblabs.models.RegisterUser
import com.nooblabs.models.UpdateUser
import com.nooblabs.models.User
import com.nooblabs.models.Users
import com.nooblabs.util.UserDoesNotExists
import com.nooblabs.util.UserExists
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import java.util.UUID

interface IAuthService {
    suspend fun register(registerUser: RegisterUser): User

    suspend fun getAllUsers(): List<User>

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserById(id: String): User

    suspend fun loginAndGetUser(email: String, password: String): User

    suspend fun updateUser(userId: String, updateUser: UpdateUser): User
}

class AuthService(private val databaseFactory: IDatabaseFactory) : IAuthService {

    override suspend fun register(registerUser: RegisterUser): User {
        return databaseFactory.dbQuery {
            val userInDatabase =
                User.find { (Users.username eq registerUser.user.username) or (Users.email eq registerUser.user.email) }
                    .firstOrNull()
            if (userInDatabase != null) throw UserExists()
            User.new {
                username = registerUser.user.username
                email = registerUser.user.email
                password = registerUser.user.password
            }
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return databaseFactory.dbQuery {
            User.all().toList()
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return databaseFactory.dbQuery {
            User.find { Users.email eq email }.firstOrNull()
        }
    }

    override suspend fun getUserById(id: String): User {
        return databaseFactory.dbQuery {
            getUser(id)
        }
    }

    override suspend fun loginAndGetUser(email: String, password: String): User {
        return databaseFactory.dbQuery {
            User.find { (Users.email eq email) and (Users.password eq password) }.firstOrNull()
                ?: throw UserDoesNotExists()
        }
    }

    override suspend fun updateUser(userId: String, updateUser: UpdateUser): User {
        return databaseFactory.dbQuery {
            val user = getUser(userId)
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

fun getUser(id: String) = User.findById(UUID.fromString(id)) ?: throw UserDoesNotExists()

fun getUserByUsername(username: String) = User.find { Users.username eq username }.firstOrNull()