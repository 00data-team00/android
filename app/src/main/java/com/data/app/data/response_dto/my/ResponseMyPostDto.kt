package com.data.app.data.response_dto.my

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMyPostDto (
    @SerialName("posts")
    val posts:List<PostDto>
){
    @Serializable
    data class PostDto (
        @SerialName("id")
        val id: Int,
        @SerialName("authorId")
        val authorId: Int,
        @SerialName("authorName")
        val authorName: String,
        @SerialName("content")
        val content: String,
        @SerialName("imageUrl")
        val imageUrl: String?, // 이미지가 없을 수도 있다면 String? = null
        @SerialName("likeCount")
        val likeCount: Int,
        @SerialName("commentCount")
        val commentCount: Int,
        @SerialName("createdAt")
        val createdAt: String, // "YYYY-MM-DDTHH:mm:ss.SSSZ" 형식의 날짜/시간 문자열
        @SerialName("isLiked")
        val isLiked: Boolean
    )
}