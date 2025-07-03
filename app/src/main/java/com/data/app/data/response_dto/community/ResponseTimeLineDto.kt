package com.data.app.data.response_dto.community

import com.data.app.data.response_dto.my.ResponseMyPostDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseTimeLineDto (
    @SerialName("posts")
    val posts: List<TimelinePostItem> // 내부 클래스 이름 변경 또는 Posts 유지 가능
) {
    @Serializable
    data class TimelinePostItem ( // 클래스 이름을 Posts에서 좀 더 명확하게 변경 (선택 사항)
        @SerialName("post")
        val post: PostData, // 내부 클래스 또는 별도 파일 DTO 사용
        @SerialName("authorProfile")
        val authorProfile: AuthorProfileData // 내부 클래스 또는 별도 파일 DTO 사용
    )

    @Serializable
    data class PostData ( // 'post' 객체에 해당하는 데이터 클래스
        @SerialName("id")
        val id: Int,
        @SerialName("authorId")
        val authorId: Int,
        @SerialName("authorName")
        val authorName: String,
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
        @SerialName("isLiked") // 새로 추가된 필드
        val isLiked: Boolean
    )

    @Serializable
    data class AuthorProfileData ( // 'authorProfile' 객체에 해당하는 데이터 클래스 (기존 AuthorProfileDto 와 내용 동일)
        @SerialName("name")
        val name: String,
        @SerialName("profileImage")
        val profileImage: String?, // Nullable 처리 유지
        @SerialName("postCount")
        val postCount: Int,
        @SerialName("followerCount")
        val followerCount: Int,
        @SerialName("followingCount")
        val followingCount: Int,
        @SerialName("isFollowing")
        val isFollowing: Boolean,
        @SerialName("isLiked")
        val isLiked: Boolean,
        @SerialName("nationName")
        val nationName: String,
        @SerialName("nationNameKo")
        val nationNameKo: String
    )
}