package com.data.app.extension.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtherProfileState (
    @SerialName("name")
    val name: String,
    @SerialName("profileImage")
    val profileImage: String?, // Nullable 고려: String? = null
    @SerialName("postCount")
    val postCount: Int,
    @SerialName("followerCount")
    val followerCount: Int,
    @SerialName("followingCount")
    val followingCount: Int,
    @SerialName("isFollowing")
    val isFollowing: Boolean,
    @SerialName("nationName")
    val nationName: String,
    @SerialName("nationNameKo")
    val nationNameKo: String
)