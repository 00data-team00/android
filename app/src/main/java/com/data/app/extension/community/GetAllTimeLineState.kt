package com.data.app.extension.community

import com.data.app.data.response_dto.community.ResponseTimeLineDto

sealed class GetAllTimeLineState {
    object Loading : GetAllTimeLineState()
    data class Success(val data: List<ResponseTimeLineDto.Posts>) : GetAllTimeLineState()
    data class Error(val message: String) : GetAllTimeLineState()
}