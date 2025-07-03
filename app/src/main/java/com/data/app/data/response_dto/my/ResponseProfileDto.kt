package com.data.app.data.response_dto.my

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseProfileDto(
    @SerialName("userId")
    val userId:Int,
    @SerialName("name")
    val name: String,
    @SerialName("profileImage")
    val profileImage: String?,
    @SerialName("postCount")
    val postCount: Int,
    @SerialName("followerCount")
    val followerCount: Int,
    @SerialName("followingCount")
    val followingCount: Int,
    @SerialName("isFollowing")
    val isFollowing: Boolean,
    @SerialName("nationName")
    val nationName:String,
    @SerialName("nationNameKo")
    val nationNameKo:String
)