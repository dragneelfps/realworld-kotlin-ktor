package com.nooblabs.service

import com.nooblabs.models.ProfileResponse
import com.nooblabs.models.User
import com.nooblabs.service.DatabaseFactory.dbQuery
import com.nooblabs.util.UserDoesNotExists
import org.jetbrains.exposed.sql.SizedCollection

class ProfileService() {
    suspend fun getProfile(username: String, currentUserId: String? = null): ProfileResponse {
        return dbQuery {
            val toUser = getUserByUsername(username) ?: return@dbQuery getProfileByUser(null, false)
            currentUserId ?: return@dbQuery getProfileByUser(toUser)
            val fromUser = getUser(currentUserId)
            val follows = isFollower(toUser, fromUser)
            getProfileByUser(toUser, follows)
        }
    }

    suspend fun changeFollowStatus(toUserName: String, fromUserId: String, follow: Boolean): ProfileResponse {
        dbQuery {
            val toUser = getUserByUsername(toUserName) ?: throw UserDoesNotExists()
            val fromUser = getUser(fromUserId)
            if (follow) {
                addFollower(toUser, fromUser)
            } else {
                removeFollower(toUser, fromUser)
            }
        }
        return getProfile(toUserName, fromUserId)
    }

    private fun addFollower(user: User, newFollower: User) {
        if (!isFollower(user, newFollower)) {
            user.followers = SizedCollection(user.followers.plus(newFollower))
        }
    }

    private fun removeFollower(user: User, newFollower: User) {
        if (isFollower(user, newFollower)) {
            user.followers = SizedCollection(user.followers.minus(newFollower))
        }
    }

}

fun isFollower(user: User, follower: User?) = if (follower != null) user.followers.any { it == follower } else false

fun getProfileByUser(user: User?, following: Boolean = false) =
    ProfileResponse(profile = user?.run { ProfileResponse.Profile(username, bio, image, following) })