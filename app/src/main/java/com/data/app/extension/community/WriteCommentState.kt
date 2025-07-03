package com.data.app.extension.community

import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.data.response_dto.my.ResponseMyPostDto

sealed class WriteCommentState {
    data object Loading:WriteCommentState()
    data class Success(val response: ResponsePostDetailDto.CommentDto):WriteCommentState()
    data class Error(val message:String):WriteCommentState()
}