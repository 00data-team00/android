package com.data.app.extension.my

import com.data.app.data.response_dto.my.ResponseMyPostDto

sealed class MyPostState {
    object Loading : MyPostState()
    data class Success(val response: ResponseMyPostDto) : MyPostState()
    data class Error(val message: String) : MyPostState()
}