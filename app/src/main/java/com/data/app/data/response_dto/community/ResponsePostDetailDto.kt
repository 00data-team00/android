package com.data.app.data.response_dto.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostDetailDto (
    @SerialName("id")
    val id: Int,
    @SerialName("authorId")
    val authorId: Int,
    @SerialName("authorName")
    val authorName: String,
    @SerialName("authorProfileImage")
    val authorProfileImage: String?, // Nullable 고려: String? = null
    @SerialName("content")
    val content: String,
    @SerialName("imageUrl")
    val imageUrl: String?, // Nullable 고려: String? = null
    @SerialName("likeCount")
    val likeCount: Int,
    @SerialName("commentCount")
    val commentCount: Int,
    @SerialName("createdAt")
    val createdAt: String, // "YYYY-MM-DDTHH:mm:ss.SSSZ"
    @SerialName("comments")
    val comments: List<CommentDto>
) {
    @Serializable
    data class CommentDto(
        @SerialName("id")
        val id: Int,
        @SerialName("postId")
        val postId: Int,
        @SerialName("commenterId")
        val commenterId: Int,
        @SerialName("commenterName")
        val commenterName: String,
        @SerialName("content")
        val content: String,
        @SerialName("createdAt")
        val createdAt: String // "YYYY-MM-DDTHH:mm:ss.SSSZ"
    )
}