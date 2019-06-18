package com.nooblabs.service

import com.nooblabs.models.RegisterUser
import com.nooblabs.models.UpdateUser
import com.nooblabs.models.User
import com.nooblabs.models.Users
import com.nooblabs.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.and

class AuthService {

    suspend fun register(registerUser: RegisterUser): User {
        return dbQuery {
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

    suspend fun loginAndGetUser(email: String, password: String): User? {
        return dbQuery {
            User.find { (Users.email eq email) and (Users.password eq password) }.firstOrNull()
        }
    }

    suspend fun updateUser(userEmail: String, updateUser: UpdateUser): User? {
        return dbQuery {
            val user = User.find { Users.email eq userEmail }.firstOrNull() ?: return@dbQuery null
            user.apply {
                email = updateUser.user.email ?: email
                password = updateUser.user.password ?: password
                username = updateUser.user.username ?: username
                image = updateUser.user.image ?: image
                bio = updateUser.user.bio ?: bio
            }
//            user.email = updateUser.user.email ?: user.email
//            user.password = updateUser.user.password ?: user.password
//            user.username = updateUser.user.username ?: user.username
//            user.image = updateUser.user.image ?: user.image
//            user.bio = updateUser.user.bio ?: user.bio
        }
    }


}