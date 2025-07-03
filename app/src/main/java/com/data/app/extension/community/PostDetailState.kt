package com.data.app.extension.community

import com.data.app.data.Post
import com.data.app.data.response_dto.community.ResponsePostDetailDto

sealed class PostDetailState {
    data object Loading:PostDetailState()
    data class Success(val response: ResponsePostDetailDto):PostDetailState()
    data class Error(val message:String):PostDetailState()
}