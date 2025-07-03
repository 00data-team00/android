package com.data.app.data.response_dto.community

import com.data.app.data.response_dto.my.ResponseMyPostDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseTimeLineDto (
    @SerialName("posts")
    val posts:List<Posts>
){
    @Serializable
    data class Posts (
        @SerialName("post")
        val post: ResponseMyPostDto.PostDto,
        @SerialName("authorProfile")
        val authorProfile:AuthorProfileDto

    ){
        @Serializable
        data class AuthorProfileDto (
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
            @SerialName("isLiked")
            val isLiked: Boolean, // 이 필드가 게시물에 대한 '좋아요'인지, 작성자에 대한 '좋아요'인지 의미 확인 필요
            @SerialName("nationName")
            val nationName: String,
            @SerialName("nationNameKo")
            val nationNameKo: String
        )
    }
}