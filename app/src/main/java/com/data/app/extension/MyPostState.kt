package com.data.app.extension

import com.data.app.data.response_dto.ResponseMyPostDto
import com.data.app.data.response_dto.ResponseProfileDto

sealed class MyPostState {
    object Loading : MyPostState()
    data class Success(val response: ResponseMyPostDto) : MyPostState()
    data class Error(val message: String) : MyPostState()
}