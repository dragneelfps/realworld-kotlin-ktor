package com.nooblabs.service

import com.nooblabs.models.ProfileResponse
import com.nooblabs.models.User
import com.nooblabs.util.UserDoesNotExists
import org.jetbrains.exposed.sql.SizedCollection

interface IProfileService {
    suspend fun getProfile(username: String, currentUserId: String? = null): ProfileResponse

    suspend fun changeFollowStatus(toUserName: String, fromUserId: String, follow: Boolean): ProfileResponse
}

class ProfileService(private val databaseFactory: IDatabaseFactory) : IProfileService {
    override suspend fun getProfile(username: String, currentUserId: String?): ProfileResponse {
        return databaseFactory.dbQuery {
            val toUser = getUserByUsername(username) ?: return@dbQuery getProfileByUser(null, false)
            currentUserId ?: return@dbQuery getProfileByUser(toUser)
            val fromUser = getUser(currentUserId)
            val follows = isFollower(toUser, fromUser)
            getProfileByUser(toUser, follows)
        }
    }

    override suspend fun changeFollowStatus(toUserName: String, fromUserId: String, follow: Boolean): ProfileResponse {
        databaseFactory.dbQuery {
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