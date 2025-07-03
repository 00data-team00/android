package com.data.app.extension.community

import com.data.app.data.response_dto.my.ResponseMyPostDto

sealed class WritePostState {
    data object Loading:WritePostState()
    data class Success(val response: ResponseMyPostDto.PostDto):WritePostState()
    data class Error(val message:String):WritePostState()
}