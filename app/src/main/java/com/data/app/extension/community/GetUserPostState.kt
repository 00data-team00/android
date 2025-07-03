package com.data.app.extension.community

import com.data.app.data.response_dto.community.ResponseTimeLineDto

sealed class GetUserPostState {
    object Loading : GetUserPostState()
    data class Success(val data: List<ResponseTimeLineDto.TimelinePostItem>) : GetUserPostState()
    data class Error(val message: String) : GetUserPostState()
}