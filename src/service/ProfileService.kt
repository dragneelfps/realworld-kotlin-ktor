package com.nooblabs.service

import com.nooblabs.models.ProfileResponse
import com.nooblabs.models.User
import com.nooblabs.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SizedCollection

class ProfileService() {
    suspend fun getProfile(username: String, currentUserId: String? = null): ProfileResponse {
        return dbQuery {
            val toUser = getUserByUsername(username)
            currentUserId ?: return@dbQuery getProfileByUser(toUser)
            val fromUser = getUser(currentUserId)
            val follows = fromUser.followings.any { it.id == toUser.id }
            getProfileByUser(toUser, follows)
        }
    }

    suspend fun followUser(toUserName: String, fromUserId: String): ProfileResponse {
        dbQuery {
            val toUser = getUserByUsername(toUserName)
            val fromUser = getUser(fromUserId)
            fromUser.followings = SizedCollection(fromUser.followings.plus(toUser).toList())
        }
        return getProfile(toUserName, fromUserId)
    }

    suspend fun unfollowUser(toUserName: String, fromUserId: String): ProfileResponse {
        dbQuery {
            val toUser = getUserByUsername(toUserName)
            val fromUser = getUser(fromUserId)
            fromUser.followings = SizedCollection(fromUser.followings.minus(toUser).toList())
        }
        return getProfile(toUserName, fromUserId)
    }

}

fun getProfileByUser(user: User, following: Boolean = false) =
    ProfileResponse(profile = user.run { ProfileResponse.Profile(username, bio, image, following) })