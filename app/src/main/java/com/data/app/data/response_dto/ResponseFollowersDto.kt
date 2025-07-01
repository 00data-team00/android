package com.data.app.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseFollowersDto (
    @SerialName("followList")
    val messages:List<Follower>
){
    @Serializable
    data class Follower(
        @SerialName("userId")
        val userId:Int,
        @SerialName("name")
        val name:String,
        @SerialName("profileImage")
        val profileImage:String,
        @SerialName("isFollowing")
        val isFollowing:Boolean
    )
}