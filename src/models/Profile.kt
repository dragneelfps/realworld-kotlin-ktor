package com.nooblabs.models

data class ProfileResponse(val profile: ProfileResponse.Profile? = null) {
    data class Profile(val username: String, val bio: String, val image: String?, val following: Boolean = false)
}