package com.nooblabs.service

import com.nooblabs.models.ProfileResponse
import com.nooblabs.models.User
import com.nooblabs.models.Users
import com.nooblabs.service.DatabaseFactory.dbQuery
import com.nooblabs.util.AuthenticationException
import com.nooblabs.util.UserDoesNotExists
import org.jetbrains.exposed.sql.SizedCollection
import java.util.*

class ProfileService() {
    suspend fun getProfile(username: String, currentUserId: String? = null): ProfileResponse {
        return dbQuery {
            val toUser = getUserByUsername(username)
            val toProfile = getProfileByUser(toUser)
            toProfile.profile ?: return@dbQuery toProfile
            currentUserId ?: return@dbQuery toProfile
            val fromUser = getUserById(currentUserId)
            val follows = fromUser.followings.any { it.id == toUser.id }
            toProfile.copy(profile = toProfile.profile.copy(following = follows))
        }
    }

    suspend fun followUser(toUserName: String, fromUserId: String): ProfileResponse {
        dbQuery {
            val toUser = getUserByUsername(toUserName)
            val fromUser = getUserById(fromUserId)
            fromUser.followings = SizedCollection(fromUser.followings.plus(toUser).toList())
        }
        return getProfile(toUserName, fromUserId)
    }

    suspend fun unfollowUser(toUserName: String, fromUserId: String): ProfileResponse {
        dbQuery {
            val toUser = getUserByUsername(toUserName) ?: error("invalid username")
            val fromUser = getUserById(fromUserId) ?: throw AuthenticationException()
            fromUser.followings = SizedCollection(fromUser.followings.minus(toUser).toList())
        }
        return getProfile(toUserName, fromUserId)
    }

    fun getUserById(id: String) =
        User.find { Users.id eq UUID.fromString(id) }.firstOrNull() ?: throw UserDoesNotExists()

    fun getUserByUsername(username: String) =
        User.find { Users.username eq username }.firstOrNull() ?: throw UserDoesNotExists()

    fun getProfileByUser(user: User) =
        ProfileResponse(profile = user.run { ProfileResponse.Profile(username, bio, image) })
}