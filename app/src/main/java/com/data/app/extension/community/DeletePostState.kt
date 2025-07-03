package com.data.app.extension.community

import com.data.app.data.response_dto.community.ResponseDeletePostDto
import com.data.app.data.response_dto.community.ResponseTimeLineDto

sealed class DeletePostState {
    object Loading : DeletePostState()
    data class Success(val data: ResponseDeletePostDto) : DeletePostState()
    data class Error(val message: String) : DeletePostState()
}